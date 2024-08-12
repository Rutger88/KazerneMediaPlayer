package be.intec.kazernemediaplayertest.service;

import be.intec.kazernemediaplayer.model.User;
import be.intec.kazernemediaplayer.repository.UserRepository;
import be.intec.kazernemediaplayer.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser() {
        // Given
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password"); // raw password
        String encodedPassword = "encodedpassword";

        // When
        when(passwordEncoder.encode(user.getPassword())).thenReturn(encodedPassword);
        when(userRepository.save(user)).thenReturn(user);

        // Call the service method
        User result = userService.registerUser(user);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals(encodedPassword, result.getPassword()); // Compare with encoded password
        verify(passwordEncoder).encode("password"); // Verify the raw password was used
        verify(userRepository).save(user);
    }


    @Test
    void testLoginUser() {
        // Given
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedpassword");
        String rawPassword = "password";

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(passwordEncoder.matches(rawPassword, user.getPassword())).thenReturn(true);

        // When
        User result = userService.loginUser("testuser", rawPassword);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches(rawPassword, user.getPassword());
    }

    @Test
    void testDeleteUser() {
        // Given
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        // When
        userService.deleteUser(userId);

        // Then
        verify(userRepository).deleteById(userId);
    }

    @Test
    void testDeleteUser_userNotFound() {
        // Given
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);

        // When & Then
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            userService.deleteUser(userId);
        });
        assertEquals("User with ID " + userId + " not found", thrown.getMessage());
    }
}
