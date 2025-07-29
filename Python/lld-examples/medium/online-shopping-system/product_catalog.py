from typing import List, Dict, Optional
from product import Product

class ProductCatalog:
    """Product catalog class for managing products"""
    
    def __init__(self):
        self.products: Dict[str, Product] = {}
        self.products_by_name: Dict[str, Product] = {}
    
    def add_product(self, product: Product) -> bool:
        """Add product to catalog"""
        if product.product_id in self.products:
            return False  # Product already exists
        
        self.products[product.product_id] = product
        self.products_by_name[product.name.lower()] = product
        return True
    
    def get_product_by_id(self, product_id: str) -> Optional[Product]:
        """Get product by product ID"""
        return self.products.get(product_id)
    
    def get_product_by_name(self, name: str) -> Optional[Product]:
        """Get product by name"""
        return self.products_by_name.get(name.lower())
    
    def get_all_products(self) -> List[Product]:
        """Get all products"""
        return list(self.products.values())
    
    def search_products(self, keyword: str) -> List[Product]:
        """Search products by keyword"""
        keyword = keyword.lower()
        results = []
        
        for product in self.products.values():
            if (keyword in product.name.lower() or 
                keyword in product.description.lower()):
                results.append(product)
        
        return results
    
    def get_products_by_category(self, category: str) -> List[Product]:
        """Get products by category (simplified - could be enhanced)"""
        # This is a simplified implementation
        return [p for p in self.products.values() if category.lower() in p.name.lower()]
    
    def update_product_stock(self, product_id: str, stock: int) -> bool:
        """Update product stock"""
        product = self.get_product_by_id(product_id)
        if product:
            product.stock = stock
            return True
        return False
    
    def update_product_price(self, product_id: str, price: float) -> bool:
        """Update product price"""
        product = self.get_product_by_id(product_id)
        if product:
            product.price = price
            return True
        return False
    
    def remove_product(self, product_id: str) -> bool:
        """Remove product from catalog"""
        product = self.get_product_by_id(product_id)
        if product:
            del self.products[product_id]
            del self.products_by_name[product.name.lower()]
            return True
        return False
    
    def get_available_products(self) -> List[Product]:
        """Get available products only"""
        return [p for p in self.products.values() if p.is_available and p.stock > 0]
    
    def get_total_products(self) -> int:
        """Get total number of products"""
        return len(self.products) 