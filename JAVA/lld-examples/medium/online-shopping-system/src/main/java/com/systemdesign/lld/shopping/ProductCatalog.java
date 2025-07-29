package com.systemdesign.lld.shopping;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * ProductCatalog manages the product inventory and catalog operations
 */
public class ProductCatalog {
    private Map<String, Product> products;
    private Map<String, List<Product>> categoryProducts;
    
    public ProductCatalog() {
        this.products = new ConcurrentHashMap<>();
        this.categoryProducts = new ConcurrentHashMap<>();
    }
    
    /**
     * Add a new product to the catalog
     */
    public boolean addProduct(Product product) {
        if (products.containsKey(product.getProductId())) {
            return false; // Product already exists
        }
        
        products.put(product.getProductId(), product);
        
        // Add to category mapping
        String category = product.getCategory();
        categoryProducts.computeIfAbsent(category, k -> new ArrayList<>()).add(product);
        
        return true;
    }
    
    /**
     * Remove a product from the catalog
     */
    public boolean removeProduct(String productId) {
        Product product = products.remove(productId);
        if (product != null) {
            // Remove from category mapping
            String category = product.getCategory();
            List<Product> categoryList = categoryProducts.get(category);
            if (categoryList != null) {
                categoryList.remove(product);
                if (categoryList.isEmpty()) {
                    categoryProducts.remove(category);
                }
            }
            return true;
        }
        return false;
    }
    
    /**
     * Get product by ID
     */
    public Product getProduct(String productId) {
        return products.get(productId);
    }
    
    /**
     * Get all products
     */
    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }
    
    /**
     * Get products by category
     */
    public List<Product> getProductsByCategory(String category) {
        return categoryProducts.getOrDefault(category, new ArrayList<>());
    }
    
    /**
     * Search products by name
     */
    public List<Product> searchProductsByName(String searchTerm) {
        return products.values().stream()
                .filter(product -> product.getName().toLowerCase().contains(searchTerm.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    /**
     * Get products within price range
     */
    public List<Product> getProductsByPriceRange(double minPrice, double maxPrice) {
        return products.values().stream()
                .filter(product -> product.getPrice() >= minPrice && product.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all available categories
     */
    public Set<String> getAllCategories() {
        return new HashSet<>(categoryProducts.keySet());
    }
    
    /**
     * Update product stock
     */
    public boolean updateProductStock(String productId, int newStock) {
        Product product = products.get(productId);
        if (product != null) {
            product.updateStock(newStock);
            return true;
        }
        return false;
    }
    
    /**
     * Check if product is available
     */
    public boolean isProductAvailable(String productId, int quantity) {
        Product product = products.get(productId);
        return product != null && product.isAvailable(quantity);
    }
    
    /**
     * Get low stock products (stock < threshold)
     */
    public List<Product> getLowStockProducts(int threshold) {
        return products.values().stream()
                .filter(product -> product.getStock() < threshold)
                .collect(Collectors.toList());
    }
    
    /**
     * Get total number of products
     */
    public int getTotalProducts() {
        return products.size();
    }
    
    /**
     * Get catalog statistics
     */
    public Map<String, Object> getCatalogStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProducts", products.size());
        stats.put("totalCategories", categoryProducts.size());
        stats.put("lowStockProducts", getLowStockProducts(5).size());
        stats.put("totalValue", products.values().stream()
                .mapToDouble(p -> p.getPrice() * p.getStock())
                .sum());
        
        return stats;
    }
} 