package dev.naspo.bidsure_auction_service.controllers;

import dev.naspo.bidsure_auction_service.HibernateManager;
import dev.naspo.bidsure_auction_service.models.Auction;
import jakarta.validation.Valid;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auctions")
public class AuctionController {

    @Autowired
    HibernateManager hibernateManager;

    @PostMapping
    public ResponseEntity<Auction> createAuction(@Valid @RequestBody Auction auction) {
        try (Session session = hibernateManager.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(auction);
            session.getTransaction().commit();
            return ResponseEntity.status(HttpStatus.CREATED).body(auction);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

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

    @PutMapping("/{id}")
    public ResponseEntity<Auction> updateAuction(@PathVariable int id, @Valid @RequestBody Auction updatedAuction) {
        try (Session session = hibernateManager.getSessionFactory().openSession()) {
            session.beginTransaction();

            // First find the auction.
            if (session.find(Auction.class, id) == null) {
                return ResponseEntity.notFound().build();
            }

            // Ensure the id of the updated auction provided in the request body matches that in the path.
            updatedAuction.setId(id);

            // Merge and update.
            session.merge(updatedAuction);
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
