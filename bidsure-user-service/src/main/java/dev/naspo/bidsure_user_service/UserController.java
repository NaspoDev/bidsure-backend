package dev.naspo.bidsure_user_service;

import dev.naspo.bidsure_user_service.models.User;
import org.hibernate.Session;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/{id}")
    public String getUser(@PathVariable int id) {
        // TODO: implement proper logic
        return "Will soon return the user with id: " + id;
    }

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody User user) throws ResponseStatusException {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create user.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully.");
    }

    // TODO: Make put and post mappings once user model is created

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable int id) {
        return "User with the id " + id + " has been deleted";
    }
}
