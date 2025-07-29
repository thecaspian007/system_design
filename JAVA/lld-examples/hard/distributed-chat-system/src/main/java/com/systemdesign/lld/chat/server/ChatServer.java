package com.systemdesign.lld.chat.server;

import com.systemdesign.lld.chat.model.User;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * ChatServer class representing a chat server in the distributed system
 */
public class ChatServer {
    private final String serverId;
    private final String region;
    private final LocalDateTime startedAt;
    private boolean isActive;
    
    // Users on this server
    private final Map<String, User> users; // userId -> User
    private final Map<String, String> userSessions; // userId -> sessionId
    private final Map<String, String> sessionUsers; // sessionId -> userId
    
    // Connection tracking
    private final Set<String> onlineUsers; // Set of online user IDs
    private final Map<String, LocalDateTime> userConnections; // userId -> connection time
    
    // Server statistics
    private int totalConnections;
    private int totalDisconnections;
    private int peakConcurrentUsers;
    private LocalDateTime lastHeartbeat;
    
    // Load balancing
    private double loadFactor;
    private final int maxConnections;
    
    public ChatServer(String serverId, String region) {
        this.serverId = serverId;
        this.region = region;
        this.startedAt = LocalDateTime.now();
        this.isActive = true;
        this.maxConnections = 10000;
        
        // Initialize thread-safe collections
        this.users = new ConcurrentHashMap<>();
        this.userSessions = new ConcurrentHashMap<>();
        this.sessionUsers = new ConcurrentHashMap<>();
        this.onlineUsers = new ConcurrentSkipListSet<>();
        this.userConnections = new ConcurrentHashMap<>();
        
        // Initialize statistics
        this.totalConnections = 0;
        this.totalDisconnections = 0;
        this.peakConcurrentUsers = 0;
        this.lastHeartbeat = LocalDateTime.now();
        this.loadFactor = 0.0;
        
        System.out.println("Chat server " + serverId + " started in region " + region);
    }
    
    /**
     * Add user to this server
     */
    public synchronized boolean addUser(User user) {
        if (users.containsKey(user.getUserId())) {
            return false; // User already on this server
        }
        
        users.put(user.getUserId(), user);
        return true;
    }
    
    /**
     * Remove user from this server
     */
    public synchronized boolean removeUser(String userId) {
        if (users.containsKey(userId)) {
            // Disconnect if online
            if (onlineUsers.contains(userId)) {
                disconnectUser(userId);
            }
            
            users.remove(userId);
            return true;
        }
        return false;
    }
    
    /**
     * Check if user is registered on this server
     */
    public boolean hasUser(String userId) {
        return users.containsKey(userId);
    }
    
    /**
     * Connect user to server
     */
    public synchronized String connectUser(String userId) {
        if (!users.containsKey(userId)) {
            return null;
        }
        
        if (onlineUsers.size() >= maxConnections) {
            return null; // Server at capacity
        }
        
        User user = users.get(userId);
        String sessionId = UUID.randomUUID().toString();
        
        // Update user status
        user.setOnline(serverId, sessionId);
        
        // Track connection
        onlineUsers.add(userId);
        userSessions.put(userId, sessionId);
        sessionUsers.put(sessionId, userId);
        userConnections.put(userId, LocalDateTime.now());
        
        // Update statistics
        totalConnections++;
        if (onlineUsers.size() > peakConcurrentUsers) {
            peakConcurrentUsers = onlineUsers.size();
        }
        
        updateLoadFactor();
        
        System.out.println("User " + user.getUsername() + " connected to server " + serverId);
        return sessionId;
    }
    
    /**
     * Disconnect user from server
     */
    public synchronized boolean disconnectUser(String userId) {
        if (!onlineUsers.contains(userId)) {
            return false;
        }
        
        User user = users.get(userId);
        if (user != null) {
            user.setOffline();
        }
        
        // Clean up tracking
        String sessionId = userSessions.get(userId);
        if (sessionId != null) {
            userSessions.remove(userId);
            sessionUsers.remove(sessionId);
        }
        
        onlineUsers.remove(userId);
        userConnections.remove(userId);
        
        // Update statistics
        totalDisconnections++;
        updateLoadFactor();
        
        if (user != null) {
            System.out.println("User " + user.getUsername() + " disconnected from server " + serverId);
        }
        
        return true;
    }
    
    /**
     * Get list of online user IDs
     */
    public List<String> getOnlineUsers() {
        return new ArrayList<>(onlineUsers);
    }
    
    /**
     * Get total user count on this server
     */
    public int getUserCount() {
        return users.size();
    }
    
    /**
     * Get online user count
     */
    public int getOnlineCount() {
        return onlineUsers.size();
    }
    
    /**
     * Check if user is online
     */
    public boolean isUserOnline(String userId) {
        return onlineUsers.contains(userId);
    }
    
    /**
     * Get user's session ID
     */
    public String getUserSession(String userId) {
        return userSessions.get(userId);
    }
    
    /**
     * Get user ID for session
     */
    public String getSessionUser(String sessionId) {
        return sessionUsers.get(sessionId);
    }
    
    /**
     * Validate user session
     */
    public boolean validateSession(String userId, String sessionId) {
        String userSessionId = userSessions.get(userId);
        return userSessionId != null && userSessionId.equals(sessionId);
    }
    
