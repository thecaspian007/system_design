package com.systemdesign.lld.shopping;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Product class representing an item in the shopping system
 */
public class Product {
    private String productId;
    private String name;
    private String description;
    private double price;
    private int stock;
    private String category;
    private String brand;
    private double weight;
    private String dimensions;
    private List<String> images;
    private Map<String, String> specifications;
    private ProductStatus status;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdated;
    private double averageRating;
    private int totalReviews;
    private List<Review> reviews;
    private double discountPercentage;
    private LocalDateTime discountExpiry;
    private int minOrderQuantity;
    private int maxOrderQuantity;
    private boolean isDigital;
    
    public Product(String productId, String name, String description, double price, int stock) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.status = ProductStatus.ACTIVE;
        this.createdDate = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
        this.images = new ArrayList<>();
        this.specifications = new HashMap<>();
        this.reviews = new ArrayList<>();
        this.averageRating = 0.0;
        this.totalReviews = 0;
        this.discountPercentage = 0.0;
        this.minOrderQuantity = 1;
        this.maxOrderQuantity = stock;
        this.isDigital = false;
    }
    
    public Product(String productId, String name, String description, double price, int stock, 
                   String category, String brand) {
        this(productId, name, description, price, stock);
        this.category = category;
        this.brand = brand;
    }
    
    public boolean isInStock() {
        return stock > 0 && status == ProductStatus.ACTIVE;
    }
    
    public boolean hasStock(int quantity) {
        return stock >= quantity;
    }
    
    public boolean isAvailable(int quantity) {
        return status == ProductStatus.ACTIVE && hasStock(quantity);
    }
    
    public void addStock(int quantity) {
        increaseStock(quantity);
    }
    
    public void updateStock(int newStock) {
        this.stock = newStock;
        this.lastUpdated = LocalDateTime.now();
        
        if (stock == 0) {
            this.status = ProductStatus.OUT_OF_STOCK;
        } else if (status == ProductStatus.OUT_OF_STOCK) {
            this.status = ProductStatus.ACTIVE;
        }
    }
    
    public void reduceStock(int quantity) {
        if (stock < quantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }
        updateStock(stock - quantity);
    }
    
    public void increaseStock(int quantity) {
        updateStock(stock + quantity);
    }
    
    public void updatePrice(double newPrice) {
        this.price = newPrice;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public void applyDiscount(double discountPercentage, LocalDateTime expiry) {
        this.discountPercentage = discountPercentage;
        this.discountExpiry = expiry;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public void removeDiscount() {
        this.discountPercentage = 0.0;
        this.discountExpiry = null;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public double getDiscountedPrice() {
        if (isDiscountActive()) {
            return price * (1 - discountPercentage / 100);
        }
        return price;
    }
    
    public boolean isDiscountActive() {
        return discountPercentage > 0 && 
               (discountExpiry == null || discountExpiry.isAfter(LocalDateTime.now()));
    }
    
    public void addReview(Review review) {
        reviews.add(review);
        recalculateRating();
    }
    
    private void recalculateRating() {
        if (reviews.isEmpty()) {
            this.averageRating = 0.0;
            this.totalReviews = 0;
            return;
        }
        
        double sum = reviews.stream().mapToDouble(Review::getRating).sum();
        this.averageRating = sum / reviews.size();
        this.totalReviews = reviews.size();
    }
    
    public void addImage(String imageUrl) {
        images.add(imageUrl);
    }
    
    public void removeImage(String imageUrl) {
        images.remove(imageUrl);
    }
    
    public void addSpecification(String key, String value) {
        specifications.put(key, value);
    }
    
    public void removeSpecification(String key) {
        specifications.remove(key);
    }
    
    public void setStatus(ProductStatus status) {
        this.status = status;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public void discontinue() {
        this.status = ProductStatus.DISCONTINUED;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public boolean canOrder(int quantity) {
        return isInStock() && 
               quantity >= minOrderQuantity && 
               quantity <= maxOrderQuantity &&
               hasStock(quantity);
    }
    
    public double calculateTotalPrice(int quantity) {
        return getDiscountedPrice() * quantity;
    }
    
    public boolean isLowStock() {
        return stock > 0 && stock <= 10; // Consider low stock when <= 10 items
    }
    
    public Map<String, Object> getProductSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("productId", productId);
        summary.put("name", name);
        summary.put("price", price);
        summary.put("discountedPrice", getDiscountedPrice());
        summary.put("stock", stock);
        summary.put("category", category);
        summary.put("brand", brand);
        summary.put("status", status);
        summary.put("averageRating", averageRating);
        summary.put("totalReviews", totalReviews);
        summary.put("isDiscountActive", isDiscountActive());
        summary.put("isLowStock", isLowStock());
        
        return summary;
    }
    
    // Getters
    public String getProductId() { return productId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public String getCategory() { return category; }
    public String getBrand() { return brand; }
    public double getWeight() { return weight; }
    public String getDimensions() { return dimensions; }
    public List<String> getImages() { return new ArrayList<>(images); }
    public Map<String, String> getSpecifications() { return new HashMap<>(specifications); }
    public ProductStatus getStatus() { return status; }
    public LocalDateTime getCreatedDate() { return createdDate; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public double getAverageRating() { return averageRating; }
    public int getTotalReviews() { return totalReviews; }
    public List<Review> getReviews() { return new ArrayList<>(reviews); }
    public double getDiscountPercentage() { return discountPercentage; }
    public LocalDateTime getDiscountExpiry() { return discountExpiry; }
    public int getMinOrderQuantity() { return minOrderQuantity; }
    public int getMaxOrderQuantity() { return maxOrderQuantity; }
    public boolean isDigital() { return isDigital; }
    
    // Setters
    public void setName(String name) { this.name = name; this.lastUpdated = LocalDateTime.now(); }
    public void setDescription(String description) { this.description = description; this.lastUpdated = LocalDateTime.now(); }
    public void setCategory(String category) { this.category = category; this.lastUpdated = LocalDateTime.now(); }
    public void setBrand(String brand) { this.brand = brand; this.lastUpdated = LocalDateTime.now(); }
    public void setWeight(double weight) { this.weight = weight; this.lastUpdated = LocalDateTime.now(); }
    public void setDimensions(String dimensions) { this.dimensions = dimensions; this.lastUpdated = LocalDateTime.now(); }
    public void setMinOrderQuantity(int minOrderQuantity) { this.minOrderQuantity = minOrderQuantity; }
    public void setMaxOrderQuantity(int maxOrderQuantity) { this.maxOrderQuantity = maxOrderQuantity; }
    public void setDigital(boolean digital) { this.isDigital = digital; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(productId, product.productId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }
    
    @Override
    public String toString() {
        return "Product{" +
                "productId='" + productId + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", discountedPrice=" + getDiscountedPrice() +
                ", stock=" + stock +
                ", category='" + category + '\'' +
                ", status=" + status +
                ", averageRating=" + averageRating +
                '}';
    }
}

/**
 * Review class for product reviews
 */
class Review {
    private String reviewId;
    private String userId;
    private String productId;
    private int rating; // 1-5 stars
    private String comment;
    private LocalDateTime reviewDate;
    private boolean isVerifiedPurchase;
    private int helpfulVotes;
    
    public Review(String reviewId, String userId, String productId, int rating, String comment) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.productId = productId;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = LocalDateTime.now();
        this.isVerifiedPurchase = false;
        this.helpfulVotes = 0;
        
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
    }
    
    public void markAsVerifiedPurchase() {
        this.isVerifiedPurchase = true;
    }
    
    public void addHelpfulVote() {
        this.helpfulVotes++;
    }
    
    // Getters
    public String getReviewId() { return reviewId; }
    public String getUserId() { return userId; }
    public String getProductId() { return productId; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public LocalDateTime getReviewDate() { return reviewDate; }
    public boolean isVerifiedPurchase() { return isVerifiedPurchase; }
    public int getHelpfulVotes() { return helpfulVotes; }
    
    @Override
    public String toString() {
        return "Review{" +
                "reviewId='" + reviewId + '\'' +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", isVerifiedPurchase=" + isVerifiedPurchase +
                ", helpfulVotes=" + helpfulVotes +
                '}';
    }
}

enum ProductStatus {
    ACTIVE,
    INACTIVE,
    OUT_OF_STOCK,
    DISCONTINUED,
    COMING_SOON
} 