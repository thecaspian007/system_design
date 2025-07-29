package com.systemdesign.lld.restaurant;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Restaurant Management System - Main Application
 * 
 * This system demonstrates:
 * - Table management and reservations
 * - Menu and order management
 * - Customer management
 * - Kitchen operations
 * - Billing and payment
 * 
 * @author System Design Repository
 */
public class RestaurantManagementSystem {
    
    private Map<Integer, Table> tables;
    private Map<String, MenuItem> menu;
    private Map<String, Customer> customers;
    private Map<String, Order> orders;
    private Queue<Order> kitchenQueue;
    private Scanner scanner;
    
    public RestaurantManagementSystem() {
        this.tables = new HashMap<>();
        this.menu = new HashMap<>();
        this.customers = new HashMap<>();
        this.orders = new HashMap<>();
        this.kitchenQueue = new LinkedList<>();
        this.scanner = new Scanner(System.in);
        
        initializeSystem();
    }
    
    private void initializeSystem() {
        // Initialize tables
        for (int i = 1; i <= 10; i++) {
            int capacity = (i <= 4) ? 2 : (i <= 8) ? 4 : 6;
            tables.put(i, new Table(i, capacity));
        }
        
        // Initialize menu items
        initializeMenu();
        
        // Initialize sample customers
        customers.put("CUST001", new Customer("CUST001", "John Doe", "+1234567890", 2));
        customers.put("CUST002", new Customer("CUST002", "Jane Smith", "+1234567891", "jane@email.com", 4));
        
        System.out.println("Restaurant Management System initialized!");
        System.out.println("Tables: " + tables.size());
        System.out.println("Menu items: " + menu.size());
    }
    
    private void initializeMenu() {
        // Appetizers
        menu.put("APP001", new MenuItem("APP001", "Caesar Salad", "Fresh romaine with caesar dressing", 12.99, MenuCategory.APPETIZER, 10));
        menu.put("APP002", new MenuItem("APP002", "Chicken Wings", "Spicy buffalo wings", 15.99, MenuCategory.APPETIZER, 15));
        menu.put("APP003", new MenuItem("APP003", "Mozzarella Sticks", "Crispy mozzarella sticks", 9.99, MenuCategory.APPETIZER, 8));
        
        // Main Courses
        menu.put("MAIN001", new MenuItem("MAIN001", "Grilled Salmon", "Fresh Atlantic salmon with vegetables", 28.99, MenuCategory.MAIN_COURSE, 20));
        menu.put("MAIN002", new MenuItem("MAIN002", "Ribeye Steak", "Premium ribeye steak with mashed potatoes", 35.99, MenuCategory.MAIN_COURSE, 25));
        menu.put("MAIN003", new MenuItem("MAIN003", "Chicken Alfredo", "Creamy pasta with grilled chicken", 19.99, MenuCategory.MAIN_COURSE, 18));
        menu.put("MAIN004", new MenuItem("MAIN004", "Margherita Pizza", "Classic pizza with tomato and mozzarella", 16.99, MenuCategory.MAIN_COURSE, 15));
        
        // Desserts
        menu.put("DES001", new MenuItem("DES001", "Chocolate Cake", "Rich chocolate cake with vanilla ice cream", 8.99, MenuCategory.DESSERT, 5));
        menu.put("DES002", new MenuItem("DES002", "Tiramisu", "Traditional Italian tiramisu", 9.99, MenuCategory.DESSERT, 5));
        
        // Beverages
        menu.put("BEV001", new MenuItem("BEV001", "Coca Cola", "Refreshing cola drink", 3.99, MenuCategory.BEVERAGE, 2));
        menu.put("BEV002", new MenuItem("BEV002", "Fresh Orange Juice", "Freshly squeezed orange juice", 4.99, MenuCategory.BEVERAGE, 3));
        menu.put("BEV003", new MenuItem("BEV003", "Coffee", "Hot coffee", 2.99, MenuCategory.BEVERAGE, 5));
    }
    
