package dev.naspo.bidsure_order_service.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.naspo.bidsure_order_service.models.Address;
import dev.naspo.bidsure_order_service.models.Auction;
import dev.naspo.bidsure_order_service.models.PaymentMethod;
import dev.naspo.bidsure_order_service.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

// Responsible for making network external requests on
// behalf of the OrderService class.
@Service
public class OrderClient {

    private final HttpClient client = HttpClient.newHttpClient();
    @Autowired
    private ObjectMapper objectMapper;

    // API URLS
    private final String USERS_URL_BASE = "http://gateway:8080/users/";
    private final String PAYMENT_METHOD_URL_BASE = "http://gateway:8080/payments/payment-methods/";
    private final String ADDRESSES_URL_BASE = "http://gateway:8080/users/addresses/";
    private final String AUCTION_URL_BASE = "http://gateway:8080/auctions/";

    // Makes a network request to the user service get the user by id.
    User getUserById(int id) {
        // Build the HTTP request to get the user.
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(USERS_URL_BASE + id))
                .build();

        try {
            // Send the request to get the user.
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200 && response.statusCode() != 201) {
                System.err.println("Request to get user failed.");
                return null;
            }
            return objectMapper.readValue(response.body(), User.class);
        } catch (Exception e) {
            System.err.println("Error while sending network request to get user.");
            e.printStackTrace();
            return null;
        }
    }

    // Makes a network request to the payment service get the payment method by id.
    PaymentMethod getPaymentMethodById(int id) {
        // Build the HTTP request to get the payment method.
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(PAYMENT_METHOD_URL_BASE + id))
                .build();

        try {
            // Send the request to get the payment method.
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200 && response.statusCode() != 201) {
                System.err.println("Request to get payment method failed.");
                return null;
            }

            return objectMapper.readValue(response.body(), PaymentMethod.class);

        } catch (Exception e) {
            System.err.println("Error while sending network request to get the payment method.");
            e.printStackTrace();
            return null;
        }
    }

    // Makes a network request to the user service get the address by id.
    Address getAddressById(int id) {
        // Build the HTTP request to get the address.
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ADDRESSES_URL_BASE + id))
                .build();

        try {
            // Send the request to get the address.
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200 && response.statusCode() != 201) {
                System.err.println("Request to get address failed.");
                return null;
            }
            return objectMapper.readValue(response.body(), Address.class);
        } catch (Exception e) {
            System.err.println("Error while sending network request to get the address.");
            e.printStackTrace();
            return null;
        }
    }

    // Makes a network request to the auction service get the auction by id.
    Auction getAuctionById(int id) {
        // Build the HTTP request to get the auction.
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(AUCTION_URL_BASE + id))
                .build();

        try {
            // Send the request to get the auction.
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200 && response.statusCode() != 201) {
                System.err.println("Request to get auction failed.");
                return null;
            }
            return objectMapper.readValue(response.body(), Auction.class);
        } catch (Exception e) {
            System.err.println("Error while sending network request to get the auction.");
            e.printStackTrace();
            return null;
        }
    }
}
