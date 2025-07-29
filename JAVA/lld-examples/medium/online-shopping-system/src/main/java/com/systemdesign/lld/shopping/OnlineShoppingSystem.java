package com.systemdesign.lld.shopping;

import java.util.List;
import java.util.Scanner;

/**
 * Online Shopping System - Main Application
 * 
 * This system demonstrates:
 * - User management and authentication
 * - Product catalog and inventory
 * - Shopping cart functionality
 * - Order processing and payment
 * - Order history and tracking
 * 
 * @author System Design Repository
 */
public class OnlineShoppingSystem {
    
    private UserManager userManager;
    private ProductCatalog productCatalog;
    private OrderManager orderManager;
    private Scanner scanner;
    private User currentUser;
    
    public OnlineShoppingSystem() {
        this.userManager = new UserManager();
        this.productCatalog = new ProductCatalog();
        this.orderManager = new OrderManager();
        this.scanner = new Scanner(System.in);
        this.currentUser = null;
        
        initializeSystem();
    }
    
    private void initializeSystem() {
        // Initialize sample users
        userManager.registerUser("john@example.com", "John Doe", "123 Main St", "password123");
        userManager.registerUser("alice@example.com", "Alice Smith", "456 Oak Ave", "password456");
        
        // Initialize sample products
        productCatalog.addProduct(new Product("LAPTOP001", "Gaming Laptop", "High-performance gaming laptop", 1299.99, 10));
        productCatalog.addProduct(new Product("PHONE001", "Smartphone", "Latest model smartphone", 699.99, 25));
        productCatalog.addProduct(new Product("HEADPHONE001", "Wireless Headphones", "Noise-cancelling headphones", 199.99, 15));
        productCatalog.addProduct(new Product("MOUSE001", "Gaming Mouse", "RGB gaming mouse", 79.99, 30));
        productCatalog.addProduct(new Product("KEYBOARD001", "Mechanical Keyboard", "RGB mechanical keyboard", 149.99, 20));
        
        System.out.println("Online Shopping System initialized!");
    }
    