    public void start() {
        System.out.println("\n=== Welcome to Restaurant Management System ===");
        
        while (true) {
            try {
                showMainMenu();
                int choice = getIntInput();
                handleMainChoice(choice);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    
    private void showMainMenu() {
        System.out.println("\n--- Restaurant Management System ---");
        System.out.println("1. Table Management");
        System.out.println("2. Order Management");
        System.out.println("3. Menu Management");
        System.out.println("4. Customer Management");
        System.out.println("5. Kitchen Operations");
        System.out.println("6. Reports");
        System.out.println("7. Exit");
        System.out.print("Choose an option: ");
    }
    
    private void handleMainChoice(int choice) {
        switch (choice) {
            case 1: tableManagement(); break;
            case 2: orderManagement(); break;
            case 3: menuManagement(); break;
            case 4: customerManagement(); break;
            case 5: kitchenOperations(); break;
            case 6: generateReports(); break;
            case 7: 
                System.out.println("Thank you for using Restaurant Management System!");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }
    
    private void tableManagement() {
        System.out.println("\n--- Table Management ---");
        System.out.println("1. View All Tables");
        System.out.println("2. Reserve Table");
        System.out.println("3. Check In Customer");
        System.out.println("4. Check Out Customer");
        System.out.println("5. Back to Main Menu");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1: viewAllTables(); break;
            case 2: reserveTable(); break;
            case 3: checkInCustomer(); break;
            case 4: checkOutCustomer(); break;
            case 5: return;
            default: System.out.println("Invalid choice.");
        }
    }
    
    private void viewAllTables() {
        System.out.println("\n--- All Tables ---");
        for (Table table : tables.values()) {
            System.out.println(table);
            if (table.getCustomer() != null) {
                System.out.println("  Customer: " + table.getCustomer().getName());
            }
            if (table.getReservationTime() != null) {
                System.out.println("  Reservation: " + table.getReservationTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            }
        }
    }
    
    private void reserveTable() {
        System.out.print("Enter customer ID: ");
        String customerId = scanner.nextLine();
        
        Customer customer = customers.get(customerId);
        if (customer == null) {
            System.out.println("Customer not found.");
            return;
        }
        
        System.out.print("Enter reservation date and time (yyyy-MM-dd HH:mm): ");
        String dateTimeStr = scanner.nextLine();
        
        try {
            LocalDateTime reservationTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            
            Table availableTable = findAvailableTable(customer.getPartySize());
            if (availableTable != null) {
                availableTable.reserve(customer, reservationTime);
                System.out.println("Table " + availableTable.getTableNumber() + " reserved for " + customer.getName());
            } else {
                System.out.println("No available table for party size " + customer.getPartySize());
            }
        } catch (Exception e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd HH:mm");
        }
    }
    
    private void checkInCustomer() {
        System.out.print("Enter table number: ");
        int tableNumber = getIntInput();
        
        Table table = tables.get(tableNumber);
        if (table == null) {
            System.out.println("Table not found.");
            return;
        }
        
        if (table.getStatus() == TableStatus.RESERVED) {
            table.occupy();
            System.out.println("Customer checked in at table " + tableNumber);
        } else {
            System.out.println("Table is not reserved or already occupied.");
        }
    }
    
    private void checkOutCustomer() {
        System.out.print("Enter table number: ");
        int tableNumber = getIntInput();
        
        Table table = tables.get(tableNumber);
        if (table == null) {
            System.out.println("Table not found.");
            return;
        }
        
        if (table.getStatus() == TableStatus.OCCUPIED) {
            // Complete any pending orders
            if (table.getCurrentOrder() != null) {
                table.getCurrentOrder().complete();
            }
            
            table.free();
            System.out.println("Customer checked out from table " + tableNumber);
        } else {
            System.out.println("Table is not occupied.");
        }
    }
    
    private void orderManagement() {
        System.out.println("\n--- Order Management ---");
        System.out.println("1. Create New Order");
        System.out.println("2. View Order");
        System.out.println("3. Update Order");
        System.out.println("4. Cancel Order");
        System.out.println("5. View All Orders");
        System.out.println("6. Back to Main Menu");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1: createNewOrder(); break;
            case 2: viewOrder(); break;
            case 3: updateOrder(); break;
            case 4: cancelOrder(); break;
            case 5: viewAllOrders(); break;
            case 6: return;
            default: System.out.println("Invalid choice.");
        }
    }
    
    private void createNewOrder() {
        System.out.print("Enter table number: ");
        int tableNumber = getIntInput();
        
        Table table = tables.get(tableNumber);
        if (table == null || table.getStatus() != TableStatus.OCCUPIED) {
            System.out.println("Table not found or not occupied.");
            return;
        }
        
        Order order = new Order(table, table.getCustomer());
        table.setCurrentOrder(order);
        orders.put(order.getOrderId(), order);
        
        System.out.println("New order created: " + order.getOrderId());
        addItemsToOrder(order);
    }
    
    private void addItemsToOrder(Order order) {
        while (true) {
            displayMenu();
            System.out.print("Enter menu item ID (or 'done' to finish): ");
            String itemId = scanner.nextLine();
            
            if ("done".equalsIgnoreCase(itemId)) {
                break;
            }
            
            MenuItem menuItem = menu.get(itemId);
            if (menuItem == null) {
                System.out.println("Menu item not found.");
                continue;
            }
            
            if (!menuItem.isAvailable()) {
                System.out.println("Menu item is not available.");
                continue;
            }
            
            System.out.print("Enter quantity: ");
            int quantity = getIntInput();
            
            order.addItem(menuItem, quantity);
            System.out.println("Added " + quantity + " x " + menuItem.getName() + " to order");
        }
        
        order.confirm();
        kitchenQueue.offer(order);
        System.out.println("Order confirmed and sent to kitchen!");
        System.out.println("Order Total: $" + order.getTotalAmount());
        System.out.println("Estimated preparation time: " + order.getEstimatedPreparationTime() + " minutes");
    }
    
    private void displayMenu() {
        System.out.println("\n--- Menu ---");
        for (MenuCategory category : MenuCategory.values()) {
            System.out.println("\n" + category + ":");
            menu.values().stream()
                    .filter(item -> item.getCategory() == category)
                    .forEach(item -> System.out.println("  " + item.getId() + ": " + item));
        }
    }
    
    private void viewOrder() {
        System.out.print("Enter order ID: ");
        String orderId = scanner.nextLine();
        
        Order order = orders.get(orderId);
        if (order == null) {
            System.out.println("Order not found.");
            return;
        }
        
        System.out.println("\n--- Order Details ---");
        System.out.println("Order ID: " + order.getOrderId());
        System.out.println("Table: " + order.getTable().getTableNumber());
        System.out.println("Customer: " + order.getCustomer().getName());
        System.out.println("Status: " + order.getStatus());
        System.out.println("Order Time: " + order.getOrderTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        System.out.println("Items:");
        for (OrderItem item : order.getItems()) {
            System.out.println("  " + item);
        }
        System.out.println("Total: $" + order.getTotalAmount());
    }
    
    private void updateOrder() {
        System.out.print("Enter order ID: ");
        String orderId = scanner.nextLine();
        
        Order order = orders.get(orderId);
        if (order == null) {
            System.out.println("Order not found.");
            return;
        }
        
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED) {
            System.out.println("Order cannot be updated in current status: " + order.getStatus());
            return;
        }
        
        System.out.println("1. Add Item");
        System.out.println("2. Remove Item");
        System.out.println("3. Update Item Quantity");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                addItemsToOrder(order);
                break;
            case 2:
                removeItemFromOrder(order);
                break;
            case 3:
                updateItemQuantityInOrder(order);
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }
    
    private void removeItemFromOrder(Order order) {
        if (order.getItems().isEmpty()) {
            System.out.println("Order has no items.");
            return;
        }
        
        System.out.println("Current items:");
        for (int i = 0; i < order.getItems().size(); i++) {
            System.out.println((i + 1) + ". " + order.getItems().get(i));
        }
        
        System.out.print("Enter item number to remove: ");
        int itemNumber = getIntInput();
        
        if (itemNumber > 0 && itemNumber <= order.getItems().size()) {
            OrderItem item = order.getItems().get(itemNumber - 1);
            order.removeItem(item.getMenuItem());
            System.out.println("Item removed from order.");
        } else {
            System.out.println("Invalid item number.");
        }
    }
    
    private void updateItemQuantityInOrder(Order order) {
        if (order.getItems().isEmpty()) {
            System.out.println("Order has no items.");
            return;
        }
        
        System.out.println("Current items:");
        for (int i = 0; i < order.getItems().size(); i++) {
            System.out.println((i + 1) + ". " + order.getItems().get(i));
        }
        
        System.out.print("Enter item number to update: ");
        int itemNumber = getIntInput();
        
        if (itemNumber > 0 && itemNumber <= order.getItems().size()) {
            OrderItem item = order.getItems().get(itemNumber - 1);
            System.out.print("Enter new quantity: ");
            int newQuantity = getIntInput();
            
            if (newQuantity > 0) {
                order.updateItemQuantity(item.getMenuItem(), newQuantity);
                System.out.println("Item quantity updated.");
            } else {
                System.out.println("Invalid quantity.");
            }
        } else {
            System.out.println("Invalid item number.");
        }
    }
    
    private void cancelOrder() {
        System.out.print("Enter order ID: ");
        String orderId = scanner.nextLine();
        
        Order order = orders.get(orderId);
        if (order == null) {
            System.out.println("Order not found.");
            return;
        }
        
        if (order.getStatus() == OrderStatus.COMPLETED) {
            System.out.println("Cannot cancel completed order.");
            return;
        }
        
        order.cancel();
        System.out.println("Order cancelled.");
    }
    
    private void viewAllOrders() {
        System.out.println("\n--- All Orders ---");
        if (orders.isEmpty()) {
            System.out.println("No orders found.");
            return;
        }
        
        for (Order order : orders.values()) {
            System.out.println(order);
        }
    }
    
    private void menuManagement() {
        System.out.println("\n--- Menu Management ---");
        System.out.println("1. View Menu");
        System.out.println("2. Add Menu Item");
        System.out.println("3. Update Menu Item");
        System.out.println("4. Remove Menu Item");
        System.out.println("5. Toggle Item Availability");
        System.out.println("6. Back to Main Menu");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1: displayMenu(); break;
            case 2: addMenuItem(); break;
            case 3: updateMenuItem(); break;
            case 4: removeMenuItem(); break;
            case 5: toggleItemAvailability(); break;
            case 6: return;
            default: System.out.println("Invalid choice.");
        }
    }
    
    private void addMenuItem() {
        System.out.print("Enter item ID: ");
        String id = scanner.nextLine();
        
        if (menu.containsKey(id)) {
            System.out.println("Item ID already exists.");
            return;
        }
        
        System.out.print("Enter item name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter description: ");
        String description = scanner.nextLine();
        
        System.out.print("Enter price: ");
        double price = getDoubleInput();
        
        System.out.println("Select category:");
        MenuCategory[] categories = MenuCategory.values();
        for (int i = 0; i < categories.length; i++) {
            System.out.println((i + 1) + ". " + categories[i]);
        }
        System.out.print("Choose category: ");
        int categoryChoice = getIntInput();
        
        if (categoryChoice < 1 || categoryChoice > categories.length) {
            System.out.println("Invalid category choice.");
            return;
        }
        
        System.out.print("Enter preparation time (minutes): ");
        int prepTime = getIntInput();
        
        MenuItem newItem = new MenuItem(id, name, description, price, categories[categoryChoice - 1], prepTime);
        menu.put(id, newItem);
        System.out.println("Menu item added successfully.");
    }
    
    private void updateMenuItem() {
        System.out.print("Enter item ID to update: ");
        String id = scanner.nextLine();
        
        MenuItem item = menu.get(id);
        if (item == null) {
            System.out.println("Menu item not found.");
            return;
        }
        
        System.out.println("Current item: " + item);
        System.out.println("What would you like to update?");
        System.out.println("1. Price");
        System.out.println("2. Availability");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                System.out.print("Enter new price: ");
                // Note: In a real system, you'd need to modify the MenuItem class to allow price updates
                System.out.println("Price update feature would be implemented here.");
                break;
            case 2:
                item.setAvailable(!item.isAvailable());
                System.out.println("Availability updated to: " + (item.isAvailable() ? "Available" : "Unavailable"));
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }
    
    private void removeMenuItem() {
        System.out.print("Enter item ID to remove: ");
        String id = scanner.nextLine();
        
        MenuItem item = menu.remove(id);
        if (item != null) {
            System.out.println("Menu item removed: " + item.getName());
        } else {
            System.out.println("Menu item not found.");
        }
    }
    
    private void toggleItemAvailability() {
        System.out.print("Enter item ID: ");
        String id = scanner.nextLine();
        
        MenuItem item = menu.get(id);
        if (item == null) {
            System.out.println("Menu item not found.");
            return;
        }
        
        item.setAvailable(!item.isAvailable());
        System.out.println("Item availability updated: " + item.getName() + " is now " + 
                          (item.isAvailable() ? "Available" : "Unavailable"));
    }
    
    private void customerManagement() {
        System.out.println("\n--- Customer Management ---");
        System.out.println("1. View All Customers");
        System.out.println("2. Add Customer");
        System.out.println("3. View Customer Details");
        System.out.println("4. Update Customer");
        System.out.println("5. Back to Main Menu");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1: viewAllCustomers(); break;
            case 2: addCustomer(); break;
            case 3: viewCustomerDetails(); break;
            case 4: updateCustomer(); break;
            case 5: return;
            default: System.out.println("Invalid choice.");
        }
    }
    
    private void viewAllCustomers() {
        System.out.println("\n--- All Customers ---");
        if (customers.isEmpty()) {
            System.out.println("No customers found.");
            return;
        }
        
        for (Customer customer : customers.values()) {
            System.out.println(customer);
        }
    }
    
    private void addCustomer() {
        System.out.print("Enter customer ID: ");
        String customerId = scanner.nextLine();
        
        if (customers.containsKey(customerId)) {
            System.out.println("Customer ID already exists.");
            return;
        }
        
        System.out.print("Enter customer name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter phone number: ");
        String phoneNumber = scanner.nextLine();
        
        System.out.print("Enter email (optional): ");
        String email = scanner.nextLine();
        
        System.out.print("Enter party size: ");
        int partySize = getIntInput();
        
        Customer customer;
        if (email.isEmpty()) {
            customer = new Customer(customerId, name, phoneNumber, partySize);
        } else {
            customer = new Customer(customerId, name, phoneNumber, email, partySize);
        }
        
        customers.put(customerId, customer);
        System.out.println("Customer added successfully.");
    }
    
    private void viewCustomerDetails() {
        System.out.print("Enter customer ID: ");
        String customerId = scanner.nextLine();
        
        Customer customer = customers.get(customerId);
        if (customer == null) {
            System.out.println("Customer not found.");
            return;
        }
        
        System.out.println("\n--- Customer Details ---");
        System.out.println("ID: " + customer.getCustomerId());
        System.out.println("Name: " + customer.getName());
        System.out.println("Phone: " + customer.getPhoneNumber());
        System.out.println("Email: " + (customer.getEmail() != null ? customer.getEmail() : "Not provided"));
        System.out.println("Party Size: " + customer.getPartySize());
        System.out.println("Total Spent: $" + customer.getTotalSpent());
        System.out.println("Order History: " + customer.getOrderHistory().size() + " orders");
    }
    
    private void updateCustomer() {
        System.out.print("Enter customer ID: ");
        String customerId = scanner.nextLine();
        
        Customer customer = customers.get(customerId);
        if (customer == null) {
            System.out.println("Customer not found.");
            return;
        }
        
        System.out.println("What would you like to update?");
        System.out.println("1. Email");
        System.out.println("2. Party Size");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                System.out.print("Enter new email: ");
                String newEmail = scanner.nextLine();
                customer.setEmail(newEmail);
                System.out.println("Email updated successfully.");
                break;
            case 2:
                System.out.print("Enter new party size: ");
                int newPartySize = getIntInput();
                customer.setPartySize(newPartySize);
                System.out.println("Party size updated successfully.");
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }
    
    private void kitchenOperations() {
        System.out.println("\n--- Kitchen Operations ---");
        System.out.println("1. View Kitchen Queue");
        System.out.println("2. Start Order Preparation");
        System.out.println("3. Mark Order Ready");
        System.out.println("4. Complete Order");
        System.out.println("5. Back to Main Menu");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1: viewKitchenQueue(); break;
            case 2: startOrderPreparation(); break;
            case 3: markOrderReady(); break;
            case 4: completeOrder(); break;
            case 5: return;
            default: System.out.println("Invalid choice.");
        }
    }
    
