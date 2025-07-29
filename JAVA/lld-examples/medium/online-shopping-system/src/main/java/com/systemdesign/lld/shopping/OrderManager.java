package com.systemdesign.lld.shopping;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * OrderManager handles order processing and management
 */
public class OrderManager {
    private Map<String, Order> orders;
    private Map<String, List<Order>> userOrders;
    private AtomicInteger orderCounter;
    
    public OrderManager() {
        this.orders = new ConcurrentHashMap<>();
        this.userOrders = new ConcurrentHashMap<>();
        this.orderCounter = new AtomicInteger(1);
    }
    
    /**
     * Create a new order from shopping cart
     */
    public Order createOrder(User user, ShoppingCart cart, PaymentMethod paymentMethod) {
        if (cart.isEmpty()) {
            return null;
        }
        
        // Validate cart items availability
        if (!cart.validateCart()) {
            return null;
        }
        
        String orderId = "ORDER-" + String.format("%06d", orderCounter.getAndIncrement());
        Order order = new Order(orderId, user.getUserId(), cart.getAllItems(), paymentMethod);
        
        // Reserve stock for ordered items
        for (CartItem item : cart.getAllItems()) {
            Product product = item.getProduct();
            product.reduceStock(item.getQuantity());
        }
        
        orders.put(orderId, order);
        userOrders.computeIfAbsent(user.getUserId(), k -> new ArrayList<>()).add(order);
        
        // Clear cart after order creation
        cart.clearCart();
        
        return order;
    }
    
    /**
     * Get order by ID
     */
    public Order getOrder(String orderId) {
        return orders.get(orderId);
    }
    
    /**
     * Get all orders for a user
     */
    public List<Order> getUserOrders(String userId) {
        return userOrders.getOrDefault(userId, new ArrayList<>());
    }
    
    /**
     * Update order status
     */
    public boolean updateOrderStatus(String orderId, OrderStatus status) {
        Order order = orders.get(orderId);
        if (order != null) {
            order.updateStatus(status);
            return true;
        }
        return false;
    }
    
    /**
     * Cancel order
     */
    public boolean cancelOrder(String orderId) {
        Order order = orders.get(orderId);
        if (order != null && order.getStatus() == OrderStatus.PENDING) {
            order.updateStatus(OrderStatus.CANCELLED);
            
            // Restore stock for cancelled items
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                product.addStock(item.getQuantity());
            }
            
            return true;
        }
        return false;
    }
    
    /**
     * Get orders by status
     */
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orders.values().stream()
                .filter(order -> order.getStatus() == status)
                .collect(Collectors.toList());
    }
    
    /**
     * Get orders within date range
     */
    public List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orders.values().stream()
                .filter(order -> {
                    LocalDateTime orderDate = order.getOrderDate();
                    return orderDate.isAfter(startDate) && orderDate.isBefore(endDate);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Get total sales amount
     */
    public double getTotalSales() {
        return orders.values().stream()
                .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                .mapToDouble(Order::getTotalAmount)
                .sum();
    }
    
    /**
     * Get order statistics
     */
    public Map<String, Object> getOrderStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", orders.size());
        stats.put("totalSales", getTotalSales());
        
        Map<OrderStatus, Long> statusCount = orders.values().stream()
                .collect(Collectors.groupingBy(Order::getStatus, Collectors.counting()));
        
        stats.put("ordersByStatus", statusCount);
        
        return stats;
    }
}

/**
 * Order represents a customer order
 */
class Order {
    private String orderId;
    private String userId;
    private List<OrderItem> items;
    private OrderStatus status;
    private PaymentMethod paymentMethod;
    private LocalDateTime orderDate;
    private double totalAmount;
    private String shippingAddress;
    
    public Order(String orderId, String userId, List<CartItem> cartItems, PaymentMethod paymentMethod) {
        this.orderId = orderId;
        this.userId = userId;
        this.paymentMethod = paymentMethod;
        this.status = OrderStatus.PENDING;
        this.orderDate = LocalDateTime.now();
        
        // Convert cart items to order items
        this.items = cartItems.stream()
                .map(cartItem -> new OrderItem(cartItem.getProduct(), cartItem.getQuantity()))
                .collect(Collectors.toList());
        
        // Calculate total amount
        this.totalAmount = items.stream()
                .mapToDouble(OrderItem::getTotalPrice)
                .sum();
    }
    
    public String getOrderId() { return orderId; }
    public String getUserId() { return userId; }
    public List<OrderItem> getItems() { return new ArrayList<>(items); }
    public OrderStatus getStatus() { return status; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public double getTotalAmount() { return totalAmount; }
    public String getShippingAddress() { return shippingAddress; }
    
    public void updateStatus(OrderStatus status) {
        this.status = status;
    }
    
    public void setShippingAddress(String address) {
        this.shippingAddress = address;
    }
    
    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", status=" + status +
                ", totalAmount=" + totalAmount +
                ", orderDate=" + orderDate +
                '}';
    }
}

/**
 * OrderItem represents an item in an order
 */
class OrderItem {
    private Product product;
    private int quantity;
    private double priceAtOrder;
    
    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.priceAtOrder = product.getPrice(); // Store price at time of order
    }
    
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public double getPriceAtOrder() { return priceAtOrder; }
    
    public double getTotalPrice() {
        return priceAtOrder * quantity;
    }
    
    @Override
    public String toString() {
        return "OrderItem{" +
                "product=" + product.getName() +
                ", quantity=" + quantity +
                ", priceAtOrder=" + priceAtOrder +
                '}';
    }
}

/**
 * OrderStatus enum for order states
 */
enum OrderStatus {
    PENDING,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    RETURNED
} 