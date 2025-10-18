package dev.naspo.bidsure_user_service;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/{id}")
    private String getUser(@PathVariable int id) {
        // TODO: implement proper logic
        return "Will soon return the user with id: " + id;
    }

    // TODO: Make put and post mappings once user model is created

    @DeleteMapping("/{id}")
    private String deleteUser(@PathVariable int id) {
        return "User with the id " + id + " has been deleted";
    }
}
