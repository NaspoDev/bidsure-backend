package dev.naspo.bidsure_order_service.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Addresses")
public class Address {
    @Id
    @GeneratedValue
    private int id;

    @NotEmpty
    @Column(name = "address_line_1")
    private String addressLine1;

    @Column(name = "address_line_2")
    private String addressLine2;

    @NotEmpty
    private String city;

    @NotEmpty
    @Column(name = "postal_code")
    @Size(min = 7, max = 7) // Ex. "L4J 5J2"
    private String postalCode;

    @NotEmpty
    @Size(min = 2, max = 2) // Ex. "ON" for Ontario.
    private String province;

    @NotEmpty
    @Column(name = "phone_number")
    private String phoneNumber;

    @NotNull
    @Column(name = "user_id")
    private Integer userId;
}