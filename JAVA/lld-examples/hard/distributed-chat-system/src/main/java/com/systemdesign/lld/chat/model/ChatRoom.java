package com.systemdesign.lld.chat.model;

import java.time.LocalDateTime;
import java.util.*;

/**
 * ChatRoom model representing a chat room in the distributed system
 */
public class ChatRoom {
    private final String roomId;
    private String name;
    private String description;
    private final String creatorId;
    private final LocalDateTime createdAt;
    private boolean isActive;
    private boolean isPrivate;
    private int maxMembers;
    
    // Members and roles
    private Set<String> members; // Set of user IDs
    private Set<String> admins; // Set of admin user IDs
    private Set<String> bannedUsers; // Set of banned user IDs
    
    // Room settings
    private boolean isReadOnly;
    private int messageRetentionDays;
    private boolean allowFileSharing;
    private boolean allowReactions;
    
    // Statistics
    private int totalMessages;
    private LocalDateTime lastActivity;
    
    public ChatRoom(String name, String description, User creator) {
        this.roomId = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.creatorId = creator.getUserId();
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
        this.isPrivate = false;
        this.maxMembers = 1000;
        
        // Initialize collections
        this.members = new HashSet<>();
        this.admins = new HashSet<>();
        this.bannedUsers = new HashSet<>();
        
        // Default settings
        this.isReadOnly = false;
        this.messageRetentionDays = 30;
        this.allowFileSharing = true;
        this.allowReactions = true;
        
        // Statistics
        this.totalMessages = 0;
        this.lastActivity = LocalDateTime.now();
        
        // Add creator as admin and member
        this.admins.add(creator.getUserId());
        this.members.add(creator.getUserId());
    }
    
    /**
     * Add member to room
     */
    public boolean addMember(String userId) {
        if (bannedUsers.contains(userId)) {
            return false;
        }
        
        if (members.size() >= maxMembers) {
            return false;
        }
        
        members.add(userId);
        lastActivity = LocalDateTime.now();
        return true;
    }
    
    /**
     * Remove member from room
     */
    public boolean removeMember(String userId) {
        if (members.contains(userId)) {
            members.remove(userId);
            // Remove from admins if they were admin
            admins.remove(userId);
            lastActivity = LocalDateTime.now();
            return true;
        }
        return false;
    }
    
    /**
     * Add admin to room
     */
    public boolean addAdmin(String userId) {
        if (members.contains(userId)) {
            admins.add(userId);
            return true;
        }
        return false;
    }
    
    /**
     * Remove admin from room
     */
    public boolean removeAdmin(String userId) {
        if (admins.contains(userId) && !userId.equals(creatorId)) {
            admins.remove(userId);
            return true;
        }
        return false;
    }
    
    /**
     * Ban user from room
     */
    public boolean banUser(String userId) {
        if (userId.equals(creatorId)) {
            return false;
        }
        
        bannedUsers.add(userId);
        removeMember(userId);
        return true;
    }
    
    /**
     * Unban user from room
     */
    public boolean unbanUser(String userId) {
        return bannedUsers.remove(userId);
    }
    
    /**
     * Check if user is a member
     */
    public boolean isMember(String userId) {
        return members.contains(userId);
    }
    
    /**
     * Check if user is an admin
     */
    public boolean isAdmin(String userId) {
        return admins.contains(userId);
    }
    
    /**
     * Check if user is the creator
     */
    public boolean isCreator(String userId) {
        return userId.equals(creatorId);
    }
    
    /**
     * Check if user is banned
     */
    public boolean isBanned(String userId) {
        return bannedUsers.contains(userId);
    }
    
    /**
     * Check if user can send messages
     */
    public boolean canSendMessage(String userId) {
        return isMember(userId) && 
               !isBanned(userId) && 
               (!isReadOnly || isAdmin(userId));
    }
    
    /**
     * Get member count
     */
    public int getMemberCount() {
        return members.size();
    }
    
    /**
     * Get admin count
     */
    public int getAdminCount() {
        return admins.size();
    }
    
    /**
     * Update activity
     */
    public void updateActivity() {
        this.lastActivity = LocalDateTime.now();
    }
    
    /**
     * Increment message count
     */
    public void incrementMessageCount() {
        this.totalMessages++;
        updateActivity();
    }
    
    /**
     * Set room as read-only
     */
    public void setReadOnly(boolean readOnly) {
        this.isReadOnly = readOnly;
    }
    
    /**
     * Set room as private
     */
    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }
    
    /**
     * Update room description
     */
    public void updateDescription(String description) {
        this.description = description;
    }
    
    /**
     * Deactivate room
     */
    public void deactivate() {
        this.isActive = false;
    }
    
    /**
     * Activate room
     */
    public void activate() {
        this.isActive = true;
    }
    
    /**
     * Get room information
     */
    public Map<String, Object> getRoomInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("roomId", roomId);
        info.put("name", name);
        info.put("description", description);
        info.put("creatorId", creatorId);
        info.put("createdAt", createdAt);
        info.put("isActive", isActive);
        info.put("isPrivate", isPrivate);
        info.put("isReadOnly", isReadOnly);
        info.put("memberCount", getMemberCount());
        info.put("adminCount", getAdminCount());
        info.put("totalMessages", totalMessages);
        info.put("lastActivity", lastActivity);
        info.put("maxMembers", maxMembers);
        info.put("messageRetentionDays", messageRetentionDays);
        info.put("allowFileSharing", allowFileSharing);
        info.put("allowReactions", allowReactions);
        return info;
    }
    
    // Getters
    public String getRoomId() { return roomId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCreatorId() { return creatorId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isActive() { return isActive; }
    public boolean isPrivate() { return isPrivate; }
    public int getMaxMembers() { return maxMembers; }
    public Set<String> getMembers() { return new HashSet<>(members); }
    public Set<String> getAdmins() { return new HashSet<>(admins); }
    public Set<String> getBannedUsers() { return new HashSet<>(bannedUsers); }
    public boolean isReadOnly() { return isReadOnly; }
    public int getMessageRetentionDays() { return messageRetentionDays; }
    public boolean isAllowFileSharing() { return allowFileSharing; }
    public boolean isAllowReactions() { return allowReactions; }
    public int getTotalMessages() { return totalMessages; }
    public LocalDateTime getLastActivity() { return lastActivity; }
    
    // Setters
    public void setName(String name) { this.name = name; }
    public void setMaxMembers(int maxMembers) { this.maxMembers = maxMembers; }
    public void setMessageRetentionDays(int messageRetentionDays) { this.messageRetentionDays = messageRetentionDays; }
    public void setAllowFileSharing(boolean allowFileSharing) { this.allowFileSharing = allowFileSharing; }
    public void setAllowReactions(boolean allowReactions) { this.allowReactions = allowReactions; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatRoom chatRoom = (ChatRoom) o;
        return Objects.equals(roomId, chatRoom.roomId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(roomId);
    }
    
    @Override
    public String toString() {
        return "ChatRoom{" +
                "roomId='" + roomId + '\'' +
                ", name='" + name + '\'' +
                ", members=" + members.size() +
                ", active=" + isActive +
                '}';
    }
} 