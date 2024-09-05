package be.intec.kazernemediaplayer.controller;

import be.intec.kazernemediaplayer.config.JwtUtil;
import be.intec.kazernemediaplayer.dto.LoginRequest;
import be.intec.kazernemediaplayer.dto.LoginResponse;
import be.intec.kazernemediaplayer.dto.RegisterRequest;
import be.intec.kazernemediaplayer.dto.RegisterResponse;
import be.intec.kazernemediaplayer.model.User;
import be.intec.kazernemediaplayer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            // Register the user and create the associated library
            User registeredUser = userService.registerUser(
                    registerRequest.getUsername(),
                    registerRequest.getPassword(),
                    registerRequest.getEmail()
            );

            // Generate a JWT token for the new user
            String token = jwtUtil.generateToken(registeredUser.getUsername());

            // Create a response object that includes the user and the token
            RegisterResponse response = new RegisterResponse(registeredUser, token);

            // Return the response
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Return a bad request response with the error message if registration fails
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            // Call the service method to get the LoginResponse
            LoginResponse loginResponse = userService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());

            // Return the LoginResponse in the response entity
            return ResponseEntity.ok(loginResponse);
        } catch (RuntimeException e) {
            // Handle invalid credentials by returning a 401 status
            return ResponseEntity.status(401).body("Invalid username or password: " + e.getMessage());
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                userService.logoutUser(token);
                return ResponseEntity.ok("Logout successful");
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Invalid token: " + e.getMessage());
            }
        }
        return ResponseEntity.badRequest().body("Authorization header missing or invalid");
    }


   /* @GetMapping("/login")
    public LoginResponse login(@RequestParam String username, @RequestParam String password) {
        return userService.authenticateUser(username, password);// or throw an exception to handle invalid credentials
    }*/

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("User not found: " + e.getMessage());
        }
    }
}