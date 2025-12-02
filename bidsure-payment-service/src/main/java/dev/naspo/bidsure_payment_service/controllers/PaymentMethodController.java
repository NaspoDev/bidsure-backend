package dev.naspo.bidsure_payment_service.controllers;

import dev.naspo.bidsure_payment_service.models.PaymentMethod;
import dev.naspo.bidsure_payment_service.services.PaymentMethodService;
import jakarta.validation.Valid;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/payments/payment-methods")
public class PaymentMethodController {

    @Autowired private PaymentMethodService paymentMethodService;

    @PostMapping
    public ResponseEntity<PaymentMethod> createPaymentMethod(@Valid @RequestBody PaymentMethod paymentMethod) {
        PaymentMethod createdPaymentMethod = paymentMethodService.createPaymentMethod(paymentMethod);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPaymentMethod);
    }

    // Get an individual payment method by id.
    @GetMapping("/{id}")
    public ResponseEntity<PaymentMethod> getPaymentMethod(@PathVariable int id) {
        Optional<PaymentMethod> paymentMethod = paymentMethodService.getPaymentMethodById(id);
        if (paymentMethod.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(paymentMethod.get());
    }

    // Get all a user's payment methods.
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentMethod>> getUserPaymentMethod(@PathVariable int userId) {
        return ResponseEntity.ok(paymentMethodService.getUserPaymentMethods(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentMethod> updatePaymentMethod(@Valid @RequestBody PaymentMethod updatedPaymentMethod) {
        return ResponseEntity.ok(paymentMethodService.updatePaymentMethod(updatedPaymentMethod));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePaymentMethod(@PathVariable int id) {
        paymentMethodService.deletePaymentMethod(id);
        return ResponseEntity.ok().build();
    }
}
