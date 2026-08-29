package server.shareboard.models;

import java.time.LocalDateTime;
import java.util.Objects;

public class User {
    private int userId;
    private String username;
    private String passwordHash;
    private boolean isOrganization;
    private LocalDateTime createdAt;

    public User() {}

    public User(int userId, String username, String passwordHash, boolean isOrganization, LocalDateTime createdAt) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.isOrganization = isOrganization;
        this.createdAt = createdAt;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean isOrganization() {
        return isOrganization;
    }

    public void setOrganization(boolean organization) {
        isOrganization = organization;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User user)) return false;
        return userId == user.userId && isOrganization == user.isOrganization && Objects.equals(username, user.username) && Objects.equals(passwordHash, user.passwordHash) && Objects.equals(createdAt, user.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, passwordHash, isOrganization, createdAt);
    }
}