package dev.naspo.bidsure_auction_service.models;

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

    public Transaction(BigDecimal amount, PaymentMethod paymentMethod) {
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }
}