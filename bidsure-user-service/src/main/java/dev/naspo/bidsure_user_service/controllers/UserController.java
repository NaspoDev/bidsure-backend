package dev.naspo.bidsure_user_service.controllers;

import dev.naspo.bidsure_user_service.HibernateManager;
import dev.naspo.bidsure_user_service.models.User;
import jakarta.validation.Valid;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    HibernateManager hibernateManager;

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        try (Session session = hibernateManager.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(user);
            session.getTransaction().commit();
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable int id) {
        try (Session session = hibernateManager.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Query for the user.
            User user = session.find(User.class, id);
            session.getTransaction().commit();

            if (user != null) {
                return ResponseEntity.ok(user);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable int id, @Valid @RequestBody User updatedUser) {
        try (Session session = hibernateManager.getSessionFactory().openSession()) {
            session.beginTransaction();

            // First find the user.
            if (session.find(User.class, id) == null) {
                return ResponseEntity.notFound().build();
            }

            // Ensure the id of the updated user provided in the request body matches that in the path.
            updatedUser.setId(id);

            // Merge and update.
            session.merge(updatedUser);
            session.getTransaction().commit();
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable int id) {
        try (Session session = hibernateManager.getSessionFactory().openSession()) {
            session.beginTransaction();

            // First find the user.
            User user = session.find(User.class, id);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            // Delete
            session.remove(user);
            session.getTransaction().commit();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
