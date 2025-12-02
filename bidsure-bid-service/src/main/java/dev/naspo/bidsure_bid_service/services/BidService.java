package dev.naspo.bidsure_bid_service.services;

import dev.naspo.bidsure_bid_service.models.Bid;
import dev.naspo.bidsure_bid_service.repositories.BidRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Responsible for bid related business logic.
@Service
public class BidService {
    @Autowired private BidRepository bidRepository;

    /**
     * Creates a new bid in the database after performing some validation.
     * @param bid The bid to persist.
     * @return null if not successful. The newly created Bid if successful.
     */
    @Transactional
    public Optional<Bid> createBid(Bid bid) {
        // First get the bid that is currently marked as winning for the auction.
        Optional<Bid> currentWinningBid = bidRepository.findWinningBidForAuction(bid.getAuctionId());
        // If there is one...
        if (currentWinningBid.isPresent()) {
            // Validate that the incoming bid is higher than the current.
            if (bid.getBidAmount().compareTo(currentWinningBid.get().getBidAmount()) <= 0) {
                return Optional.empty();
            }

            // Unmark it as winning and save.
            Bid updated = currentWinningBid.get();
            updated.setIsWinning(false);
            bidRepository.save(updated);
        }

        // Ensure the incoming bid is set as winning.
        bid.setIsWinning(true);
        // Persist.
        return Optional.of(bidRepository.save(bid));
    }

    @Transactional
    public Optional<Bid> getBidById(int id) {
        return bidRepository.findById(id);
    }

    @Transactional
    public List<Bid> getUserBids(int userId) {
        return bidRepository.findUserBids(userId);
    }

    @Transactional
    public Optional<Bid> getWinningBidForAuction(int auctionId) {
        return bidRepository.findWinningBidForAuction(auctionId);
    }

    @Transactional
    public void updateBid(Bid bid) {
        bidRepository.save(bid);
    }

    @Transactional
    public void deleteBid(int bidId) {
        bidRepository.deleteById(bidId);
    }
}
