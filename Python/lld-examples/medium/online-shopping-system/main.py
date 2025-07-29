import sys
import os

# Add the current directory to the Python path
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

from user_manager import UserManager
from product_catalog import ProductCatalog
from order_manager import OrderManager
from product import Product
from payment import PaymentMethod

class OnlineShoppingSystem:
    """
    Online Shopping System - Main Application
    
    This system demonstrates:
    - User management and authentication
    - Product catalog and inventory
    - Shopping cart functionality
    - Order processing and payment
    - Order history and tracking
    
    @author System Design Repository
    """
    
    def __init__(self):
        self.user_manager = UserManager()
        self.product_catalog = ProductCatalog()
        self.order_manager = OrderManager()
        self.current_user = None
        
        self._initialize_system()
    
    def _initialize_system(self):
        """Initialize system with sample data"""
        # Initialize sample users
        self.user_manager.register_user("john@example.com", "John Doe", "123 Main St", "password123")
        self.user_manager.register_user("alice@example.com", "Alice Smith", "456 Oak Ave", "password456")
        
        # Initialize sample products
        self.product_catalog.add_product(Product("LAPTOP001", "Gaming Laptop", "High-performance gaming laptop", 1299.99, 10))
        self.product_catalog.add_product(Product("PHONE001", "Smartphone", "Latest model smartphone", 699.99, 25))
        self.product_catalog.add_product(Product("HEADPHONE001", "Wireless Headphones", "Noise-cancelling headphones", 199.99, 15))
        self.product_catalog.add_product(Product("MOUSE001", "Gaming Mouse", "RGB gaming mouse", 79.99, 30))
        self.product_catalog.add_product(Product("KEYBOARD001", "Mechanical Keyboard", "RGB mechanical keyboard", 149.99, 20))
        
        print("Online Shopping System initialized!")
    
    def start(self):
        """Start the shopping system"""
        print("=== Welcome to Online Shopping System ===")
        
        while True:
            try:
                if self.current_user is None:
                    self._show_guest_menu()
                else:
                    self._show_user_menu()
                
                choice = self._get_int_input()
                
                if self.current_user is None:
                    self._handle_guest_choice(choice)
                else:
                    self._handle_user_choice(choice)
                    
            except KeyboardInterrupt:
                print("\nThank you for visiting!")
                break
            except Exception as e:
                print(f"Error: {e}")
    
    def _show_guest_menu(self):
        """Show guest menu"""
        print("\n--- Guest Menu ---")
        print("1. Register")
        print("2. Login")
        print("3. Browse Products")
        print("4. Exit")
        print("Choose an option: ", end="")
    
    def _show_user_menu(self):
        """Show user menu"""
        print(f"\n--- Welcome, {self.current_user.name} ---")
        print("1. Browse Products")
        print("2. View Cart")
        print("3. View Orders")
        print("4. Account Settings")
        print("5. Logout")
        print("Choose an option: ", end="")
    
    def _handle_guest_choice(self, choice: int):
        """Handle guest menu choice"""
        if choice == 1:
            self._handle_registration()
        elif choice == 2:
            self._handle_login()
        elif choice == 3:
            self._browse_products()
        elif choice == 4:
            print("Thank you for visiting!")
            sys.exit(0)
        else:
            print("Invalid choice. Please try again.")
    
    def _handle_user_choice(self, choice: int):
        """Handle user menu choice"""
        if choice == 1:
            self._browse_products()
        elif choice == 2:
            self._view_cart()
        elif choice == 3:
            self._view_orders()
        elif choice == 4:
            self._account_settings()
        elif choice == 5:
            self._logout()
        else:
            print("Invalid choice. Please try again.")
    
    def _handle_registration(self):
        """Handle user registration"""
        print("\n--- User Registration ---")
        email = input("Enter email: ")
        name = input("Enter name: ")
        address = input("Enter address: ")
        password = input("Enter password: ")
        
        user = self.user_manager.register_user(email, name, address, password)
        if user:
            print("Registration successful! You can now login.")
        else:
            print("Registration failed. Email might already be in use.")
    
    def _handle_login(self):
        """Handle user login"""
        print("\n--- User Login ---")
        email = input("Enter email: ")
        password = input("Enter password: ")
        
        user = self.user_manager.authenticate_user(email, password)
        if user:
            self.current_user = user
            print(f"Login successful! Welcome, {user.name}")
        else:
            print("Invalid email or password.")
    
    def _browse_products(self):
        """Browse products"""
        print("\n--- Product Catalog ---")
        products = self.product_catalog.get_all_products()
        
        if not products:
            print("No products available.")
            return
        
        for i, product in enumerate(products, 1):
            print(f"{i}. {product.name} - ${product.price:.2f} (Stock: {product.stock})")
            print(f"   {product.description}")
        
        if self.current_user:
            product_choice = self._get_int_input("\nEnter product number to add to cart (0 to go back): ")
            
            if product_choice > 0 and product_choice <= len(products):
                selected_product = products[product_choice - 1]
                quantity = self._get_int_input("Enter quantity: ")
                
                if self.current_user.cart.add_item(selected_product, quantity):
                    print("Product added to cart successfully!")
                else:
                    print("Failed to add product to cart. Check stock availability.")
    
    def _view_cart(self):
        """View shopping cart"""
        print("\n--- Shopping Cart ---")
        cart = self.current_user.cart
        
        if cart.is_empty():
            print("Your cart is empty.")
            return
        
        cart.display_cart()
        print(f"Total: ${cart.get_total_price():.2f}")
        
        print("\n1. Proceed to Checkout")
        print("2. Remove Item")
        print("3. Clear Cart")
        print("4. Go Back")
        
        choice = self._get_int_input("Choose an option: ")
        
        if choice == 1:
            self._proceed_to_checkout()
        elif choice == 2:
            self._remove_item_from_cart()
        elif choice == 3:
            cart.clear_cart()
            print("Cart cleared.")
        elif choice == 4:
            pass  # Go back
        else:
            print("Invalid choice.")
    
    def _proceed_to_checkout(self):
        """Proceed to checkout"""
        cart = self.current_user.cart
        
        if cart.is_empty():
            print("Cart is empty. Cannot proceed to checkout.")
            return
        
        print("\n--- Checkout ---")
        print(f"Total Amount: ${cart.get_total_price():.2f}")
        
        print("Select Payment Method:")
        print("1. Credit Card")
        print("2. PayPal")
        print("3. Bank Transfer")
        
        payment_choice = self._get_int_input("Choice: ")
        payment_method = self._get_payment_method(payment_choice)
        
        if payment_method is None:
            print("Invalid payment method.")
            return
        
        order = self.order_manager.create_order(self.current_user, cart, payment_method)
        
        if order:
            print("Order placed successfully!")
            print(f"Order ID: {order.order_id}")
            print(f"Total: ${order.total_amount:.2f}")
            cart.clear_cart()
        else:
            print("Order placement failed. Please try again.")
    
    def _remove_item_from_cart(self):
        """Remove item from cart"""
        cart = self.current_user.cart
        items = cart.get_items()
        
        if not items:
            print("Cart is empty.")
            return
        
        print("Select item to remove:")
        for i, item in enumerate(items, 1):
            print(f"{i}. {item.product.name} (Quantity: {item.quantity})")
        
        item_choice = self._get_int_input("Enter item number: ")
        
        if 1 <= item_choice <= len(items):
            item = items[item_choice - 1]
            cart.remove_item(item.product)
            print("Item removed from cart.")
        else:
            print("Invalid item number.")
    
    def _view_orders(self):
        """View order history"""
        print("\n--- Order History ---")
        orders = self.order_manager.get_orders_by_user(self.current_user)
        
        if not orders:
            print("No orders found.")
            return
        
        for order in orders:
            print(f"Order ID: {order.order_id}")
            print(f"Date: {order.get_formatted_order_date()}")
            print(f"Status: {order.status.value}")
            print(f"Total: ${order.total_amount:.2f}")
            print("Items:")
            for item in order.items:
                print(f"  - {item.product.name} x{item.quantity} @ ${item.price:.2f}")
            print("---")
    
    def _account_settings(self):
        """Account settings"""
        print("\n--- Account Settings ---")
        print(f"Name: {self.current_user.name}")
        print(f"Email: {self.current_user.email}")
        print(f"Address: {self.current_user.address}")
        
        print("\n1. Update Address")
        print("2. Change Password")
        print("3. Go Back")
        
        choice = self._get_int_input("Choose an option: ")
        
        if choice == 1:
            self._update_address()
        elif choice == 2:
            self._change_password()
        elif choice == 3:
            pass  # Go back
        else:
            print("Invalid choice.")
    
    def _update_address(self):
        """Update address"""
        new_address = input("Enter new address: ")
        self.current_user.address = new_address
        print("Address updated successfully!")
    
    def _change_password(self):
        """Change password"""
        current_password = input("Enter current password: ")
        
        if not self.current_user.validate_password(current_password):
            print("Current password is incorrect.")
            return
        
        new_password = input("Enter new password: ")
        self.current_user.password = new_password
        print("Password changed successfully!")
    
    def _logout(self):
        """Logout user"""
        self.current_user = None
        print("Logged out successfully!")
    
    def _get_payment_method(self, choice: int) -> PaymentMethod:
        """Get payment method from choice"""
        if choice == 1:
            return PaymentMethod.CREDIT_CARD
        elif choice == 2:
            return PaymentMethod.PAYPAL
        elif choice == 3:
            return PaymentMethod.BANK_TRANSFER
        else:
            return None
    
    def _get_int_input(self, prompt: str = "") -> int:
        """Get integer input with validation"""
        while True:
            try:
                return int(input(prompt))
            except ValueError:
                print("Please enter a valid number: ", end="")

if __name__ == "__main__":
    shopping_system = OnlineShoppingSystem()
    shopping_system.start() 