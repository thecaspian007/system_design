from datetime import datetime
import uuid

class Customer:
    """Customer class representing a restaurant customer"""
    
    def __init__(self, name: str, phone: str, email: str = ""):
        self.customer_id = str(uuid.uuid4())[:8].upper()
        self.name = name
        self.phone = phone
        self.email = email
        self.visit_count = 0
        self.total_spent = 0.0
        self.created_at = datetime.now()
        self.order_history = []
    
    def add_order(self, order):
        """Add order to customer history"""
        self.order_history.append(order)
        self.visit_count += 1
        self.total_spent += order.get_total()
    
    def get_average_order_value(self) -> float:
        """Get average order value"""
        if self.visit_count == 0:
            return 0.0
        return self.total_spent / self.visit_count
    
    def __str__(self):
        return f"Customer {self.name} ({self.phone})" 