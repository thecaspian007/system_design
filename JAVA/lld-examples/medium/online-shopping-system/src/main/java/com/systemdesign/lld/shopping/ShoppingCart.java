package com.systemdesign.lld.shopping;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ShoppingCart manages cart items and operations
 */
public class ShoppingCart {
    private Map<String, CartItem> items;
    private String userId;
    
    public ShoppingCart(String userId) {
        this.userId = userId;
        this.items = new ConcurrentHashMap<>();
    }
    
    /**
     * Add item to cart
     */
    public boolean addItem(Product product, int quantity) {
        if (product == null || quantity <= 0) {
            return false;
        }
        
        // Check if enough stock is available
        if (!product.isAvailable(quantity)) {
            return false;
        }
        
        String productId = product.getProductId();
        CartItem existingItem = items.get(productId);
        
        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + quantity;
            if (product.isAvailable(newQuantity)) {
                existingItem.updateQuantity(newQuantity);
                return true;
            }
            return false;
        } else {
            CartItem newItem = new CartItem(product, quantity);
            items.put(productId, newItem);
            return true;
        }
    }
    
    /**
     * Remove item from cart
     */
    public boolean removeItem(String productId) {
        return items.remove(productId) != null;
    }
    
    /**
     * Update item quantity
     */
    public boolean updateItemQuantity(String productId, int quantity) {
        CartItem item = items.get(productId);
        if (item != null) {
            if (quantity <= 0) {
                return removeItem(productId);
            }
            
            if (item.getProduct().isAvailable(quantity)) {
                item.updateQuantity(quantity);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get cart item by product ID
     */
    public CartItem getItem(String productId) {
        return items.get(productId);
    }
    
    /**
     * Get all cart items
     */
    public List<CartItem> getAllItems() {
        return new ArrayList<>(items.values());
    }
    
    /**
     * Get total price of all items in cart
     */
    public double getTotalPrice() {
        return items.values().stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
    }
    
    /**
     * Get total quantity of all items in cart
     */
    public int getTotalQuantity() {
        return items.values().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
    
    /**
     * Check if cart is empty
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    /**
     * Clear all items from cart
     */
    public void clearCart() {
        items.clear();
    }
    
    /**
     * Display cart contents
     */
    public void displayCart() {
        if (isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }
        
        System.out.println("Cart Contents:");
        for (CartItem item : items.values()) {
            Product product = item.getProduct();
            System.out.printf("- %s x%d - $%.2f each = $%.2f\n", 
                product.getName(), 
                item.getQuantity(), 
                product.getPrice(), 
                item.getTotalPrice());
        }
    }
    
    /**
     * Get cart size (number of different products)
     */
    public int getSize() {
        return items.size();
    }
    
    /**
     * Validate cart (check if all items are still available)
     */
    public boolean validateCart() {
        for (CartItem item : items.values()) {
            if (!item.getProduct().isAvailable(item.getQuantity())) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Get cart summary
     */
    public Map<String, Object> getCartSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalItems", getSize());
        summary.put("totalQuantity", getTotalQuantity());
        summary.put("totalPrice", getTotalPrice());
        summary.put("isEmpty", isEmpty());
        
        return summary;
    }
}

/**
 * CartItem represents an item in the shopping cart
 */
class CartItem {
    private Product product;
    private int quantity;
    
    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public double getTotalPrice() {
        return product.getPrice() * quantity;
    }
    
    @Override
    public String toString() {
        return "CartItem{" +
                "product=" + product.getName() +
                ", quantity=" + quantity +
                ", totalPrice=" + getTotalPrice() +
                '}';
    }
} 