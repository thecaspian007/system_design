package com.systemdesign.lld.restaurant;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer in the restaurant
 */
public class Customer {
    private String customerId;
    private String name;
    private String phoneNumber;
    private String email;
    private int partySize;
    private List<Order> orderHistory;
    
    public Customer(String customerId, String name, String phoneNumber, int partySize) {
        this.customerId = customerId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.partySize = partySize;
        this.orderHistory = new ArrayList<>();
    }
    
    public Customer(String customerId, String name, String phoneNumber, String email, int partySize) {
        this(customerId, name, phoneNumber, partySize);
        this.email = email;
    }
    
    public void addOrder(Order order) {
        orderHistory.add(order);
    }
    
    public List<Order> getOrderHistory() {
        return new ArrayList<>(orderHistory);
    }
    
    public double getTotalSpent() {
        return orderHistory.stream()
                .filter(order -> order.getStatus() == OrderStatus.COMPLETED)
                .mapToDouble(Order::getTotalAmount)
                .sum();
    }
    
    // Getters
    public String getCustomerId() { return customerId; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public int getPartySize() { return partySize; }
    
    // Setters
    public void setEmail(String email) { this.email = email; }
    public void setPartySize(int partySize) { this.partySize = partySize; }
    
    @Override
    public String toString() {
        return String.format("Customer: %s (Party of %d) - %s", 
                name, partySize, phoneNumber);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Customer customer = (Customer) obj;
        return customerId.equals(customer.customerId);
    }
    
    @Override
    public int hashCode() {
        return customerId.hashCode();
    }
} 