package dev.naspo.bidsure_auction_service.dto;

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

    private Integer dutchIncrements;

    @NotNull
    private LocalDateTime startingTime;

    @NotNull
    private LocalDateTime endTime;

    private boolean processed;

    @NotNull
    private Integer sellerId;

    @NotNull
    private List<ItemImage> itemImages;
}