    private void viewKitchenQueue() {
        System.out.println("\n--- Kitchen Queue ---");
        if (kitchenQueue.isEmpty()) {
            System.out.println("No orders in queue.");
            return;
        }
        
        int position = 1;
        for (Order order : kitchenQueue) {
            System.out.println(position + ". " + order);
            System.out.println("   Est. prep time: " + order.getEstimatedPreparationTime() + " minutes");
            position++;
        }
    }
    
    private void startOrderPreparation() {
        if (kitchenQueue.isEmpty()) {
            System.out.println("No orders in queue.");
            return;
        }
        
        Order order = kitchenQueue.poll();
        order.startPreparation();
        System.out.println("Started preparation for order: " + order.getOrderId());
    }
    
    private void markOrderReady() {
        System.out.print("Enter order ID: ");
        String orderId = scanner.nextLine();
        
        Order order = orders.get(orderId);
        if (order == null) {
            System.out.println("Order not found.");
            return;
        }
        
        if (order.getStatus() != OrderStatus.PREPARING) {
            System.out.println("Order is not being prepared.");
            return;
        }
        
        order.markReady();
        System.out.println("Order marked as ready: " + order.getOrderId());
    }
    
    private void completeOrder() {
        System.out.print("Enter order ID: ");
        String orderId = scanner.nextLine();
        
        Order order = orders.get(orderId);
        if (order == null) {
            System.out.println("Order not found.");
            return;
        }
        
        if (order.getStatus() != OrderStatus.READY) {
            System.out.println("Order is not ready for completion.");
            return;
        }
        
        order.complete();
        order.getCustomer().addOrder(order);
        System.out.println("Order completed: " + order.getOrderId());
        System.out.println("Total amount: $" + order.getTotalAmount());
    }
    
