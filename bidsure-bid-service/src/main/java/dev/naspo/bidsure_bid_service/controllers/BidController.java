package dev.naspo.bidsure_bid_service.controllers;

import dev.naspo.bidsure_bid_service.HibernateManager;
import dev.naspo.bidsure_bid_service.models.Bid;
import jakarta.validation.Valid;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bids")
public class BidController {

    @Autowired
    HibernateManager hibernateManager;

    @PostMapping
    public ResponseEntity<Bid> createBid(@Valid @RequestBody Bid bid) {
        try (Session session = hibernateManager.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(bid);
            session.getTransaction().commit();
            return ResponseEntity.status(HttpStatus.CREATED).body(bid);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bid> getBid(@PathVariable int id) {
        try (Session session = hibernateManager.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Query for the bid.
            Bid bid = session.find(Bid.class, id);
            session.getTransaction().commit();

            if (bid != null) {
                return ResponseEntity.ok(bid);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Bid> updateBid(@PathVariable int id, @Valid @RequestBody Bid updatedBid) {
        try (Session session = hibernateManager.getSessionFactory().openSession()) {
            session.beginTransaction();

            // First find the bid.
            if (session.find(Bid.class, id) == null) {
                return ResponseEntity.notFound().build();
            }

            // Ensure the id of the updated bid provided in the request body matches that in the path.
            updatedBid.setId(id);

            // Merge and update.
            session.merge(updatedBid);
            session.getTransaction().commit();
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBid(@PathVariable int id) {
        try (Session session = hibernateManager.getSessionFactory().openSession()) {
            session.beginTransaction();

            // First find the bid.
            Bid bid = session.find(Bid.class, id);
            if (bid == null) {
                return ResponseEntity.notFound().build();
            }

            // Delete
            session.remove(bid);
            session.getTransaction().commit();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
