package dev.naspo.bidsure_user_service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping
    private String[] getUsers() {
        return new String[]{"Lauren", "Athy", "Justin", "Laya"};
    }
}
