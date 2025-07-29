from typing import List, Dict, Optional
from datetime import datetime

class MenuItem:
    """Menu item class"""
    
    def __init__(self, item_id: str, name: str, description: str, price: float, category: str):
        self.item_id = item_id
        self.name = name
        self.description = description
        self.price = price
        self.category = category
        self.is_available = True
        self.created_at = datetime.now()
    
    def set_availability(self, available: bool):
        """Set item availability"""
        self.is_available = available
    
    def __str__(self):
        return f"{self.name} - ${self.price:.2f} ({self.category})"

class Menu:
    """Menu class managing menu items"""
    
    def __init__(self):
        self.items: Dict[str, MenuItem] = {}
        self.categories: Dict[str, List[MenuItem]] = {}
    
    def add_item(self, item: MenuItem):
        """Add item to menu"""
        self.items[item.item_id] = item
        
        if item.category not in self.categories:
            self.categories[item.category] = []
        self.categories[item.category].append(item)
    
    def get_item(self, item_id: str) -> Optional[MenuItem]:
        """Get item by ID"""
        return self.items.get(item_id)
    
    def get_items_by_category(self, category: str) -> List[MenuItem]:
        """Get items by category"""
        return self.categories.get(category, [])
    
    def get_all_items(self) -> List[MenuItem]:
        """Get all items"""
        return list(self.items.values())
    
    def get_categories(self) -> List[str]:
        """Get all categories"""
        return list(self.categories.keys())
    
    def remove_item(self, item_id: str) -> bool:
        """Remove item from menu"""
        if item_id in self.items:
            item = self.items[item_id]
            del self.items[item_id]
            self.categories[item.category].remove(item)
            return True
        return False
    
    def update_price(self, item_id: str, new_price: float) -> bool:
        """Update item price"""
        item = self.get_item(item_id)
        if item:
            item.price = new_price
            return True
        return False 