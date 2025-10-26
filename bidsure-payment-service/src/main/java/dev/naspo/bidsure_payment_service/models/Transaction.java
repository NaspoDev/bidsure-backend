package dev.naspo.bidsure_payment_service.models;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

// A transaction, for payment processing.
@Getter
public class Transaction {

    @NotNull
    private BigDecimal amount;

    @NotNull
    private PaymentMethod paymentMethod;
}