    public void start() {
        System.out.println("=== Welcome to Online Shopping System ===");
        
        while (true) {
            try {
                if (currentUser == null) {
                    showGuestMenu();
                } else {
                    showUserMenu();
                }
                
                int choice = getIntInput();
                
                if (currentUser == null) {
                    handleGuestChoice(choice);
                } else {
                    handleUserChoice(choice);
                }
                
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    
    private void showGuestMenu() {
        System.out.println("\n--- Guest Menu ---");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Browse Products");
        System.out.println("4. Exit");
        System.out.print("Choose an option: ");
    }
    
    private void showUserMenu() {
        System.out.println("\n--- Welcome, " + currentUser.getName() + " ---");
        System.out.println("1. Browse Products");
        System.out.println("2. View Cart");
        System.out.println("3. View Orders");
        System.out.println("4. Account Settings");
        System.out.println("5. Logout");
        System.out.print("Choose an option: ");
    }
    
    private void handleGuestChoice(int choice) {
        switch (choice) {
            case 1:
                handleRegistration();
                break;
            case 2:
                handleLogin();
                break;
            case 3:
                browseProducts();
                break;
            case 4:
                System.out.println("Thank you for visiting!");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }
    
    private void handleUserChoice(int choice) {
        switch (choice) {
            case 1:
                browseProducts();
                break;
            case 2:
                viewCart();
                break;
            case 3:
                viewOrders();
                break;
            case 4:
                accountSettings();
                break;
            case 5:
                logout();
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }
    
    private void handleRegistration() {
        System.out.println("\n--- User Registration ---");
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter address: ");
        String address = scanner.nextLine();
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        User user = userManager.registerUser(email, name, address, password);
        if (user != null) {
            System.out.println("Registration successful! You can now login.");
        } else {
            System.out.println("Registration failed. Email might already be in use.");
        }
    }
    
    private void handleLogin() {
        System.out.println("\n--- User Login ---");
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        User user = userManager.authenticateUser(email, password);
        if (user != null) {
            currentUser = user;
            System.out.println("Login successful! Welcome, " + user.getName());
        } else {
            System.out.println("Invalid email or password.");
        }
    }
    
    private void browseProducts() {
        System.out.println("\n--- Product Catalog ---");
        List<Product> products = productCatalog.getAllProducts();
        
        if (products.isEmpty()) {
            System.out.println("No products available.");
            return;
        }
        
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            System.out.printf("%d. %s - $%.2f (Stock: %d)\n", 
                i + 1, product.getName(), product.getPrice(), product.getStock());
            System.out.println("   " + product.getDescription());
        }
        
        if (currentUser != null) {
            System.out.print("\nEnter product number to add to cart (0 to go back): ");
            int productChoice = getIntInput();
            
            if (productChoice > 0 && productChoice <= products.size()) {
                Product selectedProduct = products.get(productChoice - 1);
                System.out.print("Enter quantity: ");
                int quantity = getIntInput();
                
                if (currentUser.getCart().addItem(selectedProduct, quantity)) {
                    System.out.println("Product added to cart successfully!");
                } else {
                    System.out.println("Failed to add product to cart. Check stock availability.");
                }
            }
        }
    }
    
    private void viewCart() {
        System.out.println("\n--- Shopping Cart ---");
        ShoppingCart cart = currentUser.getCart();
        
        if (cart.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }
        
        cart.displayCart();
        System.out.printf("Total: $%.2f\n", cart.getTotalPrice());
        
        System.out.println("\n1. Proceed to Checkout");
        System.out.println("2. Remove Item");
        System.out.println("3. Clear Cart");
        System.out.println("4. Go Back");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                proceedToCheckout();
                break;
            case 2:
                removeItemFromCart();
                break;
            case 3:
                cart.clearCart();
                System.out.println("Cart cleared.");
                break;
            case 4:
                // Go back
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }
    
    private void proceedToCheckout() {
        ShoppingCart cart = currentUser.getCart();
        
        if (cart.isEmpty()) {
            System.out.println("Cart is empty. Cannot proceed to checkout.");
            return;
        }
        
        System.out.println("\n--- Checkout ---");
        System.out.printf("Total Amount: $%.2f\n", cart.getTotalPrice());
        
        System.out.println("Select Payment Method:");
        System.out.println("1. Credit Card");
        System.out.println("2. PayPal");
        System.out.println("3. Bank Transfer");
        System.out.print("Choice: ");
        
        int paymentChoice = getIntInput();
        PaymentMethod paymentMethod = getPaymentMethod(paymentChoice);
        
        if (paymentMethod == null) {
            System.out.println("Invalid payment method.");
            return;
        }
        
        Order order = orderManager.createOrder(currentUser, cart, paymentMethod);
        
        if (order != null) {
            System.out.println("Order placed successfully!");
            System.out.println("Order ID: " + order.getOrderId());
            System.out.println("Total: $" + order.getTotalAmount());
        } else {
            System.out.println("Order placement failed. Please try again.");
        }
    }
    
    private void removeItemFromCart() {
        ShoppingCart cart = currentUser.getCart();
        List<CartItem> items = cart.getAllItems();
        
        if (items.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }
        
        System.out.println("Items in cart:");
        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            System.out.printf("%d. %s - Quantity: %d\n", 
                i + 1, item.getProduct().getName(), item.getQuantity());
        }
        
        System.out.print("Enter item number to remove: ");
        int itemChoice = getIntInput();
        
        if (itemChoice > 0 && itemChoice <= items.size()) {
            CartItem item = items.get(itemChoice - 1);
            cart.removeItem(item.getProduct().getProductId());
            System.out.println("Item removed from cart.");
        } else {
            System.out.println("Invalid item number.");
        }
    }
    
    private void viewOrders() {
        System.out.println("\n--- Order History ---");
        List<Order> orders = orderManager.getUserOrders(currentUser.getUserId());
        
        if (orders.isEmpty()) {
            System.out.println("No orders found.");
            return;
        }
        
        for (Order order : orders) {
            System.out.printf("Order ID: %s\n", order.getOrderId());
            System.out.printf("Status: %s\n", order.getStatus());
            System.out.printf("Date: %s\n", order.getOrderDate().toString());
            System.out.printf("Total: $%.2f\n", order.getTotalAmount());
            System.out.println("Items:");
            for (OrderItem item : order.getItems()) {
                System.out.printf("  - %s x%d - $%.2f each\n", 
                    item.getProduct().getName(), item.getQuantity(), item.getPriceAtOrder());
            }
            System.out.println("---");
        }
    }
    
    private void accountSettings() {
        System.out.println("\n--- Account Settings ---");
        System.out.println("1. Update Address");
        System.out.println("2. Change Password");
        System.out.println("3. View Profile");
        System.out.println("4. Go Back");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                updateAddress();
                break;
            case 2:
                changePassword();
                break;
            case 3:
                viewProfile();
                break;
            case 4:
                // Go back
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }
    
    private void updateAddress() {
        System.out.print("Enter new address: ");
        String newAddress = scanner.nextLine();
        currentUser.updateAddress(newAddress);
        System.out.println("Address updated successfully.");
    }
    
    private void changePassword() {
        System.out.print("Enter current password: ");
        String currentPassword = scanner.nextLine();
        
        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine();
        
        try {
            currentUser.changePassword(currentPassword, newPassword);
            System.out.println("Password changed successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void viewProfile() {
        System.out.println("\n--- User Profile ---");
        System.out.println("Name: " + currentUser.getName());
        System.out.println("Email: " + currentUser.getEmail());
        System.out.println("Address: " + currentUser.getAddress());
        System.out.println("Registration Date: " + currentUser.getRegistrationDate());
        System.out.println("Account Status: " + currentUser.getStatus());
        System.out.println("User Type: " + currentUser.getUserType());
        System.out.println("Total Orders: " + currentUser.getOrderHistory().size());
    }
    
    private void logout() {
        currentUser = null;
        System.out.println("Logged out successfully!");
    }
    
    private PaymentMethod getPaymentMethod(int choice) {
        switch (choice) {
            case 1: return PaymentMethod.CREDIT_CARD;
            case 2: return PaymentMethod.PAYPAL;
            case 3: return PaymentMethod.BANK_TRANSFER;
            default: return null;
        }
    }
    
    private int getIntInput() {
        while (true) {
            try {
                String input = scanner.nextLine();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }
    
    public static void main(String[] args) {
        OnlineShoppingSystem system = new OnlineShoppingSystem();
        system.start();
    }
}
