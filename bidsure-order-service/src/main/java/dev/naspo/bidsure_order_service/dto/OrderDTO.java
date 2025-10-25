package dev.naspo.bidsure_order_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderDTO {

    @NotNull
    private BigDecimal totalCost;

    @NotNull
    private Integer userId;

    @NotNull
    private Integer paymentId;

    @NotNull
    private Integer addressId;

    @NotNull
    private Integer auctionId;
}
