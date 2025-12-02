package dev.naspo.bidsure_auction_service.dto;

import dev.naspo.bidsure_auction_service.models.Auction;
import dev.naspo.bidsure_auction_service.models.ItemImage;
import dev.naspo.bidsure_auction_service.models.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class AuctionDTO {

    private int id;

    @NotEmpty
    private String auctionType;

    @NotEmpty
    private String title;

    @NotEmpty
    private String itemDescription;

    @NotEmpty
    private String itemCondition;

    @NotNull
    private BigDecimal startingPrice;

    private BigDecimal updatedDutchPrice;

    @NotNull
    private LocalDateTime startingTime;

    @NotNull
    private LocalDateTime endTime;

    private boolean processed;

    @NotNull
    private Integer sellerId;

    @NotNull
    private List<ItemImage> itemImages;

    /**
     * Maps the core properties of an Auction entity to an AuctionDTO.
     * <p>
     * Only copies fields directly present in the Auction entity.
     * @param auction the Auction entity to map.
     * @return a new AuctionDTO containing the mapped fields.
     */
    public static AuctionDTO from(Auction auction) {
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
        return auctionDTO;
    }

    /**
     * Maps the core properties of an AuctionDTO to an Auction entity.
     * <p>
     * Only copies fields directly present in the AuctionDTO entity.
     * @param auctionDTO the AuctionDTO entity to map.
     * @return a new Auction containing the mapped fields.
     */
    public static Auction toEntity(AuctionDTO auctionDTO) {
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
        return auction;
    }
}
