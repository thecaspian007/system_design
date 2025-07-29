package com.systemdesign.lld.restaurant;

/**
 * Represents a menu item in the restaurant
 */
public class MenuItem {
    private String id;
    private String name;
    private String description;
    private double price;
    private MenuCategory category;
    private boolean available;
    private int preparationTime; // in minutes
    
    public MenuItem(String id, String name, String description, double price, 
                   MenuCategory category, int preparationTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.preparationTime = preparationTime;
        this.available = true;
    }
    
    public boolean isAvailable() {
        return available;
    }
    
    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public MenuCategory getCategory() { return category; }
    public int getPreparationTime() { return preparationTime; }
    
    @Override
    public String toString() {
        return String.format("%s - $%.2f (%s)", name, price, 
                           available ? "Available" : "Unavailable");
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MenuItem menuItem = (MenuItem) obj;
        return id.equals(menuItem.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

enum MenuCategory {
    APPETIZER,
    MAIN_COURSE,
    DESSERT,
    BEVERAGE,
    SIDES
} 