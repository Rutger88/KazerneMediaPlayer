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
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            Library userLibrary = libraryRepository.findFirstByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("No library found for the user"));

            String token = jwtUtil.generateToken(username);
            return new LoginResponse(user, userLibrary.getId(), token);
        }
        throw new RuntimeException("Invalid username or password");
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

        // Create a new user
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);

        // Save the user to the database
        userRepository.save(user);

        // Create a default library for the new user
        Library library = new Library();
        library.setName("Default Library");
        library.setUser(user);  // Associate the library with the user

        // Save the library to the database
        libraryRepository.save(library);

        return user;
    }

    public LoginResponse loginUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            // Retrieve the user's library ID
            Library userLibrary = libraryRepository.findFirstByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("No library found for the user"));

            // Generate JWT token
            String token = jwtUtil.generateToken(username);

            // Return LoginResponse with user, libraryId, and token
            return new LoginResponse(user, userLibrary.getId(), token);
        }
        throw new RuntimeException("Invalid username or password");
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
