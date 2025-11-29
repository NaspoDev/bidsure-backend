package dev.naspo.bidsure_auction_service.repositories;

import dev.naspo.bidsure_auction_service.models.ItemImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemImageRepository extends JpaRepository<ItemImage, Integer> {
    @Query("select i from ItemImage i where i.auctionId = ?1")
    List<ItemImage> findImagesForAuction(int auctionId);
}
