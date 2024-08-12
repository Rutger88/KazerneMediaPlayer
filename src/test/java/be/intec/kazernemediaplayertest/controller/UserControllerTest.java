package be.intec.kazernemediaplayertest.controller;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import be.intec.kazernemediaplayer.KazerneMediaPLayer;
import com.fasterxml.jackson.databind.ObjectMapper;
import be.intec.kazernemediaplayer.model.User;
import be.intec.kazernemediaplayer.service.UserService;
import be.intec.kazernemediaplayer.controller.UserController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = KazerneMediaPLayer.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }

   /* @Test
    public void testRegisterUser() throws Exception {
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"password\"}"))
                .andExpect(status().isOk()); // or another expected status code
    }*/

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testLoginUser() throws Exception {
        // Given
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");

        User loggedInUser = new User();
        loggedInUser.setUsername("testuser");
        loggedInUser.setPassword("encodedpassword");

        when(userService.loginUser("testuser", "password")).thenReturn(loggedInUser);

        // When & Then
        mockMvc.perform(post("/users/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.password").value("encodedpassword"));
    }

   /* @Test
    public void testDeleteUserById() throws Exception {
        mockMvc.perform(delete("/users/1")
                        .with(user("testuser").password("password").roles("USER")))
                .andExpect(status().isNoContent()); // or another expected status code
    }*/
}
