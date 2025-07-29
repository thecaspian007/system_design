package com.systemdesign.lld.restaurant;

import java.time.LocalDateTime;

/**
 * Represents a table in the restaurant
 */
public class Table {
    private int tableNumber;
    private int capacity;
    private TableStatus status;
    private LocalDateTime reservationTime;
    private Customer customer;
    private Order currentOrder;
    
    public Table(int tableNumber, int capacity) {
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.status = TableStatus.AVAILABLE;
        this.reservationTime = null;
        this.customer = null;
        this.currentOrder = null;
    }
    
    public boolean isAvailable() {
        return status == TableStatus.AVAILABLE;
    }
    
    public boolean canAccommodate(int partySize) {
        return partySize <= capacity;
    }
    
    public void reserve(Customer customer, LocalDateTime reservationTime) {
        this.customer = customer;
        this.reservationTime = reservationTime;
        this.status = TableStatus.RESERVED;
    }
    
    public void occupy() {
        this.status = TableStatus.OCCUPIED;
    }
    
    public void free() {
        this.status = TableStatus.AVAILABLE;
        this.customer = null;
        this.reservationTime = null;
        this.currentOrder = null;
    }
    
    // Getters and setters
    public int getTableNumber() { return tableNumber; }
    public int getCapacity() { return capacity; }
    public TableStatus getStatus() { return status; }
    public LocalDateTime getReservationTime() { return reservationTime; }
    public Customer getCustomer() { return customer; }
    public Order getCurrentOrder() { return currentOrder; }
    public void setCurrentOrder(Order order) { this.currentOrder = order; }
    
    @Override
    public String toString() {
        return String.format("Table %d (Capacity: %d, Status: %s)", 
            tableNumber, capacity, status);
    }
}

enum TableStatus {
    AVAILABLE,
    RESERVED,
    OCCUPIED
} 