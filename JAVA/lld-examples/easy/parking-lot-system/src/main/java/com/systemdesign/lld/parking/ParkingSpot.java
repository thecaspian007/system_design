package com.systemdesign.lld.parking;

/**
 * Parking spot class
 */
public class ParkingSpot {
    private int spotNumber;
    private int floorNumber;
    private VehicleType vehicleType;
    private boolean isOccupied;
    private Vehicle parkedVehicle;
    private double hourlyRate;
    
    public ParkingSpot(int spotNumber, int floorNumber, VehicleType vehicleType) {
        this.spotNumber = spotNumber;
        this.floorNumber = floorNumber;
        this.vehicleType = vehicleType;
        this.isOccupied = false;
        this.parkedVehicle = null;
        this.hourlyRate = vehicleType.getHourlyRate();
    }
    
    public boolean isAvailable() {
        return !isOccupied;
    }
    
    public boolean canParkVehicle(Vehicle vehicle) {
        return isAvailable() && vehicle.getType() == this.vehicleType;
    }
    
    public boolean parkVehicle(Vehicle vehicle) {
        if (canParkVehicle(vehicle)) {
            this.isOccupied = true;
            this.parkedVehicle = vehicle;
            return true;
        }
        return false;
    }
    
    public void removeVehicle() {
        this.isOccupied = false;
        this.parkedVehicle = null;
    }
    
    // Getters
    public int getSpotNumber() {
        return spotNumber;
    }
    
    public int getFloorNumber() {
        return floorNumber;
    }
    
    public VehicleType getVehicleType() {
        return vehicleType;
    }
    
    public boolean isOccupied() {
        return isOccupied;
    }
    
    public Vehicle getParkedVehicle() {
        return parkedVehicle;
    }
    
    public double getHourlyRate() {
        return hourlyRate;
    }
    
    @Override
    public String toString() {
        return "Spot{" +
                "floor=" + floorNumber +
                ", spot=" + spotNumber +
                ", type=" + vehicleType +
                ", occupied=" + isOccupied +
                '}';
    }
} 