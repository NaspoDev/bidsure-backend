package dev.naspo.bidsure_auction_service.controllers;

import dev.naspo.bidsure_auction_service.models.ItemImage;
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

import java.util.List;

@RestController
@RequestMapping("/auctions")
public class AuctionController {

    @Autowired
    HibernateManager hibernateManager;

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
            auction.setDutchIncrements(auctionDTO.getDutchIncrements());
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
    public ResponseEntity<List<Auction>> getAllAuctions() {
        try (Session session = hibernateManager.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Query for the auctions.
            List<Auction> auctions = session.createQuery("from Auction", Auction.class)
                    .getResultList();

            session.getTransaction().commit();
            return ResponseEntity.ok(auctions);
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
            auction.setDutchIncrements(updatedAuctionDTO.getDutchIncrements());
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
}
