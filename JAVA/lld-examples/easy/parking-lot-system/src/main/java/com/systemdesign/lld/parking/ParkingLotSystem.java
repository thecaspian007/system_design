package com.systemdesign.lld.parking;

import java.util.Scanner;

/**
 * Parking Lot System - Main Application
 * 
 * This system demonstrates:
 * - Vehicle parking and exit management
 * - Different vehicle types (Car, Motorcycle, Truck)
 * - Pricing strategies
 * - Parking spot allocation
 * - Payment processing
 * 
 * @author System Design Repository
 */
public class ParkingLotSystem {
    
    private ParkingLot parkingLot;
    private Scanner scanner;
    
    public ParkingLotSystem() {
        // Initialize parking lot with 3 floors
        this.parkingLot = new ParkingLot("Downtown Parking", 3);
        this.scanner = new Scanner(System.in);
        
        // Initialize floors with parking spots
        initializeParkingLot();
    }
    
    private void initializeParkingLot() {
        // Floor 1: Mixed spots
        parkingLot.addFloor(new ParkingFloor(1, 20, 10, 5)); // 20 car, 10 motorcycle, 5 truck spots
        
        // Floor 2: More cars and motorcycles
        parkingLot.addFloor(new ParkingFloor(2, 25, 15, 3));
        
        // Floor 3: Mostly cars
        parkingLot.addFloor(new ParkingFloor(3, 30, 8, 2));
        
        System.out.println("Parking lot initialized with 3 floors");
        System.out.println("Total spots: " + parkingLot.getTotalSpots());
    }
    
