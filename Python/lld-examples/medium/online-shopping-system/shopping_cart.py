from typing import List, Dict, Optional
from product import Product

class CartItem:
    """Cart item class"""
    
    def __init__(self, product: Product, quantity: int):
        self.product = product
        self.quantity = quantity
    
    def get_total_price(self) -> float:
        """Get total price for this item"""
        return self.product.price * self.quantity
    
    def __str__(self):
        return f"{self.product.name} x{self.quantity} - ${self.get_total_price():.2f}"

class ShoppingCart:
    """Shopping cart class"""
    
    def __init__(self):
        self.items: List[CartItem] = []
        self.items_by_product: Dict[str, CartItem] = {}
    
    def add_item(self, product: Product, quantity: int) -> bool:
        """Add item to cart"""
        if not product.is_in_stock(quantity):
            return False
        
        if product.product_id in self.items_by_product:
            # Update existing item
            existing_item = self.items_by_product[product.product_id]
            new_quantity = existing_item.quantity + quantity
            
            if not product.is_in_stock(new_quantity):
                return False
            
            existing_item.quantity = new_quantity
        else:
            # Add new item
            cart_item = CartItem(product, quantity)
            self.items.append(cart_item)
            self.items_by_product[product.product_id] = cart_item
        
        return True
    
    def remove_item(self, product: Product):
        """Remove item from cart"""
        if product.product_id in self.items_by_product:
            cart_item = self.items_by_product[product.product_id]
            self.items.remove(cart_item)
            del self.items_by_product[product.product_id]
    
    def update_quantity(self, product: Product, quantity: int) -> bool:
        """Update item quantity"""
        if product.product_id in self.items_by_product:
            if quantity <= 0:
                self.remove_item(product)
                return True
            
            if not product.is_in_stock(quantity):
                return False
            
            self.items_by_product[product.product_id].quantity = quantity
            return True
        return False
    
    def clear_cart(self):
        """Clear cart"""
        self.items.clear()
        self.items_by_product.clear()
    
    def get_items(self) -> List[CartItem]:
        """Get all items in cart"""
        return self.items.copy()
    
    def get_total_price(self) -> float:
        """Get total price of cart"""
        return sum(item.get_total_price() for item in self.items)
    
    def get_item_count(self) -> int:
        """Get total number of items in cart"""
        return sum(item.quantity for item in self.items)
    
    def is_empty(self) -> bool:
        """Check if cart is empty"""
        return len(self.items) == 0
    
    def display_cart(self):
        """Display cart contents"""
        if self.is_empty():
            print("Cart is empty")
            return
        
        print("Cart contents:")
        for item in self.items:
            print(f"  {item}")
        print(f"Total items: {self.get_item_count()}")
        print(f"Total price: ${self.get_total_price():.2f}")
    
    def __str__(self):
        return f"Cart({self.get_item_count()} items, ${self.get_total_price():.2f})" 