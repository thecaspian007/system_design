package com.systemdesign.lld.parking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main parking lot class
 */
public class ParkingLot {
    private String name;
    private List<ParkingFloor> floors;
    private Map<String, ParkingTicket> activeTickets; // ticketId -> ticket
    private Map<String, ParkingTicket> ticketsByLicense; // licensePlate -> ticket
    
    public ParkingLot(String name, int maxFloors) {
        this.name = name;
        this.floors = new ArrayList<>();
        this.activeTickets = new HashMap<>();
        this.ticketsByLicense = new HashMap<>();
    }
    
    public void addFloor(ParkingFloor floor) {
        floors.add(floor);
    }
    
    public ParkingTicket parkVehicle(Vehicle vehicle) {
        // Check if vehicle is already parked
        if (ticketsByLicense.containsKey(vehicle.getLicensePlate())) {
            return null; // Vehicle already parked
        }
        
        // Find available spot
        for (ParkingFloor floor : floors) {
            ParkingSpot spot = floor.findAvailableSpot(vehicle.getType());
            if (spot != null) {
                if (spot.parkVehicle(vehicle)) {
                    ParkingTicket ticket = new ParkingTicket(vehicle, spot);
                    activeTickets.put(ticket.getTicketId(), ticket);
                    ticketsByLicense.put(vehicle.getLicensePlate(), ticket);
                    return ticket;
                }
            }
        }
        
        return null; // No available spot
    }
    
    public boolean processPayment(ParkingTicket ticket, double amount) {
        if (ticket != null && !ticket.isPaid()) {
            ticket.markAsPaid(amount);
            
            // Remove vehicle from spot
            ticket.getParkingSpot().removeVehicle();
            
            // Remove from active tracking
            activeTickets.remove(ticket.getTicketId());
            ticketsByLicense.remove(ticket.getVehicle().getLicensePlate());
            
            return true;
        }
        return false;
    }
    
    public double calculateParkingFee(ParkingTicket ticket) {
        if (ticket == null) {
            return 0.0;
        }
        
        long hours = ticket.getParkingDurationInHours();
        double hourlyRate = ticket.getParkingSpot().getHourlyRate();
        
        // First hour free
        if (hours <= 1) {
            return 0.0;
        }
        
        return (hours - 1) * hourlyRate;
    }
    
    public ParkingTicket findTicket(String ticketIdOrLicense) {
        // Try to find by ticket ID first
        ParkingTicket ticket = activeTickets.get(ticketIdOrLicense);
        if (ticket != null) {
            return ticket;
        }
        
        // Try to find by license plate
        return ticketsByLicense.get(ticketIdOrLicense);
    }
    
    public ParkingTicket findTicketByLicensePlate(String licensePlate) {
        return ticketsByLicense.get(licensePlate);
    }
    
    public int getTotalSpots() {
        return floors.stream().mapToInt(ParkingFloor::getTotalSpots).sum();
    }
    
    public int getAvailableSpots() {
        return floors.stream().mapToInt(ParkingFloor::getAvailableSpots).sum();
    }
    
    public int getOccupiedSpots() {
        return getTotalSpots() - getAvailableSpots();
    }
    
    public String getName() {
        return name;
    }
    
    public List<ParkingFloor> getFloors() {
        return new ArrayList<>(floors);
    }
    
    public List<ParkingTicket> getActiveTickets() {
        return new ArrayList<>(activeTickets.values());
    }
} 