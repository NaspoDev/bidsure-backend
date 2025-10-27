package dev.naspo.bidsure_user_service.controllers;

import dev.naspo.bidsure_user_service.HibernateManager;
import dev.naspo.bidsure_user_service.models.Address;
import dev.naspo.bidsure_user_service.models.User;
import jakarta.validation.Valid;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/addresses")
public class AddressController {

    @Autowired
    HibernateManager hibernateManager;

    @PostMapping
    public ResponseEntity<Address> createAddress(@Valid @RequestBody Address address) {
        try (Session session = hibernateManager.getSessionFactory().openSession()) {
            session.beginTransaction();

            // First find the provided user to confirm they exist.
            User user = session.find(User.class, address.getUserId());
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            // Persist the address.
            session.persist(address);

            session.getTransaction().commit();
            return ResponseEntity.status(HttpStatus.CREATED).body(address);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Returns all addresses for the user.
    @GetMapping("/user-addresses")
    public ResponseEntity<List<Address>> getUserAddresses(@PathVariable int userId) {
        try (Session session = hibernateManager.getSessionFactory().openSession()) {
            session.beginTransaction();

            List<Address> addresses = session.createQuery("from Address a where a.user.id = :userId", Address.class)
                    .setParameter("userId", userId)
                    .list();

            session.getTransaction().commit();
            return ResponseEntity.ok(addresses);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Update a specific address based on the id.
    @PutMapping("/{addressId}")
    public ResponseEntity<Address> updateAddress(@PathVariable int addressId, @Valid @RequestBody Address updatedAddress) {
        try (Session session = hibernateManager.getSessionFactory().openSession()) {
            session.beginTransaction();

            // First find the address.
            if (session.find(Address.class, addressId) == null) {
                return ResponseEntity.notFound().build();
            }

            // Ensure the updatedAddress' id from the request body matches that in the path.
            updatedAddress.setId(addressId);
            // Update
            session.merge(updatedAddress);
            session.getTransaction().commit();
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Delete a specific address.
    @DeleteMapping("/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable int addressId) {
        try (Session session = hibernateManager.getSessionFactory().openSession()) {
            session.beginTransaction();

            // First find the address.
            Address address = session.find(Address.class, addressId);
            if (address == null) {
                return ResponseEntity.notFound().build();
            }

            // Delete
            session.remove(address);
            session.getTransaction().commit();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
