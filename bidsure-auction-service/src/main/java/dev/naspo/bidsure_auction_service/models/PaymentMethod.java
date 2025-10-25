package dev.naspo.bidsure_auction_service.models;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.hibernate.validator.constraints.CreditCardNumber;

@Getter
public class PaymentMethod {

    private int id;

    @NotEmpty
    @Size(max = 50)
    private String firstName;

    @NotEmpty
    @Size(max = 50)
    private String lastName;

    @CreditCardNumber
    private String cardNumber;

    @Pattern(
            regexp = "^(0[1-9]|1[0-2])\\d{2}$",
            message = "Expiry date must be in the format MMYY and have a valid month (01–12)"
    )
    private String expiry_date;

    @Pattern(
            regexp = "^\\d{4}$",
            message = "CVV must be exactly 3 digits"
    )
    private String cvv;

    @NotNull
    private Integer userId;
}