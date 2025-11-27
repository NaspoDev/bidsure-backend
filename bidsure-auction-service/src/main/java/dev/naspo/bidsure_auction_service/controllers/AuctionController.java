package dev.naspo.bidsure_auction_service.controllers;

import dev.naspo.bidsure_auction_service.models.Bid;
import dev.naspo.bidsure_auction_service.models.ItemImage;
import dev.naspo.bidsure_auction_service.services.AuctionProcessingService;
import dev.naspo.bidsure_auction_service.services.HibernateManager;
import dev.naspo.bidsure_auction_service.dto.AuctionDTO;
import dev.naspo.bidsure_auction_service.models.Auction;
import dev.naspo.bidsure_auction_service.models.User;
import jakarta.validation.Valid;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/auctions")
public class AuctionController {

    @Autowired
    HibernateManager hibernateManager;

    @Autowired
    AuctionProcessingService auctionProcessingService;

    @PostMapping
    public ResponseEntity<Auction> createAuction(@Valid @RequestBody AuctionDTO auctionDTO) {
        try (Session session = hibernateManager.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Query to get the entities that Auction needs.
            User user = session.find(User.class, auctionDTO.getSellerId());

            // If user is null, return a NOT FOUND response.
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            // Create an Auction and transfer it the data from the DTO.
            Auction auction = new Auction();
            auction.setAuctionType(auctionDTO.getAuctionType());
            auction.setTitle(auctionDTO.getTitle());
            auction.setItemDescription(auctionDTO.getItemDescription());
            auction.setItemCondition(auctionDTO.getItemCondition());
            auction.setStartingPrice(auctionDTO.getStartingPrice());
            auction.setUpdatedDutchPrice(auctionDTO.getUpdatedDutchPrice());
            auction.setStartingTime(auctionDTO.getStartingTime());
            auction.setEndTime(auctionDTO.getEndTime());
            auction.setProcessed(auctionDTO.isProcessed());
            auction.setSeller(user);

            // Persist.
            session.persist(auction);
            session.getTransaction().commit();

            // Start a new transaction to persist the images.
            session.beginTransaction();
            for (ItemImage image : auctionDTO.getItemImages()) {
                image.setAuctionId(auction.getId());
                session.persist(image);
            }
            session.getTransaction().commit();

            return ResponseEntity.status(HttpStatus.CREATED).body(auction);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get one auction by id.
    @GetMapping("/{id}")
    public ResponseEntity<Auction> getAuction(@PathVariable int id) {
        try (Session session = hibernateManager.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Query for the auction.
            Auction auction = session.find(Auction.class, id);
            session.getTransaction().commit();

            if (auction != null) {
                return ResponseEntity.ok(auction);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get all auctions for a user.
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Auction>> getUserAuctions(@PathVariable int userId) {
        try (Session session = hibernateManager.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Query for the auctions.
            List<Auction> auctions = session.createQuery("from Auction a where a.seller.id = :userId", Auction.class)
                    .setParameter("userId", userId)
                    .getResultList();

            session.getTransaction().commit();
            return ResponseEntity.ok(auctions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get all auctions. Typically used for browsing.
    @GetMapping
    public ResponseEntity<List<AuctionDTO>> getAllAuctions() {
        try (Session session = hibernateManager.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Query for the auctions.
            List<Auction> auctions = session.createQuery("from Auction a where a.processed = false", Auction.class)
                    .getResultList();

            System.out.println("auctions len: " + auctions.size());

            session.getTransaction().commit();

            // Start a new transaction to get item images for each auction.
            // (And convert each Auction to AuctionDTO).
            session.beginTransaction();

            // Convert auctions list to AuctionDTO list.
            List<AuctionDTO> result = new ArrayList<>();
            for (Auction auction : auctions) {
                AuctionDTO auctionDTO = new AuctionDTO();
                auctionDTO.setId(auction.getId());
                auctionDTO.setAuctionType(auction.getAuctionType());
                auctionDTO.setTitle(auction.getTitle());
                auctionDTO.setItemDescription(auction.getItemDescription());
                auctionDTO.setItemCondition(auction.getItemCondition());
                auctionDTO.setStartingPrice(auction.getStartingPrice());
                auctionDTO.setUpdatedDutchPrice(auction.getUpdatedDutchPrice());
                auctionDTO.setStartingTime(auction.getStartingTime());
                auctionDTO.setEndTime(auction.getEndTime());
                auctionDTO.setSellerId(auction.getSeller().getId());

                // Get the auction's item images.
                List<ItemImage> itemImages = session.createQuery("from ItemImage ii where ii.auctionId = :auctionId", ItemImage.class)
                        .setParameter("auctionId", auction.getId())
                        .getResultList();

                auctionDTO.setItemImages(itemImages);

                result.add(auctionDTO);
            }

            session.getTransaction().commit();

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
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
}
