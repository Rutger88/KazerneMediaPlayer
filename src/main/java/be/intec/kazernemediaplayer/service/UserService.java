package be.intec.kazernemediaplayer.service;

import be.intec.kazernemediaplayer.config.JwtUtil;
import be.intec.kazernemediaplayer.controller.MediaController;
import be.intec.kazernemediaplayer.model.Library;
import be.intec.kazernemediaplayer.model.User;
import be.intec.kazernemediaplayer.repository.LibraryRepository;
import be.intec.kazernemediaplayer.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import be.intec.kazernemediaplayer.dto.LoginResponse;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

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

            // Generate both access token and refresh token
            String accessToken = jwtUtil.generateToken(username);
            String refreshToken = jwtUtil.generateRefreshToken(username);

            // Pass both tokens to the LoginResponse constructor
            return new LoginResponse(user, userLibrary.getId(), accessToken, refreshToken);
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
            Library userLibrary = libraryRepository.findFirstByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("No library found for the user"));

            String token = jwtUtil.generateToken(username);  // Access token
            String refreshToken = jwtUtil.generateRefreshToken(username);  // Refresh token

            logger.info("User {} logged in successfully with libraryId {}", username, userLibrary.getId());

            return new LoginResponse(user, userLibrary.getId(), token, refreshToken);  // Include both tokens in the response
        }
        logger.warn("Login failed for user {}", username);
        throw new RuntimeException("Invalid username or password");
    }
    public void logoutUser(String token) {
        if (token == null || token.trim().isEmpty()) {
            logger.warn("Attempt to log out with an empty or null token");
            throw new IllegalArgumentException("JWT token is missing or empty");
        }
        // Optionally, you could add the token to a blacklist if you implement token invalidation.
        logger.info("User logged out successfully with token: {}", token);
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
