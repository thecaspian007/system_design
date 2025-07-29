package com.systemdesign.lld.restaurant;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents an order in the restaurant
 */
public class Order {
    private String orderId;
    private Table table;
    private Customer customer;
    private List<OrderItem> items;
    private OrderStatus status;
    private LocalDateTime orderTime;
    private LocalDateTime completionTime;
    private double totalAmount;
    
    public Order(Table table, Customer customer) {
        this.orderId = UUID.randomUUID().toString().substring(0, 8);
        this.table = table;
        this.customer = customer;
        this.items = new ArrayList<>();
        this.status = OrderStatus.PENDING;
        this.orderTime = LocalDateTime.now();
        this.totalAmount = 0.0;
    }
    
    public void addItem(MenuItem menuItem, int quantity) {
        OrderItem orderItem = new OrderItem(menuItem, quantity);
        items.add(orderItem);
        calculateTotal();
    }
    
    public void removeItem(MenuItem menuItem) {
        items.removeIf(item -> item.getMenuItem().equals(menuItem));
        calculateTotal();
    }
    
    public void updateItemQuantity(MenuItem menuItem, int newQuantity) {
        for (OrderItem item : items) {
            if (item.getMenuItem().equals(menuItem)) {
                item.setQuantity(newQuantity);
                break;
            }
        }
        calculateTotal();
    }
    
    private void calculateTotal() {
        totalAmount = items.stream()
                .mapToDouble(item -> item.getMenuItem().getPrice() * item.getQuantity())
                .sum();
    }
    
    public void confirm() {
        this.status = OrderStatus.CONFIRMED;
    }
    
    public void startPreparation() {
        this.status = OrderStatus.PREPARING;
    }
    
    public void markReady() {
        this.status = OrderStatus.READY;
    }
    
    public void complete() {
        this.status = OrderStatus.COMPLETED;
        this.completionTime = LocalDateTime.now();
    }
    
    public void cancel() {
        this.status = OrderStatus.CANCELLED;
    }
    
    public int getEstimatedPreparationTime() {
        return items.stream()
                .mapToInt(item -> item.getMenuItem().getPreparationTime())
                .max()
                .orElse(0);
    }
    
    // Getters
    public String getOrderId() { return orderId; }
    public Table getTable() { return table; }
    public Customer getCustomer() { return customer; }
    public List<OrderItem> getItems() { return items; }
    public OrderStatus getStatus() { return status; }
    public LocalDateTime getOrderTime() { return orderTime; }
    public LocalDateTime getCompletionTime() { return completionTime; }
    public double getTotalAmount() { return totalAmount; }
    
    @Override
    public String toString() {
        return String.format("Order %s - Table %d - Total: $%.2f - Status: %s",
                orderId, table.getTableNumber(), totalAmount, status);
    }
}

/**
 * Represents an item in an order
 */
class OrderItem {
    private MenuItem menuItem;
    private int quantity;
    
    public OrderItem(MenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
    }
    
    public MenuItem getMenuItem() { return menuItem; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public double getSubtotal() {
        return menuItem.getPrice() * quantity;
    }
    
    @Override
    public String toString() {
        return String.format("%s x%d - $%.2f", 
                menuItem.getName(), quantity, getSubtotal());
    }
}

enum OrderStatus {
    PENDING,
    CONFIRMED,
    PREPARING,
    READY,
    COMPLETED,
    CANCELLED
} 