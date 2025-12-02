package dev.naspo.bidsure_payment_service.repositories;

import dev.naspo.bidsure_payment_service.models.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Integer> {
    @Query("select p from PaymentMethod p where p.userId = ?1")
    List<PaymentMethod> findAllUserPaymentMethods(int userId);
}