    /**
     * Update user presence (heartbeat)
     */
    public void updateUserPresence() {
        LocalDateTime currentTime = LocalDateTime.now();
        this.lastHeartbeat = currentTime;
        
        // Update last seen for online users
        for (String userId : onlineUsers) {
            User user = users.get(userId);
            if (user != null) {
                user.updateLastSeen();
            }
        }
    }
    
    /**
     * Get server statistics
     */
    public Map<String, Object> getServerStats() {
        long uptimeSeconds = java.time.Duration.between(startedAt, LocalDateTime.now()).getSeconds();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("serverId", serverId);
        stats.put("region", region);
        stats.put("isActive", isActive);
        stats.put("startedAt", startedAt);
        stats.put("uptimeSeconds", uptimeSeconds);
        stats.put("totalUsers", users.size());
        stats.put("onlineUsers", onlineUsers.size());
        stats.put("totalConnections", totalConnections);
        stats.put("totalDisconnections", totalDisconnections);
        stats.put("peakConcurrentUsers", peakConcurrentUsers);
        stats.put("loadFactor", loadFactor);
        stats.put("maxConnections", maxConnections);
        stats.put("lastHeartbeat", lastHeartbeat);
        
        return stats;
    }
    
    /**
     * Get list of users on this server
     */
    public List<Map<String, Object>> getUserList() {
        List<Map<String, Object>> userList = new ArrayList<>();
        
        for (User user : users.values()) {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", user.getUserId());
            userInfo.put("username", user.getUsername());
            userInfo.put("displayName", user.getDisplayName());
            userInfo.put("email", user.getEmail());
            userInfo.put("isOnline", user.isOnline());
            userInfo.put("isOnlineOnServer", onlineUsers.contains(user.getUserId()));
            
            LocalDateTime connectionTime = userConnections.get(user.getUserId());
            if (connectionTime != null) {
                userInfo.put("connectionTime", connectionTime);
            }
            
            userList.add(userInfo);
        }
        
        return userList;
    }
    
    /**
     * Broadcast message to all online users
     */
    public void broadcastMessage(String message, String excludeUserId) {
        for (String userId : onlineUsers) {
            if (excludeUserId != null && userId.equals(excludeUserId)) {
                continue;
            }
            
            User user = users.get(userId);
            if (user != null) {
                System.out.println("[Broadcast to " + user.getUsername() + "] " + message);
            }
        }
    }
    
    /**
     * Send message to specific user
     */
    public boolean sendToUser(String userId, String message) {
        if (!onlineUsers.contains(userId)) {
            return false;
        }
        
        User user = users.get(userId);
        if (user != null) {
            System.out.println("[Message to " + user.getUsername() + "] " + message);
            return true;
        }
        
        return false;
    }
    
    /**
     * Check if server can accept new connections
     */
    public boolean canAcceptConnections() {
        return isActive && 
               onlineUsers.size() < maxConnections && 
               loadFactor < 0.9;
    }
    
    /**
     * Update server load factor
     */
    private void updateLoadFactor() {
        if (maxConnections > 0) {
            this.loadFactor = (double) onlineUsers.size() / maxConnections;
        } else {
            this.loadFactor = 0.0;
        }
    }
    
    /**
     * Shutdown server gracefully
     */
    public synchronized void shutdown() {
        System.out.println("Shutting down server " + serverId + "...");
        
        // Disconnect all users
        List<String> onlineUsersCopy = new ArrayList<>(onlineUsers);
        for (String userId : onlineUsersCopy) {
            disconnectUser(userId);
        }
        
        isActive = false;
        System.out.println("Server " + serverId + " shutdown complete");
    }
    
    /**
     * Get user connection duration in seconds
     */
    public Long getConnectionDuration(String userId) {
        LocalDateTime connectionTime = userConnections.get(userId);
        if (connectionTime != null) {
            return java.time.Duration.between(connectionTime, LocalDateTime.now()).getSeconds();
        }
        return null;
    }
    
    /**
     * Migrate user to another server
     */
    public boolean migrateUser(String userId, ChatServer targetServer) {
        if (!users.containsKey(userId)) {
            return false;
        }
        
        User user = users.get(userId);
        boolean wasOnline = onlineUsers.contains(userId);
        String sessionId = userSessions.get(userId);
        
        // Remove from current server
        removeUser(userId);
        
        // Add to target server
        if (targetServer.addUser(user)) {
            if (wasOnline && sessionId != null) {
                targetServer.connectUser(userId);
            }
            System.out.println("User " + user.getUsername() + " migrated from " + 
                             serverId + " to " + targetServer.getServerId());
            return true;
        }
        
        return false;
    }
    
    // Getters
    public String getServerId() { return serverId; }
    public String getRegion() { return region; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public boolean isActive() { return isActive; }
    public double getLoadFactor() { return loadFactor; }
    public int getMaxConnections() { return maxConnections; }
    
    @Override
    public String toString() {
        return "ChatServer{" +
                "serverId='" + serverId + '\'' +
                ", region='" + region + '\'' +
                ", users=" + users.size() +
                ", online=" + onlineUsers.size() +
                '}';
    }
} 