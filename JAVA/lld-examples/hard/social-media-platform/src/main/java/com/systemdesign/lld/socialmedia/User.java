package com.systemdesign.lld.socialmedia;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a user in the social media platform
 */
public class User {
    private String userId;
    private String username;
    private String email;
    private String password;
    private String profilePicture;
    private String bio;
    private LocalDateTime joinDate;
    private UserStatus status;
    private PrivacySettings privacySettings;
    
    // Relationships
    private Set<String> followers;
    private Set<String> following;
    private Set<String> blockedUsers;
    private Set<String> mutedUsers;
    
    // Content
    private List<Post> posts;
    private Map<String, List<Message>> conversations;
    
    // Activity
    private LocalDateTime lastActive;
    private boolean isOnline;
    
    public User(String userId, String username, String email, String password) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.joinDate = LocalDateTime.now();
        this.status = UserStatus.ACTIVE;
        this.privacySettings = new PrivacySettings();
        
        this.followers = ConcurrentHashMap.newKeySet();
        this.following = ConcurrentHashMap.newKeySet();
        this.blockedUsers = ConcurrentHashMap.newKeySet();
        this.mutedUsers = ConcurrentHashMap.newKeySet();
        
        this.posts = new ArrayList<>();
        this.conversations = new ConcurrentHashMap<>();
        
        this.lastActive = LocalDateTime.now();
        this.isOnline = true;
    }
    
    // Relationship methods
    public void follow(String userId) {
        if (!userId.equals(this.userId) && !blockedUsers.contains(userId)) {
            following.add(userId);
        }
    }
    
    public void unfollow(String userId) {
        following.remove(userId);
    }
    
    public void addFollower(String userId) {
        if (!userId.equals(this.userId) && !blockedUsers.contains(userId)) {
            followers.add(userId);
        }
    }
    
    public void removeFollower(String userId) {
        followers.remove(userId);
    }
    
    public void blockUser(String userId) {
        if (!userId.equals(this.userId)) {
            blockedUsers.add(userId);
            followers.remove(userId);
            following.remove(userId);
        }
    }
    
    public void unblockUser(String userId) {
        blockedUsers.remove(userId);
    }
    
    public void muteUser(String userId) {
        if (!userId.equals(this.userId)) {
            mutedUsers.add(userId);
        }
    }
    
    public void unmuteUser(String userId) {
        mutedUsers.remove(userId);
    }
    
    // Content methods
    public void addPost(Post post) {
        posts.add(0, post); // Add to beginning for chronological order
    }
    
    public void removePost(Post post) {
        posts.remove(post);
    }
    
    public List<Post> getPosts() {
        return new ArrayList<>(posts);
    }
    
    public List<Post> getRecentPosts(int limit) {
        return posts.stream()
                .limit(limit)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    // Message methods
    public void addMessage(String conversationId, Message message) {
        conversations.computeIfAbsent(conversationId, k -> new ArrayList<>()).add(message);
    }
    
    public List<Message> getConversation(String conversationId) {
        return conversations.getOrDefault(conversationId, new ArrayList<>());
    }
    
    // Activity methods
    public void setOnline(boolean online) {
        this.isOnline = online;
        if (online) {
            this.lastActive = LocalDateTime.now();
        }
    }
    
    public void updateLastActive() {
        this.lastActive = LocalDateTime.now();
    }
    
    // Validation methods
    public boolean canViewProfile(User viewer) {
        if (viewer.getUserId().equals(this.userId)) {
            return true;
        }
        
        if (blockedUsers.contains(viewer.getUserId())) {
            return false;
        }
        
        switch (privacySettings.getProfileVisibility()) {
            case PUBLIC:
                return true;
            case FRIENDS_ONLY:
                return followers.contains(viewer.getUserId()) && following.contains(viewer.getUserId());
            case PRIVATE:
                return false;
            default:
                return false;
        }
    }
    
    public boolean canSendMessage(User recipient) {
        if (recipient.getBlockedUsers().contains(this.userId)) {
            return false;
        }
        
        switch (recipient.getPrivacySettings().getMessagePrivacy()) {
            case PUBLIC:
                return true;
            case FRIENDS_ONLY:
                return recipient.getFollowers().contains(this.userId) && 
                       recipient.getFollowing().contains(this.userId);
            case PRIVATE:
                return false;
            default:
                return false;
        }
    }
    
    public boolean validatePassword(String password) {
        return this.password.equals(password);
    }
    
    // Getters and setters
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getProfilePicture() { return profilePicture; }
    public String getBio() { return bio; }
    public LocalDateTime getJoinDate() { return joinDate; }
    public UserStatus getStatus() { return status; }
    public PrivacySettings getPrivacySettings() { return privacySettings; }
    public Set<String> getFollowers() { return new HashSet<>(followers); }
    public Set<String> getFollowing() { return new HashSet<>(following); }
    public Set<String> getBlockedUsers() { return new HashSet<>(blockedUsers); }
    public Set<String> getMutedUsers() { return new HashSet<>(mutedUsers); }
    public LocalDateTime getLastActive() { return lastActive; }
    public boolean isOnline() { return isOnline; }
    
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }
    public void setBio(String bio) { this.bio = bio; }
    public void setStatus(UserStatus status) { this.status = status; }
    
    public int getFollowersCount() { return followers.size(); }
    public int getFollowingCount() { return following.size(); }
    public int getPostsCount() { return posts.size(); }
    
    @Override
    public String toString() {
        return String.format("User{username='%s', followers=%d, following=%d, posts=%d, status=%s}",
                username, followers.size(), following.size(), posts.size(), status);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return userId.equals(user.userId);
    }
    
    @Override
    public int hashCode() {
        return userId.hashCode();
    }
}

enum UserStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED,
    BANNED
}

enum PrivacyLevel {
    PUBLIC,
    FRIENDS_ONLY,
    PRIVATE
}

class PrivacySettings {
    private PrivacyLevel profileVisibility;
    private PrivacyLevel messagePrivacy;
    private PrivacyLevel postVisibility;
    private boolean showOnlineStatus;
    private boolean allowTagging;
    
    public PrivacySettings() {
        this.profileVisibility = PrivacyLevel.PUBLIC;
        this.messagePrivacy = PrivacyLevel.PUBLIC;
        this.postVisibility = PrivacyLevel.PUBLIC;
        this.showOnlineStatus = true;
        this.allowTagging = true;
    }
    
    // Getters and setters
    public PrivacyLevel getProfileVisibility() { return profileVisibility; }
    public PrivacyLevel getMessagePrivacy() { return messagePrivacy; }
    public PrivacyLevel getPostVisibility() { return postVisibility; }
    public boolean isShowOnlineStatus() { return showOnlineStatus; }
    public boolean isAllowTagging() { return allowTagging; }
    
    public void setProfileVisibility(PrivacyLevel profileVisibility) { this.profileVisibility = profileVisibility; }
    public void setMessagePrivacy(PrivacyLevel messagePrivacy) { this.messagePrivacy = messagePrivacy; }
    public void setPostVisibility(PrivacyLevel postVisibility) { this.postVisibility = postVisibility; }
    public void setShowOnlineStatus(boolean showOnlineStatus) { this.showOnlineStatus = showOnlineStatus; }
    public void setAllowTagging(boolean allowTagging) { this.allowTagging = allowTagging; }
} 