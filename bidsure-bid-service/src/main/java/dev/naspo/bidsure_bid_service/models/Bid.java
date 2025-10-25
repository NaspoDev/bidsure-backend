package dev.naspo.bidsure_bid_service.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Bids")
@Getter
@Setter
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @Column(name = "bid_amount")
    private BigDecimal bidAmount;

    @Column(name = "placed_at", insertable = false, updatable = false)
    private LocalDateTime placedAt;

    @Column(name = "withdrawn_at")
    private LocalDateTime withdrawnAt;

    @NotNull
    @Column(name = "is_winning")
    private Boolean isWinning;

    @NotNull
    @Column(name = "user_id")
    private Integer userId;

    @NotNull
    @Column(name = "auction_id")
    private Integer auctionId;
}
