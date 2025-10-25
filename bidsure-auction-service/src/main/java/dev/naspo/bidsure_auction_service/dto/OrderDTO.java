package dev.naspo.bidsure_auction_service.dto;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class OrderDTO {

    private BigDecimal totalCost;

    private int userId;

    private int paymentId;

    private int addressId;

    private int auctionId;

    public OrderDTO(BigDecimal totalCost, int userId, int paymentId, int addressId, int auctionId) {
        this.totalCost = totalCost;
        this.userId = userId;
        this.paymentId = paymentId;
        this.addressId = addressId;
        this.auctionId = auctionId;
    }
}
