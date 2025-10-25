package dev.naspo.bidsure_order_service.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Auctions")
@Getter
@Setter
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty
    @Column(name = "auction_type")
    private String auctionType;

    @NotEmpty
    private String title;

    @NotEmpty
    @Column(name = "item_description")
    private String itemDescription;

    @NotEmpty
    @Column(name = "item_condition")
    private String itemCondition;

    @NotNull
    @Column(name = "starting_price")
    private BigDecimal startingPrice;

    @Column(name = "dutch_increments")
    private Integer dutchIncrements;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @NotNull
    @Column(name = "starting_time")
    private LocalDateTime startingTime;

    @NotNull
    @Column(name = "end_time")
    private LocalDateTime endTime;

    @NotNull
    @Column(name = "seller_id")
    private Integer sellerId;
}
