package dev.naspo.bidsure_order_service.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "Orders")
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @Column(name = "total_cost")
    private BigDecimal totalCost;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "payment_id")
    private PaymentMethod paymentMethod;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;

    @NotNull
    @OneToOne
    @JoinColumn(name = "auction_id")
    private Auction auction;
}
