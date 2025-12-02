package dev.naspo.bidsure_order_service.models;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class Auction {

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

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @NotNull
    private LocalDateTime startingTime;

    @NotNull
    private LocalDateTime endTime;

    private boolean processed;

    private User seller;
}
