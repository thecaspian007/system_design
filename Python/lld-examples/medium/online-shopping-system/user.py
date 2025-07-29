from datetime import datetime
from typing import List
from shopping_cart import ShoppingCart

class User:
    """User class representing a user in the shopping system"""
    
    def __init__(self, user_id: str, email: str, name: str, address: str, password: str):
        self.user_id = user_id
        self.email = email
        self.name = name
        self.address = address
        self.password = password
        self.created_at = datetime.now()
        self.cart = ShoppingCart()
        self.order_history: List[str] = []  # List of order IDs
    
    def validate_password(self, password: str) -> bool:
        """Validate password"""
        return self.password == password
    
    def add_order(self, order_id: str):
        """Add order to user's history"""
        self.order_history.append(order_id)
    
    def __str__(self):
        return f"User(id={self.user_id}, name={self.name}, email={self.email})" 