package dev.naspo.bidsure_user_service.services;

import dev.naspo.bidsure_user_service.models.User;
import dev.naspo.bidsure_user_service.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

// Responsible for User related business logic.
@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public Optional<User> getUserById(int id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User getUserByCredentials(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password);
    }

    @Transactional
    public void updateUser(User user) {
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(int userId) {
        userRepository.deleteById(userId);
    }
}
