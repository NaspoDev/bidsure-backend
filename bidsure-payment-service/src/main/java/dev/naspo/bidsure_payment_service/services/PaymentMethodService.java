package dev.naspo.bidsure_payment_service.services;

import dev.naspo.bidsure_payment_service.models.PaymentMethod;
import dev.naspo.bidsure_payment_service.repositories.PaymentMethodRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Responsible for payment method related business logic.
@Service
public class PaymentMethodService {

    @Autowired private PaymentMethodRepository paymentMethodRepository;

    @Transactional
    public PaymentMethod createPaymentMethod(PaymentMethod paymentMethod) {
        return paymentMethodRepository.save(paymentMethod);
    }

    @Transactional
    public Optional<PaymentMethod> getPaymentMethodById(int id) {
        return paymentMethodRepository.findById(id);
    }

    @Transactional
    public List<PaymentMethod> getUserPaymentMethods(int userId) {
        return paymentMethodRepository.findAllUserPaymentMethods(userId);
    }

    @Transactional
    public PaymentMethod updatePaymentMethod(PaymentMethod paymentMethod) {
        return paymentMethodRepository.save(paymentMethod);
    }

    @Transactional
    public void deletePaymentMethod(int id) {
        paymentMethodRepository.deleteById(id);
    }
}
