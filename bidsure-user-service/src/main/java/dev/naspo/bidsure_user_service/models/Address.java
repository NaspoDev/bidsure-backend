package dev.naspo.bidsure_user_service.models;

import jakarta.persistence.*;

@Entity
@Table(name = "Addresses")
public class Address {
    @Id
    @GeneratedValue
    private int id;

    @Column(name = "user_id")
    private int userId;

    private String address;

    @Column(name = "address_line_2")
    private String addressLine2;

    private String city;

    @Column(name = "postal_code")
    private String postalCode;

    private String province;

    @Column(name = "phone_number")
    private String phoneNumber;
}
