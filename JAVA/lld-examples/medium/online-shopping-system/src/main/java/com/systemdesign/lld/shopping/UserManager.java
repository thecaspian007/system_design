package com.systemdesign.lld.shopping;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * UserManager handles user registration, authentication, and management
 */
public class UserManager {
    private Map<String, User> users;
    
    public UserManager() {
        this.users = new ConcurrentHashMap<>();
    }
    
    /**
     * Register a new user
     */
    public User registerUser(String email, String name, String address, String password) {
        if (users.containsKey(email)) {
            return null; // User already exists
        }
        
        User user = new User(email, name, address, password);
        users.put(email, user);
        return user;
    }
    
    /**
     * Authenticate user with email and password
     */
    public User authenticateUser(String email, String password) {
        User user = users.get(email);
        if (user != null && user.validatePassword(password)) {
            return user;
        }
        return null;
    }
    
    /**
     * Get user by email
     */
    public User getUserByEmail(String email) {
        return users.get(email);
    }
    
    /**
     * Update user information
     */
    public boolean updateUser(String email, String name, String address) {
        User user = users.get(email);
        if (user != null) {
            user.updateProfile(name, address);
            return true;
        }
        return false;
    }
    
    /**
     * Get total number of registered users
     */
    public int getTotalUsers() {
        return users.size();
    }
} 