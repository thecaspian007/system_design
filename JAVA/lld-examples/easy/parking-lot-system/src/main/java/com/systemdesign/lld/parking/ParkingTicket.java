package com.systemdesign.lld.parking;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Parking ticket class
 */
public class ParkingTicket {
    private static int ticketCounter = 1;
    
    private String ticketId;
    private Vehicle vehicle;
    private ParkingSpot parkingSpot;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private double fee;
    private boolean isPaid;
    
    public ParkingTicket(Vehicle vehicle, ParkingSpot parkingSpot) {
        this.ticketId = "TICKET" + String.format("%06d", ticketCounter++);
        this.vehicle = vehicle;
        this.parkingSpot = parkingSpot;
        this.entryTime = LocalDateTime.now();
        this.exitTime = null;
        this.fee = 0.0;
        this.isPaid = false;
    }
    
    public long getParkingDurationInMinutes() {
        LocalDateTime endTime = exitTime != null ? exitTime : LocalDateTime.now();
        return ChronoUnit.MINUTES.between(entryTime, endTime);
    }
    
    public long getParkingDurationInHours() {
        long minutes = getParkingDurationInMinutes();
        return (minutes + 59) / 60; // Round up to next hour
    }
    
    public void markAsPaid(double fee) {
        this.fee = fee;
        this.isPaid = true;
        this.exitTime = LocalDateTime.now();
    }
    
    public String getFormattedEntryTime() {
        return entryTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    public String getFormattedExitTime() {
        if (exitTime != null) {
            return exitTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        return "Not exited";
    }
    
    // Getters
    public String getTicketId() {
        return ticketId;
    }
    
    public Vehicle getVehicle() {
        return vehicle;
    }
    
    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }
    
    public LocalDateTime getEntryTime() {
        return entryTime;
    }
    
    public LocalDateTime getExitTime() {
        return exitTime;
    }
    
    public double getFee() {
        return fee;
    }
    
    public boolean isPaid() {
        return isPaid;
    }
    
    @Override
    public String toString() {
        return "ParkingTicket{" +
                "ticketId='" + ticketId + '\'' +
                ", vehicle=" + vehicle +
                ", parkingSpot=" + parkingSpot +
                ", entryTime=" + getFormattedEntryTime() +
                ", duration=" + getParkingDurationInHours() + "h" +
                ", fee=" + fee +
                ", isPaid=" + isPaid +
                '}';
    }
} 