    private void generateReports() {
        System.out.println("\n--- Reports ---");
        System.out.println("1. Daily Sales Report");
        System.out.println("2. Popular Items Report");
        System.out.println("3. Table Utilization Report");
        System.out.println("4. Customer Report");
        System.out.println("5. Back to Main Menu");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1: generateDailySalesReport(); break;
            case 2: generatePopularItemsReport(); break;
            case 3: generateTableUtilizationReport(); break;
            case 4: generateCustomerReport(); break;
            case 5: return;
            default: System.out.println("Invalid choice.");
        }
    }
    
    private void generateDailySalesReport() {
        System.out.println("\n--- Daily Sales Report ---");
        
        List<Order> completedOrders = orders.values().stream()
                .filter(order -> order.getStatus() == OrderStatus.COMPLETED)
                .collect(Collectors.toList());
        
        if (completedOrders.isEmpty()) {
            System.out.println("No completed orders found.");
            return;
        }
        
        double totalSales = completedOrders.stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();
        
        System.out.println("Total Orders: " + completedOrders.size());
        System.out.println("Total Sales: $" + String.format("%.2f", totalSales));
        System.out.println("Average Order Value: $" + String.format("%.2f", totalSales / completedOrders.size()));
    }
    
    private void generatePopularItemsReport() {
        System.out.println("\n--- Popular Items Report ---");
        
        Map<String, Integer> itemCount = new HashMap<>();
        
        for (Order order : orders.values()) {
            for (OrderItem item : order.getItems()) {
                String itemName = item.getMenuItem().getName();
                itemCount.put(itemName, itemCount.getOrDefault(itemName, 0) + item.getQuantity());
            }
        }
        
        itemCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue() + " orders"));
    }
    
    private void generateTableUtilizationReport() {
        System.out.println("\n--- Table Utilization Report ---");
        
        long occupiedTables = tables.values().stream()
                .filter(table -> table.getStatus() == TableStatus.OCCUPIED)
                .count();
        
        long reservedTables = tables.values().stream()
                .filter(table -> table.getStatus() == TableStatus.RESERVED)
                .count();
        
        long availableTables = tables.values().stream()
                .filter(table -> table.getStatus() == TableStatus.AVAILABLE)
                .count();
        
        System.out.println("Total Tables: " + tables.size());
        System.out.println("Occupied: " + occupiedTables);
        System.out.println("Reserved: " + reservedTables);
        System.out.println("Available: " + availableTables);
        System.out.println("Utilization: " + String.format("%.1f%%", 
                (double) occupiedTables / tables.size() * 100));
    }
    
    private void generateCustomerReport() {
        System.out.println("\n--- Customer Report ---");
        
        if (customers.isEmpty()) {
            System.out.println("No customers found.");
            return;
        }
        
        System.out.println("Total Customers: " + customers.size());
        
        Customer topCustomer = customers.values().stream()
                .max(Comparator.comparingDouble(Customer::getTotalSpent))
                .orElse(null);
        
        if (topCustomer != null) {
            System.out.println("Top Customer: " + topCustomer.getName() + 
                    " (Total Spent: $" + String.format("%.2f", topCustomer.getTotalSpent()) + ")");
        }
        
        double avgSpent = customers.values().stream()
                .mapToDouble(Customer::getTotalSpent)
                .average()
                .orElse(0.0);
        
        System.out.println("Average Customer Spend: $" + String.format("%.2f", avgSpent));
    }
    
    private Table findAvailableTable(int partySize) {
        return tables.values().stream()
                .filter(table -> table.isAvailable() && table.canAccommodate(partySize))
                .min(Comparator.comparingInt(Table::getCapacity))
                .orElse(null);
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
    
    private double getDoubleInput() {
        while (true) {
            try {
                String input = scanner.nextLine();
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }
    
    public static void main(String[] args) {
        RestaurantManagementSystem system = new RestaurantManagementSystem();
        system.start();
    }
} 