package dev.naspo.bidsure_auction_service.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.naspo.bidsure_auction_service.models.Auction;
import dev.naspo.bidsure_auction_service.models.Bid;
import dev.naspo.bidsure_auction_service.models.ItemImage;
import dev.naspo.bidsure_auction_service.repositories.AuctionRepository;
import dev.naspo.bidsure_auction_service.repositories.ItemImageRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

// Responsible for auction related business logic.
@Service
public class AuctionService {

    @Autowired private AuctionRepository auctionRepository;
    @Autowired private ItemImageRepository itemImageRepository;
    @Autowired private AuctionProcessingService auctionProcessingService;
    @Autowired private ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Transactional
    public Auction createAuction(Auction auction, List<ItemImage> itemImages) {
        itemImageRepository.saveAll(itemImages);
        return auctionRepository.save(auction);
    }

    @Transactional
    public Optional<Auction> getAuctionById(int id) {
        return auctionRepository.findById(id);
    }

    @Transactional
    public List<Auction> getUserAuctions(int userId) {
        return auctionRepository.findAllUserAuctions(userId);
    }

    @Transactional
    public List<Auction> getAllAuctions() {
        return auctionRepository.findAll();
    }

    @Transactional
    public Auction updateAuction(Auction auction) {
        return auctionRepository.save(auction);
    }

    @Transactional
    public List<ItemImage> getItemImagesForAuction(int auctionId) {
        return itemImageRepository.findImagesForAuction(auctionId);
    }

    @Transactional
    public void updateDutchAuctionPrice(int auctionId, BigDecimal updatedPrice) {
        auctionRepository.updateDutchAuctionPrice(auctionId, updatedPrice);
    }

    @Transactional
    public void deleteAuction(int auctionId) {
        auctionRepository.deleteById(auctionId);
    }

    /**
     * Special handling for bids on Dutch auctions. They are treated as "buy-now",
     * so auction is processed immediately.
     *
     * @param bid The bid.
     * @return true if the full process of purchasing the Dutch auction succeeds.
     */
    public boolean purchaseDutchAuction(Bid bid, Auction auction) {
        // If it's not a Dutch auction...
        if (!auction.getAuctionType().equals("dutch")) {
            System.err.println("The auction associated with the bid is not a Dutch auction.");
            return false;
        }

        // Try to create the bid. If successful, process the auction.
        if (createBid(bid)) {
            auctionProcessingService.processAuction(auction);
            return true;
        } else {
            System.err.println("Failed to create the bid.");
            return false;
        }
    }

    @Transactional
    public List<Auction> getExpiredNonProcessedAuctions() {

    }

    /**
     * Makes a network request to the bid service to persist a bid.
     *
     * @param bid The Bid to be created.
     * @return true if the request succeeds.
     */
    private boolean createBid(Bid bid) {
        final String CREATE_BID_URL = "http://gateway:8080/bids";

        String jsonBody;
        try {
            jsonBody = objectMapper.writeValueAsString(bid);
        } catch (JsonProcessingException e) {
            System.err.println("Error processing JSON body for create bid request.");
            return false;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(CREATE_BID_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200 && response.statusCode() != 201) {
                System.err.println("Request to create bid failed!");
                return false;
            }
            return true;
        } catch (Exception e) {
            System.err.println("Failed to send request to create bid!");
            e.printStackTrace();
            return false;
        }
    }
}
