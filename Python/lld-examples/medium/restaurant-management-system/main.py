import sys
import os
from datetime import datetime

sys.path.append(os.path.dirname(os.path.abspath(__file__)))

from restaurant import Restaurant
from table import Table
from menu import Menu, MenuItem
from order import Order
from customer import Customer

class RestaurantManagementSystem:
    """
    Restaurant Management System - Main Application
    
    This system demonstrates:
    - Table management and reservations
    - Menu management and ordering
    - Order processing and billing
    - Customer management
    - Kitchen operations
    
    @author System Design Repository
    """
    
    def __init__(self):
        self.restaurant = Restaurant("Delicious Eats", "123 Food Street")
        self._initialize_restaurant()
        
    def _initialize_restaurant(self):
        """Initialize restaurant with sample data"""
        # Add tables
        for i in range(1, 11):
            table = Table(i, 4)  # 4-seat tables
            self.restaurant.add_table(table)
        
        for i in range(11, 16):
            table = Table(i, 6)  # 6-seat tables
            self.restaurant.add_table(table)
        
        # Add menu items
        menu = self.restaurant.menu
        menu.add_item(MenuItem("APPETIZER001", "Caesar Salad", "Fresh romaine lettuce with caesar dressing", 8.99, "Appetizer"))
        menu.add_item(MenuItem("APPETIZER002", "Chicken Wings", "Spicy buffalo wings with ranch dip", 12.99, "Appetizer"))
        menu.add_item(MenuItem("MAIN001", "Grilled Salmon", "Fresh Atlantic salmon with lemon butter", 24.99, "Main Course"))
        menu.add_item(MenuItem("MAIN002", "Beef Steak", "Premium ribeye steak with mashed potatoes", 28.99, "Main Course"))
        menu.add_item(MenuItem("MAIN003", "Chicken Pasta", "Creamy alfredo pasta with grilled chicken", 16.99, "Main Course"))
        menu.add_item(MenuItem("DESSERT001", "Chocolate Cake", "Rich chocolate cake with vanilla ice cream", 7.99, "Dessert"))
        menu.add_item(MenuItem("BEVERAGE001", "Coca Cola", "Refreshing soft drink", 2.99, "Beverage"))
        menu.add_item(MenuItem("BEVERAGE002", "Coffee", "Freshly brewed coffee", 3.99, "Beverage"))
        
        print(f"Restaurant initialized with {self.restaurant.get_total_tables()} tables and {len(menu.items)} menu items")
    
    def start(self):
        """Start the restaurant management system"""
        print(f"=== Welcome to {self.restaurant.name} Management System ===")
        
        while True:
            try:
                self._show_main_menu()
                choice = self._get_int_input()
                
                if choice == 1:
                    self._view_tables()
                elif choice == 2:
                    self._reserve_table()
                elif choice == 3:
                    self._view_menu()
                elif choice == 4:
                    self._take_order()
                elif choice == 5:
                    self._view_order()
                elif choice == 6:
                    self._process_payment()
                elif choice == 7:
                    self._restaurant_status()
                elif choice == 8:
                    print("Thank you for using the restaurant management system!")
                    break
                else:
                    print("Invalid choice. Please try again.")
                    
            except Exception as e:
                print(f"Error: {e}")
    
    def _show_main_menu(self):
        """Show main menu"""
        print("\n--- Restaurant Management System ---")
        print("1. View Tables")
        print("2. Reserve Table")
        print("3. View Menu")
        print("4. Take Order")
        print("5. View Order")
        print("6. Process Payment")
        print("7. Restaurant Status")
        print("8. Exit")
        print("Choose an option: ", end="")
    
    def _view_tables(self):
        """View all tables"""
        print("\n--- Tables ---")
        tables = self.restaurant.get_all_tables()
        
        for table in tables:
            status = "Available" if table.is_available() else "Occupied"
            print(f"Table {table.table_number} - {table.capacity} seats - {status}")
    
    def _reserve_table(self):
        """Reserve a table"""
        print("\n--- Reserve Table ---")
        
        # Customer info
        name = input("Enter customer name: ")
        phone = input("Enter phone number: ")
        party_size = self._get_int_input("Enter party size: ")
        
        customer = Customer(name, phone)
        
        # Find available table
        available_tables = self.restaurant.get_available_tables(party_size)
        
        if not available_tables:
            print("No available tables for the requested party size.")
            return
        
        print("Available tables:")
        for i, table in enumerate(available_tables, 1):
            print(f"{i}. Table {table.table_number} - {table.capacity} seats")
        
        choice = self._get_int_input("Select table: ")
        
        if 1 <= choice <= len(available_tables):
            table = available_tables[choice - 1]
            if self.restaurant.reserve_table(table.table_number, customer):
                print(f"Table {table.table_number} reserved successfully!")
            else:
                print("Table reservation failed.")
        else:
            print("Invalid selection.")
    
    def _view_menu(self):
        """View menu"""
        print("\n--- Menu ---")
        menu = self.restaurant.menu
        
        categories = menu.get_categories()
        for category in categories:
            print(f"\n{category}:")
            items = menu.get_items_by_category(category)
            for item in items:
                print(f"  {item.item_id}: {item.name} - ${item.price:.2f}")
                print(f"    {item.description}")
    
    def _take_order(self):
        """Take order"""
        print("\n--- Take Order ---")
        
        table_number = self._get_int_input("Enter table number: ")
        table = self.restaurant.get_table(table_number)
        
        if not table or table.is_available():
            print("Table not found or not occupied.")
            return
        
        if table.current_order:
            print("Table already has an active order.")
            return
        
        order = Order(table, table.current_customer)
        
        while True:
            print("\nAdd items to order (enter 'done' to finish):")
            item_id = input("Enter item ID: ")
            
            if item_id.lower() == 'done':
                break
            
            item = self.restaurant.menu.get_item(item_id)
            if not item:
                print("Item not found.")
                continue
            
            quantity = self._get_int_input("Enter quantity: ")
            order.add_item(item, quantity)
            print(f"Added {quantity} x {item.name} to order")
        
        table.current_order = order
        print(f"Order created successfully! Total: ${order.get_total():.2f}")
    
    def _view_order(self):
        """View order"""
        print("\n--- View Order ---")
        
        table_number = self._get_int_input("Enter table number: ")
        table = self.restaurant.get_table(table_number)
        
        if not table or not table.current_order:
            print("No active order found for this table.")
            return
        
        order = table.current_order
        print(f"Order for Table {table_number}:")
        print(f"Customer: {order.customer.name}")
        print(f"Status: {order.status.value}")
        print("Items:")
        for item in order.items:
            print(f"  {item.menu_item.name} x{item.quantity} - ${item.get_total():.2f}")
        print(f"Total: ${order.get_total():.2f}")
    
    def _process_payment(self):
        """Process payment"""
        print("\n--- Process Payment ---")
        
        table_number = self._get_int_input("Enter table number: ")
        table = self.restaurant.get_table(table_number)
        
        if not table or not table.current_order:
            print("No active order found for this table.")
            return
        
        order = table.current_order
        total = order.get_total()
        
        print(f"Total amount: ${total:.2f}")
        payment = self._get_float_input("Enter payment amount: $")
        
        if payment >= total:
            order.mark_as_paid()
            table.clear_table()
            print(f"Payment processed successfully! Change: ${payment - total:.2f}")
        else:
            print("Insufficient payment.")
    
    def _restaurant_status(self):
        """Show restaurant status"""
        print("\n--- Restaurant Status ---")
        print(f"Restaurant: {self.restaurant.name}")
        print(f"Total Tables: {self.restaurant.get_total_tables()}")
        print(f"Available Tables: {self.restaurant.get_available_tables_count()}")
        print(f"Occupied Tables: {self.restaurant.get_occupied_tables_count()}")
    
    def _get_int_input(self, prompt="") -> int:
        """Get integer input with validation"""
        while True:
            try:
                return int(input(prompt))
            except ValueError:
                print("Please enter a valid number: ", end="")
    
    def _get_float_input(self, prompt="") -> float:
        """Get float input with validation"""
        while True:
            try:
                return float(input(prompt))
            except ValueError:
                print("Please enter a valid amount: ", end="")

if __name__ == "__main__":
    restaurant_system = RestaurantManagementSystem()
    restaurant_system.start()
