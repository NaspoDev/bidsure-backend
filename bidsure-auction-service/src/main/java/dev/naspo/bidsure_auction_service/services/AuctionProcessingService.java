package dev.naspo.bidsure_auction_service.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.naspo.bidsure_auction_service.dto.OrderDTO;
import dev.naspo.bidsure_auction_service.models.*;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Responsible for processing auctions when they expire.
@Service
public class AuctionProcessingService {

    @Autowired
    private HibernateManager hibernateManager;

    @Autowired
    private ObjectMapper objectMapper;

    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final HttpClient client = HttpClient.newHttpClient();

    // API URLS
    private final String WINNING_BID_URL_BASE = "http://localhost:8080/bids/winning-bid/auction/";
    private final String PAYMENT_METHOD_URL_BASE = "http://localhost:8080/payments/payment-methods/user/";
    private final String PAYMENT_PROCESSING_UL = "http://localhost:8080/payments/processing";
    private final String CREATE_ORDER_URL = "http://localhost:8080/orders";

    // Checks every minute for auctions that are expired and not-processed.
    @Scheduled(fixedRate = 60000)
    private void findExpiredAuctions() {
        try (Session session = hibernateManager.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Query for expired auctions that have not been processed.
            List<Auction> auctions = session.createQuery(
                            "from Auction a where a.endTime <= :currentTime and a.processed = false", Auction.class)
                    .setParameter("currentTime", LocalDateTime.now())
                    .getResultList();

            session.getTransaction().commit();

            // For each auction, call to process it.
            for (Auction auction : auctions) {
                executor.submit(() -> processAuction(auction));
            }
        }
    }

    // Processes an action.
    // This involves charging the winner and creating an order once payment has been completed.
    private void processAuction(Auction auction) {
        Bid winningBid = getWinningBid(auction);
        PaymentMethod paymentMethod = getPaymentMethod(auction.getSeller());
        Address address = getAddress(auction.getSeller());

        if (winningBid != null && paymentMethod != null && address != null) {
            createOrder(auction, winningBid, paymentMethod, address);
        }
    }

    // Makes a network request for the winning bid of an auction.
    private Bid getWinningBid(Auction auction) {
        // Build the request.
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(WINNING_BID_URL_BASE + auction.getId()))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.err.println("Request to get winning bid failed!");
            } else {
                return objectMapper.readValue(response.body(), Bid.class);
            }
        } catch (Exception e) {
            System.err.println("Request to get winning bid failed!");
        }
        return null;
    }

    // Make a network request to get a user's payment method.
    private PaymentMethod getPaymentMethod(User user) {
        // Build the request.
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(PAYMENT_METHOD_URL_BASE + user.getId()))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.err.println("Request to get payment method failed!");
            } else {
                // Currently just using the first payment method we get, no preference set.
                List<PaymentMethod> paymentMethods = objectMapper.readValue(response.body(), new TypeReference<List<PaymentMethod>>() {});
                return paymentMethods.getFirst();
            }
        } catch (Exception e) {
            System.err.println("Request to get payment method failed!");
        }
        return null;
    }

    // Make a network request to get a user's address.
    private Address getAddress(User user) {
        // Build the request.
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/users/" + user.getId() + "/addresses"))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.err.println("Request to get address failed!");
            } else {
                // Currently just using the first address we get, no preference set.
                List<Address> addresses = objectMapper.readValue(response.body(), new TypeReference<List<Address>>() {});
                return addresses.getFirst();
            }
        } catch (Exception e) {
            System.err.println("Request to get address failed!");
        }
        return null;
    }

    // Call to process payment, and if success, create order.
    private void createOrder(Auction auction, Bid winningBid, PaymentMethod paymentMethod, Address address) {
        // 1. Call to process payment.
        // Add tax to get total.
        BigDecimal total = winningBid.getBidAmount().multiply(BigDecimal.valueOf(1.13));
        // Build transaction.
        Transaction transaction = new Transaction(total, paymentMethod);
        // Call to process payment.
        HttpResponse<String> paymentProcessingResponse = processPayment(transaction);
        // If the payment failed, exit.
        if (paymentProcessingResponse == null ||
                (paymentProcessingResponse.statusCode() != 200 && paymentProcessingResponse.statusCode() != 201)) {
            System.err.println("Failed to process payment!");
            return;
        }

        // 2. Create order.
        // Construct the JSON Body.
        OrderDTO orderDTO = new OrderDTO(
                winningBid.getBidAmount(),
                auction.getSeller().getId(),
                paymentMethod.getId(),
                address.getId(),
                auction.getId()
        );

        String jsonBody;
        try {
            jsonBody = objectMapper.writeValueAsString(orderDTO);
        } catch (JsonProcessingException e) {
            System.err.println("Error processing JSON body for order request.");
            return;
        }

        // Build the request.
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(CREATE_ORDER_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200 || response.statusCode() == 201) {
                markAuctionAsProcessed(auction);
            } else {
                System.err.println("Request to create the order failed!");
            }
        } catch (Exception e) {
            System.err.println("Request to create the order failed!");
        }
    }

    // Make a network request to process a payment.
    private HttpResponse<String> processPayment(Transaction transaction) {
        String jsonBody;
        try {
            jsonBody = objectMapper.writeValueAsString(transaction);
        } catch (JsonProcessingException e) {
            System.err.println("Error processing JSON body for payment processing request.");
            return null;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(PAYMENT_PROCESSING_UL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.err.println("Request to process payment failed!");
        }
        return null;
    }

    // Marks an auction as processed in the database.
    private void markAuctionAsProcessed(Auction auction) {
        try (Session session = hibernateManager.getSessionFactory().openSession()) {
            session.beginTransaction();

            auction.setProcessed(true);
            session.merge(auction);

            session.getTransaction().commit();
        }
    }
}
