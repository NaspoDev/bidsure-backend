package dev.naspo.bidsure_auction_service.models;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class Address {

    private int id;

    @NotEmpty
    private String addressLine1;

    private String addressLine2;

    @NotEmpty
    private String city;

    @NotEmpty
    @Size(min = 7, max = 7) // Ex. "L4J 5J2"
    private String postalCode;

    @NotEmpty
    @Size(min = 2, max = 2) // Ex. "ON" for Ontario.
    private String province;

    @NotEmpty
    private String phoneNumber;

    @NotNull
    private Integer userId;
}
