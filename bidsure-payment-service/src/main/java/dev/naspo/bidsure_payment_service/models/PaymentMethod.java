package dev.naspo.bidsure_payment_service.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.CreditCardNumber;

@Entity
@Table(name = "PaymentMethods")
@Getter
@Setter
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty
    @Size(max = 50)
    @Column(name = "first_name")
    private String firstName;

    @NotEmpty
    @Size(max = 50)
    @Column(name = "last_name")
    private String lastName;

    @CreditCardNumber
    @Column(name = "card_number")
    private String cardNumber;

    @Pattern(
            regexp = "^(0[1-9]|1[0-2])\\d{2}$",
            message = "Expiry date must be in the format MMYY and have a valid month (01–12)"
    )
    @Column(name = "exp_date")
    private String expiry_date;

    @Pattern(
            regexp = "^\\d{3}$",
            message = "CVV must be exactly 3 digits"
    )
    private String cvv;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "address_id")
    private Address address;
}
