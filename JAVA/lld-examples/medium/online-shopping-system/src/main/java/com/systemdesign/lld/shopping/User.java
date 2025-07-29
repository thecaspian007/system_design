package com.systemdesign.lld.shopping;

import java.time.LocalDateTime;
import java.util.*;

/**
 * User class representing a customer in the online shopping system
 */
public class User {
    private String userId;
    private String email;
    private String name;
    private String password;
    private String address;
    private String phoneNumber;
    private LocalDateTime registrationDate;
    private UserStatus status;
    private List<String> orderHistory;
    private ShoppingCart cart;
    private List<PaymentMethod> savedPaymentMethods;
    private Set<String> wishlist;
    private boolean isEmailVerified;
    private String preferredCurrency;
    private UserType userType;
    
    public User(String email, String name, String address, String password) {
        this.userId = UUID.randomUUID().toString();
        this.email = email;
        this.name = name;
        this.password = password;
        this.address = address;
        this.registrationDate = LocalDateTime.now();
        this.status = UserStatus.ACTIVE;
        this.orderHistory = new ArrayList<>();
        this.cart = new ShoppingCart(userId);
        this.savedPaymentMethods = new ArrayList<>();
        this.wishlist = new HashSet<>();
        this.isEmailVerified = false;
        this.preferredCurrency = "USD";
        this.userType = UserType.REGULAR;
    }
    
    public boolean validatePassword(String password) {
        return this.password.equals(password);
    }
    
    public void changePassword(String oldPassword, String newPassword) {
        if (!validatePassword(oldPassword)) {
            throw new IllegalArgumentException("Invalid old password");
        }
        this.password = newPassword;
    }
    
    public void updateAddress(String newAddress) {
        this.address = newAddress;
    }
    
    public void updateProfile(String name, String address) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
        if (address != null && !address.trim().isEmpty()) {
            this.address = address;
        }
    }
    
    public void addToOrderHistory(String orderId) {
        orderHistory.add(orderId);
    }
    
    public void addToWishlist(String productId) {
        wishlist.add(productId);
    }
    
    public void removeFromWishlist(String productId) {
        wishlist.remove(productId);
    }
    
    public boolean isInWishlist(String productId) {
        return wishlist.contains(productId);
    }
    
    public void addPaymentMethod(PaymentMethod paymentMethod) {
        if (!savedPaymentMethods.contains(paymentMethod)) {
            savedPaymentMethods.add(paymentMethod);
        }
    }
    
    public void removePaymentMethod(PaymentMethod paymentMethod) {
        savedPaymentMethods.remove(paymentMethod);
    }
    
    public void verifyEmail() {
        this.isEmailVerified = true;
    }
    
    public void upgradeToVip() {
        this.userType = UserType.VIP;
    }
    
    public void suspendUser() {
        this.status = UserStatus.SUSPENDED;
    }
    
    public void activateUser() {
        this.status = UserStatus.ACTIVE;
    }
    
    public boolean canPlaceOrder() {
        return status == UserStatus.ACTIVE && isEmailVerified;
    }
    
    public double getDiscountRate() {
        return userType == UserType.VIP ? 0.10 : 0.0; // 10% discount for VIP users
    }
    
    // Getters
    public String getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public UserStatus getStatus() { return status; }
    public List<String> getOrderHistory() { return new ArrayList<>(orderHistory); }
    public ShoppingCart getCart() { return cart; }
    public List<PaymentMethod> getSavedPaymentMethods() { return new ArrayList<>(savedPaymentMethods); }
    public Set<String> getWishlist() { return new HashSet<>(wishlist); }
    public boolean isEmailVerified() { return isEmailVerified; }
    public String getPreferredCurrency() { return preferredCurrency; }
    public UserType getUserType() { return userType; }
    
    // Setters
    public void setEmail(String email) { this.email = email; }
    public void setName(String name) { this.name = name; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setPreferredCurrency(String preferredCurrency) { this.preferredCurrency = preferredCurrency; }
    
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
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", userType=" + userType +
                ", orderCount=" + orderHistory.size() +
                '}';
    }
}

enum UserStatus {
    ACTIVE,
    SUSPENDED,
    INACTIVE,
    DELETED
}

enum UserType {
    REGULAR,
    VIP,
    ADMIN
} 