package com.systemdesign.lld.hotel;

import java.time.LocalDate;
import java.util.*;

/**
 * Room class representing a hotel room
 */
public class Room {
    private String roomNumber;
    private RoomType roomType;
    private double pricePerNight;
    private RoomStatus status;
    private String floor;
    private int maxOccupancy;
    private List<String> amenities;
    private Map<LocalDate, String> bookings; // date -> bookingId
    private String currentBookingId;
    private String currentGuestId;
    private boolean isClean;
    private String maintenanceNotes;
    private LocalDate lastCleaned;
    
    public Room(String roomNumber, RoomType roomType, double pricePerNight) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.status = RoomStatus.AVAILABLE;
        this.floor = String.valueOf(roomNumber.charAt(0));
        this.maxOccupancy = getMaxOccupancyByType(roomType);
        this.amenities = getAmenitiesByType(roomType);
        this.bookings = new HashMap<>();
        this.isClean = true;
        this.lastCleaned = LocalDate.now();
    }
    
    public Room(String roomNumber, RoomType roomType, double pricePerNight, String floor, int maxOccupancy) {
        this(roomNumber, roomType, pricePerNight);
        this.floor = floor;
        this.maxOccupancy = maxOccupancy;
    }
    
    private int getMaxOccupancyByType(RoomType roomType) {
        switch (roomType) {
            case STANDARD:
                return 2;
            case DELUXE:
                return 3;
            case SUITE:
                return 4;
            case PRESIDENTIAL:
                return 6;
            default:
                return 2;
        }
    }
    
    private List<String> getAmenitiesByType(RoomType roomType) {
        List<String> amenities = new ArrayList<>();
        
        // Common amenities
        amenities.addAll(Arrays.asList("WiFi", "TV", "Air Conditioning", "Private Bathroom"));
        
        switch (roomType) {
            case STANDARD:
                amenities.add("Coffee Maker");
                break;
            case DELUXE:
                amenities.addAll(Arrays.asList("Coffee Maker", "Mini Bar", "Balcony"));
                break;
            case SUITE:
                amenities.addAll(Arrays.asList("Coffee Maker", "Mini Bar", "Balcony", "Living Room", "Jacuzzi"));
                break;
            case PRESIDENTIAL:
                amenities.addAll(Arrays.asList("Coffee Maker", "Mini Bar", "Balcony", "Living Room", "Jacuzzi", 
                                              "Dining Room", "Kitchen", "Butler Service"));
                break;
        }
        
        return amenities;
    }
    
    public boolean isAvailable(LocalDate date) {
        return status == RoomStatus.AVAILABLE && !bookings.containsKey(date);
    }
    
    public boolean isAvailableForRange(LocalDate checkIn, LocalDate checkOut) {
        if (status != RoomStatus.AVAILABLE) {
            return false;
        }
        
        LocalDate current = checkIn;
        while (!current.isAfter(checkOut)) {
            if (bookings.containsKey(current)) {
                return false;
            }
            current = current.plusDays(1);
        }
        return true;
    }
    
    public void bookForRange(LocalDate checkIn, LocalDate checkOut, String bookingId) {
        if (!isAvailableForRange(checkIn, checkOut)) {
            throw new IllegalStateException("Room not available for the requested dates");
        }
        
        LocalDate current = checkIn;
        while (!current.isAfter(checkOut)) {
            bookings.put(current, bookingId);
            current = current.plusDays(1);
        }
    }
    
    public void cancelBookingForRange(LocalDate checkIn, LocalDate checkOut) {
        LocalDate current = checkIn;
        while (!current.isAfter(checkOut)) {
            bookings.remove(current);
            current = current.plusDays(1);
        }
    }
    
    public void checkIn(String bookingId, String guestId) {
        if (status != RoomStatus.AVAILABLE) {
            throw new IllegalStateException("Room is not available for check-in");
        }
        
        this.status = RoomStatus.OCCUPIED;
        this.currentBookingId = bookingId;
        this.currentGuestId = guestId;
    }
    
    public void checkOut() {
        if (status != RoomStatus.OCCUPIED) {
            throw new IllegalStateException("Room is not currently occupied");
        }
        
        this.status = RoomStatus.AVAILABLE;
        this.currentBookingId = null;
        this.currentGuestId = null;
        this.isClean = false; // Room needs cleaning after checkout
    }
    
    public void setMaintenance(String notes) {
        this.status = RoomStatus.MAINTENANCE;
        this.maintenanceNotes = notes;
    }
    
    public void setOutOfOrder(String reason) {
        this.status = RoomStatus.OUT_OF_ORDER;
        this.maintenanceNotes = reason;
    }
    
    public void markAsClean() {
        this.isClean = true;
        this.lastCleaned = LocalDate.now();
        
        // If room was in maintenance and now clean, make it available
        if (status == RoomStatus.MAINTENANCE && isClean) {
            this.status = RoomStatus.AVAILABLE;
            this.maintenanceNotes = null;
        }
    }
    
    public void addAmenity(String amenity) {
        if (!amenities.contains(amenity)) {
            amenities.add(amenity);
        }
    }
    
    public void removeAmenity(String amenity) {
        amenities.remove(amenity);
    }
    
    public double calculateRate(int nights) {
        return pricePerNight * nights;
    }
    
    public double calculateRateWithDiscount(int nights, double discountPercentage) {
        double totalRate = calculateRate(nights);
        return totalRate * (1 - discountPercentage / 100);
    }
    
    public List<LocalDate> getBookedDates() {
        return new ArrayList<>(bookings.keySet());
    }
    
    public String getBookingIdForDate(LocalDate date) {
        return bookings.get(date);
    }
    
    public boolean hasAmenity(String amenity) {
        return amenities.contains(amenity);
    }
    
    public boolean requiresCleaning() {
        return !isClean;
    }
    
    public int getDaysSinceLastCleaned() {
        return (int) java.time.temporal.ChronoUnit.DAYS.between(lastCleaned, LocalDate.now());
    }
    
    // Getters
    public String getRoomNumber() { return roomNumber; }
    public RoomType getRoomType() { return roomType; }
    public double getPricePerNight() { return pricePerNight; }
    public RoomStatus getStatus() { return status; }
    public String getFloor() { return floor; }
    public int getMaxOccupancy() { return maxOccupancy; }
    public List<String> getAmenities() { return new ArrayList<>(amenities); }
    public Map<LocalDate, String> getBookings() { return new HashMap<>(bookings); }
    public String getCurrentBookingId() { return currentBookingId; }
    public String getCurrentGuestId() { return currentGuestId; }
    public boolean isClean() { return isClean; }
    public String getMaintenanceNotes() { return maintenanceNotes; }
    public LocalDate getLastCleaned() { return lastCleaned; }
    
    // Setters
    public void setPricePerNight(double pricePerNight) { this.pricePerNight = pricePerNight; }
    public void setStatus(RoomStatus status) { this.status = status; }
    public void setFloor(String floor) { this.floor = floor; }
    public void setMaxOccupancy(int maxOccupancy) { this.maxOccupancy = maxOccupancy; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return Objects.equals(roomNumber, room.roomNumber);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(roomNumber);
    }
    
    @Override
    public String toString() {
        return "Room{" +
                "roomNumber='" + roomNumber + '\'' +
                ", roomType=" + roomType +
                ", pricePerNight=" + pricePerNight +
                ", status=" + status +
                ", floor='" + floor + '\'' +
                ", maxOccupancy=" + maxOccupancy +
                ", isClean=" + isClean +
                '}';
    }
}

enum RoomType {
    STANDARD,
    DELUXE,
    SUITE,
    PRESIDENTIAL
}

enum RoomStatus {
    AVAILABLE,
    OCCUPIED,
    MAINTENANCE,
    OUT_OF_ORDER
} 