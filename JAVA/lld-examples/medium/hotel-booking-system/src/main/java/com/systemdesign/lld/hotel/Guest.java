package com.systemdesign.lld.hotel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Guest class representing a hotel guest
 */
public class Guest {
    private String guestId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private LocalDate registrationDate;
    private List<String> bookingHistory;
    private GuestStatus status;
    private String nationalId;
    private String emergencyContact;
    private String preferences;
    private boolean isVip;
    private double totalSpent;
    
    public Guest(String guestId, String name, String email, String phone, String address) {
        this.guestId = guestId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.registrationDate = LocalDate.now();
        this.bookingHistory = new ArrayList<>();
        this.status = GuestStatus.ACTIVE;
        this.isVip = false;
        this.totalSpent = 0.0;
    }
    
    public Guest(String guestId, String name, String email, String phone, String address, String nationalId, String emergencyContact) {
        this(guestId, name, email, phone, address);
        this.nationalId = nationalId;
        this.emergencyContact = emergencyContact;
    }
    
    public void addBooking(String bookingId) {
        bookingHistory.add(bookingId);
    }
    
    public void removeBooking(String bookingId) {
        bookingHistory.remove(bookingId);
    }
    
    public void addToTotalSpent(double amount) {
        this.totalSpent += amount;
        
        // Automatically make VIP if spent more than $5000
        if (this.totalSpent > 5000.0) {
            this.isVip = true;
        }
    }
    
    public boolean hasBooking(String bookingId) {
        return bookingHistory.contains(bookingId);
    }
    
    public int getTotalBookings() {
        return bookingHistory.size();
    }
    
    public void updateStatus(GuestStatus status) {
        this.status = status;
    }
    
    public void setVip(boolean vip) {
        this.isVip = vip;
    }
    
    // Getters
    public String getGuestId() { return guestId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public LocalDate getRegistrationDate() { return registrationDate; }
    public List<String> getBookingHistory() { return new ArrayList<>(bookingHistory); }
    public GuestStatus getStatus() { return status; }
    public String getNationalId() { return nationalId; }
    public String getEmergencyContact() { return emergencyContact; }
    public String getPreferences() { return preferences; }
    public boolean isVip() { return isVip; }
    public double getTotalSpent() { return totalSpent; }
    
    // Setters
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
    public void setPreferences(String preferences) { this.preferences = preferences; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Guest guest = (Guest) o;
        return Objects.equals(guestId, guest.guestId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(guestId);
    }
    
    @Override
    public String toString() {
        return "Guest{" +
                "guestId='" + guestId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", isVip=" + isVip +
                ", totalSpent=" + totalSpent +
                '}';
    }
}

enum GuestStatus {
    ACTIVE,
    INACTIVE,
    BLACKLISTED,
    SUSPENDED
} 