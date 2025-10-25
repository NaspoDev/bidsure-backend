package dev.naspo.bidsure_payment_service.controllers;

import dev.naspo.bidsure_payment_service.HibernateManager;
import dev.naspo.bidsure_payment_service.models.PaymentMethod;
import jakarta.validation.Valid;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments/payment-methods")
public class PaymentMethodController {

    @Autowired
    HibernateManager hibernateManager;

    @PostMapping
    public ResponseEntity<PaymentMethod> createPaymentMethod(@Valid @RequestBody PaymentMethod paymentMethod) {
        try (Session session = hibernateManager.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(paymentMethod);
            session.getTransaction().commit();
            return ResponseEntity.status(HttpStatus.CREATED).body(paymentMethod);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get an individual payment method by id.
    @GetMapping("/{id}")
    public ResponseEntity<PaymentMethod> getPaymentMethod(@PathVariable int id) {
        try (Session session = hibernateManager.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Query for the payment method.
            PaymentMethod paymentMethod = session.find(PaymentMethod.class, id);
            session.getTransaction().commit();

            if (paymentMethod != null) {
                return ResponseEntity.ok(paymentMethod);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get all a user's payment methods.
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentMethod>> getUserPaymentMethod(@PathVariable int userId) {
        try (Session session = hibernateManager.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Query for the payment methods.
            List<PaymentMethod> paymentMethods =
                    session.createQuery("from PaymentMethod pm where pm.userId = :userId", PaymentMethod.class)
                            .setParameter("userId", userId)
                            .getResultList();
            session.getTransaction().commit();

            return ResponseEntity.ok(paymentMethods);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentMethod> updatePaymentMethod(@PathVariable int id, @Valid @RequestBody PaymentMethod updatedPaymentMethod) {
        try (Session session = hibernateManager.getSessionFactory().openSession()) {
            session.beginTransaction();

            // First find the payment method.
            if (session.find(PaymentMethod.class, id) == null) {
                return ResponseEntity.notFound().build();
            }

            // Ensure the id of the updated payment method provided in the request body matches that in the path.
            updatedPaymentMethod.setId(id);

            // Merge and update.
            session.merge(updatedPaymentMethod);
            session.getTransaction().commit();
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePaymentMethod(@PathVariable int id) {
        try (Session session = hibernateManager.getSessionFactory().openSession()) {
            session.beginTransaction();

            // First find the paymentMethod.
            PaymentMethod paymentMethod = session.find(PaymentMethod.class, id);
            if (paymentMethod == null) {
                return ResponseEntity.notFound().build();
            }

            // Delete
            session.remove(paymentMethod);
            session.getTransaction().commit();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
