package dev.naspo.bidsure_payment_service.controllers;

import dev.naspo.bidsure_payment_service.models.Transaction;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Responsible for processing payments.
@RestController
@RequestMapping("/payments/processing")
public class PaymentProcessingController {

    // A fake payment processing endpoint.
    @PostMapping
    public ResponseEntity<String> processPayment(@Valid Transaction transaction) {
        // As long as we receive a valid Transaction, which contains a valid PaymentMethod,
        // we can "process" the transaction.
        return ResponseEntity.ok("Payment processed.");
    }
}
