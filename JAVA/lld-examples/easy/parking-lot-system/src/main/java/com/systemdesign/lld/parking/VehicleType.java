package com.systemdesign.lld.parking;

/**
 * Enum representing different types of vehicles
 */
public enum VehicleType {
    CAR("Car", 2.0),
    MOTORCYCLE("Motorcycle", 1.0),
    TRUCK("Truck", 4.0);
    
    private final String displayName;
    private final double hourlyRate;
    
    VehicleType(String displayName, double hourlyRate) {
        this.displayName = displayName;
        this.hourlyRate = hourlyRate;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public double getHourlyRate() {
        return hourlyRate;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
} 