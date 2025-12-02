package dev.naspo.bidsure_user_service.controllers;

import dev.naspo.bidsure_user_service.dto.UserCredentialsPayload;
import dev.naspo.bidsure_user_service.models.User;
import dev.naspo.bidsure_user_service.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    // Create new user.
    @PostMapping("/sign-up")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(user));
    }

    // Get user by id.
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable int id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());

    }

    // User login
    @PostMapping("/login")
    public ResponseEntity<User> userLogin(@Valid @RequestBody UserCredentialsPayload credentials) {
        return ResponseEntity.ok(userService.getUserByCredentials(credentials.getEmail(), credentials.getPassword()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@Valid @RequestBody User updatedUser) {
        userService.updateUser(updatedUser);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
