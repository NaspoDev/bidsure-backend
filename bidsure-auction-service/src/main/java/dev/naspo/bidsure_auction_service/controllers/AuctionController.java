package dev.naspo.bidsure_auction_service.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.naspo.bidsure_auction_service.dto.UpdateDutchAuctionPriceRequest;
import dev.naspo.bidsure_auction_service.models.Bid;
import dev.naspo.bidsure_auction_service.models.ItemImage;
import dev.naspo.bidsure_auction_service.services.AuctionProcessingService;
import dev.naspo.bidsure_auction_service.dto.AuctionDTO;
import dev.naspo.bidsure_auction_service.models.Auction;
import dev.naspo.bidsure_auction_service.models.User;
import dev.naspo.bidsure_auction_service.services.AuctionService;
import jakarta.validation.Valid;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/auctions")
public class AuctionController {

    @Autowired
    private AuctionService auctionService;
    @Autowired
    private ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @PostMapping
    public ResponseEntity<Auction> createAuction(@Valid @RequestBody AuctionDTO auctionDTO) {
        // Get the user associated with the auction.
        User user = getUserById(auctionDTO.getSellerId());
        if (user == null) {
            return ResponseEntity.internalServerError().build();
        }

        // Create an Auction object from the DTO and User.
        Auction auction = AuctionDTO.toEntity(auctionDTO);
        auction.setSeller(user);

        Auction createdAuction = auctionService.createAuction(auction, auctionDTO.getItemImages());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAuction);
    }

    // Get one auction by id.
    @GetMapping("/{id}")
    public ResponseEntity<Auction> getAuction(@PathVariable int id) {
        Optional<Auction> auction = auctionService.getAuctionById(id);
        return auction.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Get all auctions for a user.
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Auction>> getUserAuctions(@PathVariable int userId) {
        return ResponseEntity.ok(auctionService.getUserAuctions(userId));
    }

    // Get all auctions.
    @GetMapping
    public ResponseEntity<List<AuctionDTO>> getAllAuctions() {
        List<Auction> auctions = auctionService.getAllAuctions();

        // For each auction, get its images and convert it to an AuctionDTO.
        List<AuctionDTO> auctionDTOs = new ArrayList<>();
        for (Auction auction : auctions) {
            AuctionDTO auctionDTO = AuctionDTO.from(auction);

            // Get the auction's item images.
            List<ItemImage> itemImages = auctionService.getItemImagesForAuction(auction.getId());
            auctionDTO.setItemImages(itemImages);

            auctionDTOs.add(auctionDTO);
        }
        return ResponseEntity.ok(auctionDTOs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Auction> updateAuction(@PathVariable int id, @Valid @RequestBody AuctionDTO updatedAuctionDTO) {
        try (Session session = hibernateManager.getSessionFactory().openSession()) {
            session.beginTransaction();

            // First find the Auction.
            Auction auction = session.find(Auction.class, id);
            if (auction == null) {
                return ResponseEntity.notFound().build();
            }

            // Update the auction based on DTO.
            auction.setAuctionType(updatedAuctionDTO.getAuctionType());
            auction.setTitle(updatedAuctionDTO.getTitle());
            auction.setItemDescription(updatedAuctionDTO.getItemDescription());
            auction.setItemCondition(updatedAuctionDTO.getItemCondition());
            auction.setStartingPrice(updatedAuctionDTO.getStartingPrice());
            auction.setUpdatedDutchPrice(updatedAuctionDTO.getUpdatedDutchPrice());
            auction.setStartingTime(updatedAuctionDTO.getStartingTime());
            auction.setEndTime(updatedAuctionDTO.getEndTime());
            auction.setProcessed(updatedAuctionDTO.isProcessed());

            session.getTransaction().commit();
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/update-dutch-price/{id}")
    public ResponseEntity<String> updateDutchAuctionPrice(@PathVariable int id, @Valid @RequestBody UpdateDutchAuctionPriceRequest request) {
        try (Session session = hibernateManager.getSessionFactory().openSession()) {
            session.beginTransaction();

            // First find the Auction.
            Auction auction = session.find(Auction.class, id);
            if (auction == null) {
                return ResponseEntity.notFound().build();
            }

            // If its not a dutch auction, return bad request.
            if (!auction.getAuctionType().equals("dutch")) {
                return ResponseEntity.badRequest().build();
            }

            // Update
            auction.setUpdatedDutchPrice(request.getUpdatedPrice());

            session.getTransaction().commit();

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAuction(@PathVariable int id) {
        try (Session session = hibernateManager.getSessionFactory().openSession()) {
            session.beginTransaction();

            // First find the auction.
            Auction auction = session.find(Auction.class, id);
            if (auction == null) {
                return ResponseEntity.notFound().build();
            }

            // Delete
            session.remove(auction);
            session.getTransaction().commit();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // "Bids" (buy-now orders) on Dutch auctions are called here to process the auction.
    @PostMapping("/purchase-dutch-auction")
    public ResponseEntity<String> purchaseDutchAuction(@Valid @RequestBody Bid bid) {
        try (Session session = hibernateManager.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Get the Dutch auction associated with the bid.
            Auction auction = session.find(Auction.class, bid.getAuctionId());

            if (auction == null) {
                return ResponseEntity.badRequest().build();
            }

            // If the auction isn't a dutch auction, response with Bad Request.
            if (!auction.getAuctionType().equals("dutch")) {
                return ResponseEntity.badRequest().build();
            }

            // Persist the bid.
            session.persist(bid);

            session.getTransaction().commit();

            // Call to process the auction.
            auctionProcessingService.processAuction(auction);

            return ResponseEntity.ok("Dutch auction purchase successful.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Makes a network call to the User Service to get a user by id.
    private User getUserById(int userId) {
        final String USERS_URL = "http://gateway:8080/users";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(USERS_URL))
                .build();

        try {
            // Send the request
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200 && response.statusCode() != 201) {
                System.err.println("Request to get user failed.");
                return null;
            }

            // Parse JSON response to User and return.
            return objectMapper.readValue(response.body(), User.class);

        } catch (Exception e) {
            System.err.println("Failed to send request to get user.");
            e.printStackTrace();
            return null;
        }
    }
}
