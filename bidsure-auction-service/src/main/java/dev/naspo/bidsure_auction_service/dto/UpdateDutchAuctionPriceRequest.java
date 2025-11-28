package dev.naspo.bidsure_auction_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class UpdateDutchAuctionPriceRequest {

    @NotNull
    private BigDecimal updatedPrice;
}
