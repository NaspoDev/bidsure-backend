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
import org.apache.catalina.connector.Response;
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
        // Get an Auction from the AuctionDTO.
        Auction auction = AuctionDTO.toEntity(updatedAuctionDTO);
        auction.setSeller(getUserById(updatedAuctionDTO.getSellerId()));

        // Call to update the auction.
        auctionService.updateAuction(auction);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update-dutch-price/{id}")
    public ResponseEntity<String> updateDutchAuctionPrice(@PathVariable int id, @Valid @RequestBody UpdateDutchAuctionPriceRequest request) {
        auctionService.updateDutchAuctionPrice(id, request.getUpdatedPrice());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAuction(@PathVariable int id) {
        auctionService.deleteAuction(id);
        return ResponseEntity.ok().build();
    }

    // "Bids" (buy-now orders) on Dutch auctions are called here to process the auction.
    @PostMapping("/purchase-dutch-auction")
    public ResponseEntity<String> purchaseDutchAuction(@Valid @RequestBody Bid bid) {
        // Get the auction.
        Optional<Auction> auction = auctionService.getAuctionById(bid.getAuctionId());
        if (auction.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Call to fulfill logic to purchase the Dutch auction.
        if (auctionService.purchaseDutchAuction(bid, auction.get())) {
            return ResponseEntity.ok().build();
        } else {
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
