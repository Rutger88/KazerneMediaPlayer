package be.intec.kazernemediaplayer.dto;

import be.intec.kazernemediaplayer.model.User;

public class RegisterResponse {
    private User user;
    private String token;

    public RegisterResponse(User user, String token) {
        this.user = user;
        this.token = token;
    }

    // Getters and Setters

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

