package com.systemdesign.designpatterns.creational.factory;

/**
 * Factory Pattern Implementation
 * 
 * The Factory pattern creates objects without specifying the exact class to create.
 * It provides a common interface for creating objects in a superclass,
 * but allows subclasses to alter the type of objects that will be created.
 */

// Product interface
interface Vehicle {
    void start();
    void stop();
    String getType();
}

// Concrete Products
class Car implements Vehicle {
    @Override
    public void start() {
        System.out.println("Car engine started");
    }
    
    @Override
    public void stop() {
        System.out.println("Car engine stopped");
    }
    
    @Override
    public String getType() {
        return "Car";
    }
}

class Motorcycle implements Vehicle {
    @Override
    public void start() {
        System.out.println("Motorcycle engine started");
    }
    
    @Override
    public void stop() {
        System.out.println("Motorcycle engine stopped");
    }
    
    @Override
    public String getType() {
        return "Motorcycle";
    }
}

class Truck implements Vehicle {
    @Override
    public void start() {
        System.out.println("Truck engine started");
    }
    
    @Override
    public void stop() {
        System.out.println("Truck engine stopped");
    }
    
    @Override
    public String getType() {
        return "Truck";
    }
}

// Factory class
class VehicleFactory {
    public static Vehicle createVehicle(String type) {
        switch (type.toLowerCase()) {
            case "car":
                return new Car();
            case "motorcycle":
                return new Motorcycle();
            case "truck":
                return new Truck();
            default:
                throw new IllegalArgumentException("Unknown vehicle type: " + type);
        }
    }
}

// Alternative Factory using enum
enum VehicleType {
    CAR, MOTORCYCLE, TRUCK
}

class EnumVehicleFactory {
    public static Vehicle createVehicle(VehicleType type) {
        switch (type) {
            case CAR:
                return new Car();
            case MOTORCYCLE:
                return new Motorcycle();
            case TRUCK:
                return new Truck();
            default:
                throw new IllegalArgumentException("Unknown vehicle type: " + type);
        }
    }
}

public class FactoryPattern {
    public static void main(String[] args) {
        System.out.println("=== Factory Pattern Demo ===");
        
        // Using string-based factory
        Vehicle car = VehicleFactory.createVehicle("car");
        Vehicle motorcycle = VehicleFactory.createVehicle("motorcycle");
        Vehicle truck = VehicleFactory.createVehicle("truck");
        
        // Test vehicles
        testVehicle(car);
        testVehicle(motorcycle);
        testVehicle(truck);
        
        System.out.println("\n=== Enum Factory Demo ===");
        
        // Using enum-based factory
        Vehicle enumCar = EnumVehicleFactory.createVehicle(VehicleType.CAR);
        Vehicle enumMotorcycle = EnumVehicleFactory.createVehicle(VehicleType.MOTORCYCLE);
        Vehicle enumTruck = EnumVehicleFactory.createVehicle(VehicleType.TRUCK);
        
        testVehicle(enumCar);
        testVehicle(enumMotorcycle);
        testVehicle(enumTruck);
    }
    
    private static void testVehicle(Vehicle vehicle) {
        System.out.println("Testing " + vehicle.getType() + ":");
        vehicle.start();
        vehicle.stop();
        System.out.println();
    }
} 