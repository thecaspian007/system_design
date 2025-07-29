package com.systemdesign.lld.parking;

import java.util.ArrayList;
import java.util.List;

/**
 * Parking floor class
 */
public class ParkingFloor {
    private int floorNumber;
    private List<ParkingSpot> parkingSpots;
    
    public ParkingFloor(int floorNumber, int carSpots, int motorcycleSpots, int truckSpots) {
        this.floorNumber = floorNumber;
        this.parkingSpots = new ArrayList<>();
        
        initializeParkingSpots(carSpots, motorcycleSpots, truckSpots);
    }
    
    private void initializeParkingSpots(int carSpots, int motorcycleSpots, int truckSpots) {
        int spotNumber = 1;
        
        // Add car spots
        for (int i = 0; i < carSpots; i++) {
            parkingSpots.add(new ParkingSpot(spotNumber++, floorNumber, VehicleType.CAR));
        }
        
        // Add motorcycle spots
        for (int i = 0; i < motorcycleSpots; i++) {
            parkingSpots.add(new ParkingSpot(spotNumber++, floorNumber, VehicleType.MOTORCYCLE));
        }
        
        // Add truck spots
        for (int i = 0; i < truckSpots; i++) {
            parkingSpots.add(new ParkingSpot(spotNumber++, floorNumber, VehicleType.TRUCK));
        }
    }
    
    public ParkingSpot findAvailableSpot(VehicleType vehicleType) {
        for (ParkingSpot spot : parkingSpots) {
            if (spot.getVehicleType() == vehicleType && spot.isAvailable()) {
                return spot;
            }
        }
        return null;
    }
    
    public boolean parkVehicle(Vehicle vehicle) {
        ParkingSpot spot = findAvailableSpot(vehicle.getType());
        if (spot != null) {
            return spot.parkVehicle(vehicle);
        }
        return false;
    }
    
    public boolean removeVehicle(Vehicle vehicle) {
        for (ParkingSpot spot : parkingSpots) {
            if (spot.isOccupied() && spot.getParkedVehicle().getLicensePlate().equals(vehicle.getLicensePlate())) {
                spot.removeVehicle();
                return true;
            }
        }
        return false;
    }
    
    public ParkingSpot findSpotByVehicle(String licensePlate) {
        for (ParkingSpot spot : parkingSpots) {
            if (spot.isOccupied() && spot.getParkedVehicle().getLicensePlate().equals(licensePlate)) {
                return spot;
            }
        }
        return null;
    }
    
    public int getAvailableSpots(VehicleType vehicleType) {
        int count = 0;
        for (ParkingSpot spot : parkingSpots) {
            if (spot.getVehicleType() == vehicleType && spot.isAvailable()) {
                count++;
            }
        }
        return count;
    }
    
    public int getTotalSpots(VehicleType vehicleType) {
        int count = 0;
        for (ParkingSpot spot : parkingSpots) {
            if (spot.getVehicleType() == vehicleType) {
                count++;
            }
        }
        return count;
    }
    
    public int getFloorNumber() {
        return floorNumber;
    }
    
    public List<ParkingSpot> getParkingSpots() {
        return new ArrayList<>(parkingSpots);
    }
    
    public int getTotalSpots() {
        return parkingSpots.size();
    }
    
    public int getAvailableSpots() {
        int count = 0;
        for (ParkingSpot spot : parkingSpots) {
            if (spot.isAvailable()) {
                count++;
            }
        }
        return count;
    }
    
    public int getOccupiedSpots() {
        return getTotalSpots() - getAvailableSpots();
    }
} 