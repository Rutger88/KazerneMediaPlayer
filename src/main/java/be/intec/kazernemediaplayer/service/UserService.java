package be.intec.kazernemediaplayer.service;

import be.intec.kazernemediaplayer.config.JwtUtil;
import be.intec.kazernemediaplayer.model.User;
import be.intec.kazernemediaplayer.repository.UserRepository;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import be.intec.kazernemediaplayer.dto.LoginResponse;
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    @Lazy
    private JwtUtil jwtUtil;
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public LoginResponse authenticateUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            String token = jwtUtil.generateToken(username);
            return new LoginResponse(user, token);
        }
        return null; // or throw an exception to handle invalid credentials
    }
    public User registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("Username already taken");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Hash the password
        return userRepository.save(user);
    }

    public LoginResponse loginUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            String token = jwtUtil.generateToken(String.valueOf(user)); // Ensure you're passing the correct type here
            return new LoginResponse(user, token);
        }
        throw new RuntimeException("Invalid username or password"); // Consider using a custom exception
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User loadUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void deleteUser(Long userId) {
        // Check if user exists
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }

        // Perform the deletion
        userRepository.deleteById(userId);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}