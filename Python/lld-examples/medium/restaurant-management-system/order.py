from typing import List
from enum import Enum
from datetime import datetime
import uuid
from menu import MenuItem

class OrderStatus(Enum):
    """Order status enum"""
    PENDING = "Pending"
    PREPARING = "Preparing"
    READY = "Ready"
    SERVED = "Served"
    PAID = "Paid"
    CANCELLED = "Cancelled"

class OrderItem:
    """Order item class"""
    
    def __init__(self, menu_item: MenuItem, quantity: int):
        self.menu_item = menu_item
        self.quantity = quantity
        self.unit_price = menu_item.price
        self.special_instructions = ""
    
    def get_total(self) -> float:
        """Get total price for this item"""
        return self.unit_price * self.quantity
    
    def __str__(self):
        return f"{self.menu_item.name} x{self.quantity} - ${self.get_total():.2f}"

class Order:
    """Order class representing a customer order"""
    
    def __init__(self, table, customer):
        self.order_id = str(uuid.uuid4())[:8].upper()
        self.table = table
        self.customer = customer
        self.items: List[OrderItem] = []
        self.status = OrderStatus.PENDING
        self.order_time = datetime.now()
        self.special_requests = ""
    
    def add_item(self, menu_item: MenuItem, quantity: int, special_instructions: str = ""):
        """Add item to order"""
        order_item = OrderItem(menu_item, quantity)
        if special_instructions:
            order_item.special_instructions = special_instructions
        self.items.append(order_item)
    
    def remove_item(self, menu_item: MenuItem):
        """Remove item from order"""
        self.items = [item for item in self.items if item.menu_item.item_id != menu_item.item_id]
    
    def get_total(self) -> float:
        """Get total order amount"""
        return sum(item.get_total() for item in self.items)
    
    def get_total_with_tax(self, tax_rate: float = 0.08) -> float:
        """Get total with tax"""
        subtotal = self.get_total()
        return subtotal * (1 + tax_rate)
    
    def update_status(self, status: OrderStatus):
        """Update order status"""
        self.status = status
    
    def mark_as_paid(self):
        """Mark order as paid"""
        self.status = OrderStatus.PAID
    
    def cancel_order(self):
        """Cancel order"""
        self.status = OrderStatus.CANCELLED
    
    def get_formatted_order_time(self) -> str:
        """Get formatted order time"""
        return self.order_time.strftime("%Y-%m-%d %H:%M:%S")
    
    def __str__(self):
        return f"Order {self.order_id} - Table {self.table.table_number} - {self.status.value} - ${self.get_total():.2f}" 