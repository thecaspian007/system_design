from datetime import datetime
from typing import List
from enum import Enum
import uuid
from product import Product
from payment import PaymentMethod

class OrderStatus(Enum):
    """Order status enum"""
    PENDING = "Pending"
    CONFIRMED = "Confirmed"
    SHIPPED = "Shipped"
    DELIVERED = "Delivered"
    CANCELLED = "Cancelled"

class OrderItem:
    """Order item class"""
    
    def __init__(self, product: Product, quantity: int, price: float):
        self.product = product
        self.quantity = quantity
        self.price = price  # Price at time of order
    
    def get_total_price(self) -> float:
        """Get total price for this item"""
        return self.price * self.quantity
    
    def __str__(self):
        return f"{self.product.name} x{self.quantity} @ ${self.price:.2f} = ${self.get_total_price():.2f}"

class Order:
    """Order class representing an order"""
    
    def __init__(self, user_id: str, items: List[OrderItem], payment_method: PaymentMethod):
        self.order_id = str(uuid.uuid4())[:8].upper()
        self.user_id = user_id
        self.items = items
        self.payment_method = payment_method
        self.status = OrderStatus.PENDING
        self.order_date = datetime.now()
        self.total_amount = sum(item.get_total_price() for item in items)
        self.shipping_address = ""
        self.tracking_number = ""
    
    def confirm_order(self):
        """Confirm order"""
        self.status = OrderStatus.CONFIRMED
    
    def ship_order(self, tracking_number: str):
        """Ship order"""
        self.status = OrderStatus.SHIPPED
        self.tracking_number = tracking_number
    
    def deliver_order(self):
        """Deliver order"""
        self.status = OrderStatus.DELIVERED
    
    def cancel_order(self):
        """Cancel order"""
        self.status = OrderStatus.CANCELLED
    
    def get_formatted_order_date(self) -> str:
        """Get formatted order date"""
        return self.order_date.strftime("%Y-%m-%d %H:%M:%S")
    
    def __str__(self):
        return f"Order {self.order_id} - {self.status.value} - ${self.total_amount:.2f}" 