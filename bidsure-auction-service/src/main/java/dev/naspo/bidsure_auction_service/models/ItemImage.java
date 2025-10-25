package dev.naspo.bidsure_auction_service.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Entity
@Table(name = "ItemImages")
@Getter
@Setter
public class ItemImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @URL
    @NotNull
    private String uri;

    @Column(name = "auction_id")
    private Integer auctionId;
}
