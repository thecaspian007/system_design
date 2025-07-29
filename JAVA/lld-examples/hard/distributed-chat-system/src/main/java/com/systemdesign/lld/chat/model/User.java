package com.systemdesign.lld.chat.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * User model representing a chat user in the distributed system
 */
public class User {
    private final String userId;
    private String username;
    private String email;
    private String displayName;
    private boolean isOnline;
    private LocalDateTime createdAt;
    private LocalDateTime lastSeen;
    private String statusMessage;
    private String profilePictureUrl;
    
    // Connection information
    private String serverId;
    private String sessionId;
    private LocalDateTime connectionTime;
    
    public User(String username, String email, String displayName) {
        this.userId = UUID.randomUUID().toString();
        this.username = username;
        this.email = email;
        this.displayName = displayName;
        this.isOnline = false;
        this.createdAt = LocalDateTime.now();
        this.lastSeen = LocalDateTime.now();
        this.statusMessage = "";
        this.profilePictureUrl = "";
    }
    
    /**
     * Set user as online
     */
    public void setOnline(String serverId, String sessionId) {
        this.isOnline = true;
        this.serverId = serverId;
        this.sessionId = sessionId;
        this.connectionTime = LocalDateTime.now();
        this.lastSeen = LocalDateTime.now();
    }
    
    /**
     * Set user as offline
     */
    public void setOffline() {
        this.isOnline = false;
        this.serverId = null;
        this.sessionId = null;
        this.connectionTime = null;
        this.lastSeen = LocalDateTime.now();
    }
    
    /**
     * Update last seen timestamp
     */
    public void updateLastSeen() {
        this.lastSeen = LocalDateTime.now();
    }
    
    /**
     * Set user status message
     */
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
    
    /**
     * Set profile picture URL
     */
    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
    
    /**
     * Get connection duration in seconds
     */
    public Long getConnectionDuration() {
        if (isOnline && connectionTime != null) {
            return java.time.Duration.between(connectionTime, LocalDateTime.now()).getSeconds();
        }
        return null;
    }
    
    // Getters
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getDisplayName() { return displayName; }
    public boolean isOnline() { return isOnline; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastSeen() { return lastSeen; }
    public String getStatusMessage() { return statusMessage; }
    public String getProfilePictureUrl() { return profilePictureUrl; }
    public String getServerId() { return serverId; }
    public String getSessionId() { return sessionId; }
    public LocalDateTime getConnectionTime() { return connectionTime; }
    
    // Setters
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", displayName='" + displayName + '\'' +
                ", isOnline=" + isOnline +
                '}';
    }
} 