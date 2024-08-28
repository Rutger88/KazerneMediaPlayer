package be.intec.kazernemediaplayer.service;

import be.intec.kazernemediaplayer.config.JwtUtil;
import be.intec.kazernemediaplayer.model.Library;
import be.intec.kazernemediaplayer.model.User;
import be.intec.kazernemediaplayer.repository.LibraryRepository;
import be.intec.kazernemediaplayer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import be.intec.kazernemediaplayer.dto.LoginResponse;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LibraryRepository libraryRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserService(UserRepository userRepository, LibraryRepository libraryRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.libraryRepository = libraryRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse authenticateUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {  // Correct password verification
            String token = jwtUtil.generateToken(username);  // Pass the correct type here
            return new LoginResponse(user, token);
        }
        throw new RuntimeException("Invalid username or password");  // Consider using a custom exception
    }

    /*public User registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("Username already taken");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));  // Hash the password
        return userRepository.save(user);
    }*/


    public User registerUser(String username, String password, String email) {
        // Check if the username already exists
        if (userRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("Username is already taken");
        }

        // Create new user
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);

        // Save user to the database
        userRepository.save(user);

        // Create a default library for the new user
        Library library = new Library();
        library.setName("Default Library");
        library.setUser(user);  // Associate the library with the user

        // Save library to the database
        libraryRepository.save(library);

        return user;
    }

    public LoginResponse loginUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {  // Correct password verification
            String token = jwtUtil.generateToken(username);  // Pass the correct type here
            return new LoginResponse(user, token);
        }
        throw new RuntimeException("Invalid username or password");  // Consider using a custom exception
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User loadUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
