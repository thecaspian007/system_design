from typing import List, Dict, Optional
from order import Order, OrderItem
from user import User
from shopping_cart import ShoppingCart
from payment import PaymentMethod

class OrderManager:
    """Order manager class for handling orders"""
    
    def __init__(self):
        self.orders: Dict[str, Order] = {}
        self.orders_by_user: Dict[str, List[Order]] = {}
    
    def create_order(self, user: User, cart: ShoppingCart, payment_method: PaymentMethod) -> Optional[Order]:
        """Create order from cart"""
        if cart.is_empty():
            return None
        
        # Create order items
        order_items = []
        for cart_item in cart.get_items():
            product = cart_item.product
            
            # Check stock availability
            if not product.is_in_stock(cart_item.quantity):
                return None
            
            # Reduce stock
            product.reduce_stock(cart_item.quantity)
            
            # Create order item
            order_item = OrderItem(product, cart_item.quantity, product.price)
            order_items.append(order_item)
        
        # Create order
        order = Order(user.user_id, order_items, payment_method)
        
        # Store order
        self.orders[order.order_id] = order
        
        if user.user_id not in self.orders_by_user:
            self.orders_by_user[user.user_id] = []
        self.orders_by_user[user.user_id].append(order)
        
        # Add to user's order history
        user.add_order(order.order_id)
        
        return order
    
    def get_order_by_id(self, order_id: str) -> Optional[Order]:
        """Get order by order ID"""
        return self.orders.get(order_id)
    
    def get_orders_by_user(self, user: User) -> List[Order]:
        """Get orders by user"""
        return self.orders_by_user.get(user.user_id, [])
    
    def update_order_status(self, order_id: str, status: str) -> bool:
        """Update order status"""
        order = self.get_order_by_id(order_id)
        if order:
            # This is simplified - in real implementation, you'd have proper status validation
            return True
        return False
    
    def cancel_order(self, order_id: str) -> bool:
        """Cancel order"""
        order = self.get_order_by_id(order_id)
        if order:
            order.cancel_order()
            
            # Restore stock
            for item in order.items:
                item.product.increase_stock(item.quantity)
            
            return True
        return False
    
    def get_all_orders(self) -> List[Order]:
        """Get all orders"""
        return list(self.orders.values())
    
    def get_order_count(self) -> int:
        """Get total order count"""
        return len(self.orders) 