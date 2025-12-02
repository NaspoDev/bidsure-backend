package dev.naspo.bidsure_bid_service.controllers;

import dev.naspo.bidsure_bid_service.models.Bid;
import dev.naspo.bidsure_bid_service.services.BidService;
import jakarta.validation.Valid;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/bids")
public class BidController {

    @Autowired private BidService bidService;

    @PostMapping
    public ResponseEntity<Bid> createBid(@Valid @RequestBody Bid bid) {
        Optional<Bid> createdBid = bidService.createBid(bid);
        if (createdBid.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBid.get());
    }

    // Get single bid by id.
    @GetMapping("/{id}")
    public ResponseEntity<Bid> getBid(@PathVariable int id) {
        Optional<Bid> bid = bidService.getBidById(id);
        return bid.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Get all of a user's bids.
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Bid>> getUserBids(@PathVariable int userId) {
        List<Bid> bids = bidService.getUserBids(userId);
        return ResponseEntity.ok(bids);
    }

    // Get the winning bid for an auction.
    @GetMapping("/winning-bid/auction/{auctionId}")
    public ResponseEntity<Bid> getWinningBidForAuction(@PathVariable int auctionId) {
        Optional<Bid> bid = bidService.getWinningBidForAuction(auctionId);
        return bid.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Bid> updateBid(@Valid @RequestBody Bid updatedBid) {
        bidService.updateBid(updatedBid);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBid(@PathVariable int id) {
        bidService.deleteBid(id);
        return ResponseEntity.ok().build();
    }
}
