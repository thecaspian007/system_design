from datetime import datetime

class Product:
    """Product class representing a product in the shopping system"""
    
    def __init__(self, product_id: str, name: str, description: str, price: float, stock: int):
        self.product_id = product_id
        self.name = name
        self.description = description
        self.price = price
        self.stock = stock
        self.created_at = datetime.now()
        self.is_available = True
    
    def is_in_stock(self, quantity: int = 1) -> bool:
        """Check if product is in stock"""
        return self.is_available and self.stock >= quantity
    
    def reduce_stock(self, quantity: int) -> bool:
        """Reduce stock by quantity"""
        if self.stock >= quantity:
            self.stock -= quantity
            return True
        return False
    
    def increase_stock(self, quantity: int):
        """Increase stock by quantity"""
        self.stock += quantity
    
    def set_availability(self, available: bool):
        """Set product availability"""
        self.is_available = available
    
    def __str__(self):
        return f"Product(id={self.product_id}, name={self.name}, price=${self.price:.2f}, stock={self.stock})" 