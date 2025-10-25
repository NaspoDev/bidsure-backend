package dev.naspo.bidsure_auction_service.models;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class Bid {

    private int id;

    @NotNull
    private BigDecimal bidAmount;

    private LocalDateTime placedAt;

    private LocalDateTime withdrawnAt;

    @NotNull
    private Boolean isWinning;

    @NotNull
    private Integer userId;

    @NotNull
    private Integer auctionId;
}
