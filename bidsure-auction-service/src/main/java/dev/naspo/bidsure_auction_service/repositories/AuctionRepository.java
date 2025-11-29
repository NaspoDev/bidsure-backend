package dev.naspo.bidsure_auction_service.repositories;

import dev.naspo.bidsure_auction_service.models.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Integer> {
    @Query("select a from Auction a where a.seller.id = ?1")
    List<Auction> findAllUserAuctions(int userId);

    @Modifying
    @Query("update Auction a set a.updatedDutchPrice = ?2 where a.id = ?1")
    void updateDutchAuctionPrice(int auctionId, BigDecimal updatedPrice);
}
