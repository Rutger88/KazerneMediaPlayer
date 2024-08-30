package be.intec.kazernemediaplayer.dto;

import be.intec.kazernemediaplayer.model.User;

public class LoginResponse {
    private User user;
    private Long libraryId;
    private String token;
    private String refreshToken;
    // Constructor
    public LoginResponse(User user, Long libraryId, String token, String refreshToken) {
        this.user = user;
        this.libraryId = libraryId;  // This line was missing
        this.token = token;
        this.refreshToken = refreshToken;
    }

    // Getters and Setters
    public Long getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(Long libraryId) {
        this.libraryId = libraryId;
    }

    public User getUser() {
        return user;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
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