    public void start() {
        System.out.println("=== Welcome to " + parkingLot.getName() + " ===");
        
        while (true) {
            try {
                showMainMenu();
                int choice = getIntInput();
                
                switch (choice) {
                    case 1:
                        handleVehicleEntry();
                        break;
                    case 2:
                        handleVehicleExit();
                        break;
                    case 3:
                        showParkingStatus();
                        break;
                    case 4:
                        searchVehicle();
                        break;
                    case 5:
                        System.out.println("Thank you for using our parking system!");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    
    private void showMainMenu() {
        System.out.println("\n--- Parking Lot Management ---");
        System.out.println("1. Park Vehicle");
        System.out.println("2. Exit Vehicle");
        System.out.println("3. Show Parking Status");
        System.out.println("4. Search Vehicle");
        System.out.println("5. Exit System");
        System.out.print("Choose an option: ");
    }
    
    private void handleVehicleEntry() {
        System.out.println("\n--- Vehicle Entry ---");
        System.out.print("Enter vehicle license plate: ");
        String licensePlate = scanner.nextLine().toUpperCase();
        
        System.out.println("Select vehicle type:");
        System.out.println("1. Car");
        System.out.println("2. Motorcycle");
        System.out.println("3. Truck");
        System.out.print("Choice: ");
        
        int typeChoice = getIntInput();
        VehicleType vehicleType = getVehicleType(typeChoice);
        
        if (vehicleType == null) {
            System.out.println("Invalid vehicle type selected.");
            return;
        }
        
        Vehicle vehicle = VehicleFactory.createVehicle(vehicleType, licensePlate);
        ParkingTicket ticket = parkingLot.parkVehicle(vehicle);
        
        if (ticket != null) {
            System.out.println("Vehicle parked successfully!");
            System.out.println("Ticket ID: " + ticket.getTicketId());
            System.out.println("Parking Spot: Floor " + ticket.getParkingSpot().getFloorNumber() + 
                             ", Spot " + ticket.getParkingSpot().getSpotNumber());
            System.out.println("Entry Time: " + ticket.getFormattedEntryTime());
            System.out.println("Hourly Rate: $" + ticket.getParkingSpot().getHourlyRate());
        } else {
            System.out.println("Sorry, no available parking spot for " + vehicleType.getDisplayName());
        }
    }
    
    private void handleVehicleExit() {
        System.out.println("\n--- Vehicle Exit ---");
        System.out.print("Enter ticket ID or license plate: ");
        String input = scanner.nextLine().toUpperCase();
        
        ParkingTicket ticket = parkingLot.findTicket(input);
        
        if (ticket == null) {
            System.out.println("Ticket not found.");
            return;
        }
        
        if (ticket.isPaid()) {
            System.out.println("Vehicle already exited.");
            return;
        }
        
        double fee = parkingLot.calculateParkingFee(ticket);
        System.out.println("Vehicle: " + ticket.getVehicle().getLicensePlate());
        System.out.println("Parking Duration: " + ticket.getParkingDurationInHours() + " hours");
        System.out.println("Parking Fee: $" + String.format("%.2f", fee));
        
        if (fee > 0) {
            System.out.print("Confirm payment (y/n): ");
            String confirm = scanner.nextLine();
            
            if (confirm.equalsIgnoreCase("y")) {
                if (parkingLot.processPayment(ticket, fee)) {
                    System.out.println("Payment successful! Vehicle can exit.");
                    System.out.println("Thank you for using our parking service!");
                } else {
                    System.out.println("Payment failed. Please try again.");
                }
            } else {
                System.out.println("Payment cancelled.");
            }
        } else {
            // Free parking (less than 1 hour)
            parkingLot.processPayment(ticket, 0);
            System.out.println("Free parking! Vehicle can exit.");
        }
    }
    
    private void showParkingStatus() {
        System.out.println("\n--- Parking Status ---");
        System.out.println("Parking Lot: " + parkingLot.getName());
        System.out.println("Total Floors: " + parkingLot.getFloors().size());
        System.out.println("Total Spots: " + parkingLot.getTotalSpots());
        System.out.println("Available Spots: " + parkingLot.getAvailableSpots());
        System.out.println("Occupied Spots: " + parkingLot.getOccupiedSpots());
        System.out.println();
        
        for (ParkingFloor floor : parkingLot.getFloors()) {
            System.out.println("Floor " + floor.getFloorNumber() + ":");
            System.out.println("  Cars: " + floor.getAvailableSpots(VehicleType.CAR) + "/" + 
                             floor.getTotalSpots(VehicleType.CAR));
            System.out.println("  Motorcycles: " + floor.getAvailableSpots(VehicleType.MOTORCYCLE) + "/" + 
                             floor.getTotalSpots(VehicleType.MOTORCYCLE));
            System.out.println("  Trucks: " + floor.getAvailableSpots(VehicleType.TRUCK) + "/" + 
                             floor.getTotalSpots(VehicleType.TRUCK));
        }
    }
    
    private void searchVehicle() {
        System.out.println("\n--- Search Vehicle ---");
        System.out.print("Enter license plate: ");
        String licensePlate = scanner.nextLine().toUpperCase();
        
        ParkingTicket ticket = parkingLot.findTicketByLicensePlate(licensePlate);
        
        if (ticket == null) {
            System.out.println("Vehicle not found in the parking lot.");
            return;
        }
        
        System.out.println("Vehicle found!");
        System.out.println("License Plate: " + ticket.getVehicle().getLicensePlate());
        System.out.println("Vehicle Type: " + ticket.getVehicle().getType().getDisplayName());
        System.out.println("Parking Spot: Floor " + ticket.getParkingSpot().getFloorNumber() + 
                         ", Spot " + ticket.getParkingSpot().getSpotNumber());
        System.out.println("Entry Time: " + ticket.getFormattedEntryTime());
        System.out.println("Duration: " + ticket.getParkingDurationInHours() + " hours");
        System.out.println("Current Fee: $" + String.format("%.2f", parkingLot.calculateParkingFee(ticket)));
    }
    
    private VehicleType getVehicleType(int choice) {
        switch (choice) {
            case 1: return VehicleType.CAR;
            case 2: return VehicleType.MOTORCYCLE;
            case 3: return VehicleType.TRUCK;
            default: return null;
        }
    }
    
    private int getIntInput() {
        while (true) {
            try {
                String input = scanner.nextLine();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }
    
    public static void main(String[] args) {
        ParkingLotSystem system = new ParkingLotSystem();
        system.start();
    }
} 