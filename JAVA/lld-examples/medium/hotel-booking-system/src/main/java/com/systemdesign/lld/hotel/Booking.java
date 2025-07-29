package com.systemdesign.lld.hotel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Booking class representing a hotel booking/reservation
 */
public class Booking {
    private String bookingId;
    private String guestId;
    private String roomNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int numberOfGuests;
    private BookingStatus status;
    private LocalDateTime bookingDate;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private double totalAmount;
    private double roomCharges;
    private double serviceCharges;
    private double taxes;
    private double discountAmount;
    private String discountReason;
    private List<String> serviceIds;
    private String specialRequests;
    private String cancellationReason;
    private LocalDateTime cancellationDate;
    private boolean isPaid;
    
    public Booking(String bookingId, String guestId, String roomNumber, 
                   LocalDate checkInDate, LocalDate checkOutDate, int numberOfGuests) {
        this.bookingId = bookingId;
        this.guestId = guestId;
        this.roomNumber = roomNumber;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfGuests = numberOfGuests;
        this.status = BookingStatus.CONFIRMED;
        this.bookingDate = LocalDateTime.now();
        this.serviceIds = new ArrayList<>();
        this.serviceCharges = 0.0;
        this.discountAmount = 0.0;
        this.isPaid = false;
        
        calculateTotalAmount();
    }
    
    public void addService(String serviceId, double serviceCost) {
        if (!serviceIds.contains(serviceId)) {
            serviceIds.add(serviceId);
            serviceCharges += serviceCost;
            calculateTotalAmount();
        }
    }
    
    public void removeService(String serviceId, double serviceCost) {
        if (serviceIds.remove(serviceId)) {
            serviceCharges -= serviceCost;
            calculateTotalAmount();
        }
    }
    
    public void applyDiscount(double discountAmount, String reason) {
        this.discountAmount = discountAmount;
        this.discountReason = reason;
        calculateTotalAmount();
    }
    
    public void removeDiscount() {
        this.discountAmount = 0.0;
        this.discountReason = null;
        calculateTotalAmount();
    }
    
    public void calculateTotalAmount() {
        // Calculate room charges based on number of nights
        int nights = (int) java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        
        // This would typically get the room price from the room object
        // For now, using a simple calculation
        this.roomCharges = nights * getRoomPricePerNight();
        
        // Calculate taxes (typically 10-15% of room charges)
        this.taxes = (roomCharges + serviceCharges) * 0.12; // 12% tax
        
        // Total = room charges + service charges + taxes - discount
        this.totalAmount = roomCharges + serviceCharges + taxes - discountAmount;
        
        // Ensure total amount is not negative
        if (this.totalAmount < 0) {
            this.totalAmount = 0;
        }
    }
    
    private double getRoomPricePerNight() {
        // This is a simplified approach. In a real system, 
        // you would get the actual room price from the Room object
        char roomFloor = roomNumber.charAt(0);
        switch (roomFloor) {
            case '1': return 99.99;  // Standard rooms
            case '2': return 149.99; // Deluxe rooms
            case '3': return 299.99; // Suite rooms
            case '4': return 599.99; // Presidential suite
            default: return 99.99;
        }
    }
    
    public void checkIn() {
        if (status != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Booking must be confirmed to check in");
        }
        
        this.status = BookingStatus.CHECKED_IN;
        this.checkInTime = LocalDateTime.now();
    }
    
    public void checkOut() {
        if (status != BookingStatus.CHECKED_IN) {
            throw new IllegalStateException("Guest must be checked in to check out");
        }
        
        this.status = BookingStatus.CHECKED_OUT;
        this.checkOutTime = LocalDateTime.now();
    }
    
    public void complete() {
        if (status != BookingStatus.CHECKED_OUT) {
            throw new IllegalStateException("Guest must be checked out to complete booking");
        }
        
        this.status = BookingStatus.COMPLETED;
    }
    
    public void cancel(String reason) {
        if (status == BookingStatus.COMPLETED || status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel a completed or already cancelled booking");
        }
        
        this.status = BookingStatus.CANCELLED;
        this.cancellationReason = reason;
        this.cancellationDate = LocalDateTime.now();
    }
    
    public void updateGuestCount(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }
    
    public void updateDates(LocalDate checkInDate, LocalDate checkOutDate) {
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        calculateTotalAmount();
    }
    
    public void updateRoom(String roomNumber) {
        this.roomNumber = roomNumber;
        calculateTotalAmount(); // Recalculate since room price may be different
    }
    
    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }
    
    public void markAsPaid() {
        this.isPaid = true;
    }
    
    public void markAsUnpaid() {
        this.isPaid = false;
    }
    
    public int getNumberOfNights() {
        return (int) java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }
    
    public boolean isCurrentlyActive() {
        LocalDate today = LocalDate.now();
        return status == BookingStatus.CHECKED_IN && 
               (today.isEqual(checkInDate) || today.isAfter(checkInDate)) &&
               today.isBefore(checkOutDate);
    }
    
    public boolean isUpcoming() {
        return status == BookingStatus.CONFIRMED && checkInDate.isAfter(LocalDate.now());
    }
    
    public boolean isPast() {
        return (status == BookingStatus.COMPLETED || status == BookingStatus.CHECKED_OUT) &&
               checkOutDate.isBefore(LocalDate.now());
    }
    
    public boolean canBeCancelled() {
        return status == BookingStatus.CONFIRMED && checkInDate.isAfter(LocalDate.now());
    }
    
    public boolean canBeModified() {
        return status == BookingStatus.CONFIRMED && checkInDate.isAfter(LocalDate.now().plusDays(1));
    }
    
    public double getRefundAmount() {
        if (status != BookingStatus.CANCELLED) {
            return 0.0;
        }
        
        long daysUntilCheckIn = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), checkInDate);
        
        if (daysUntilCheckIn >= 7) {
            return totalAmount; // Full refund
        } else if (daysUntilCheckIn >= 3) {
            return totalAmount * 0.8; // 80% refund
        } else if (daysUntilCheckIn >= 1) {
            return totalAmount * 0.5; // 50% refund
        } else {
            return 0.0; // No refund
        }
    }
    
    public Map<String, Object> getBookingSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("bookingId", bookingId);
        summary.put("guestId", guestId);
        summary.put("roomNumber", roomNumber);
        summary.put("checkInDate", checkInDate);
        summary.put("checkOutDate", checkOutDate);
        summary.put("numberOfGuests", numberOfGuests);
        summary.put("numberOfNights", getNumberOfNights());
        summary.put("status", status);
        summary.put("roomCharges", roomCharges);
        summary.put("serviceCharges", serviceCharges);
        summary.put("taxes", taxes);
        summary.put("discountAmount", discountAmount);
        summary.put("totalAmount", totalAmount);
        summary.put("isPaid", isPaid);
        summary.put("servicesCount", serviceIds.size());
        
        return summary;
    }
    
    // Getters
    public String getBookingId() { return bookingId; }
    public String getGuestId() { return guestId; }
    public String getRoomNumber() { return roomNumber; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public int getNumberOfGuests() { return numberOfGuests; }
    public BookingStatus getStatus() { return status; }
    public LocalDateTime getBookingDate() { return bookingDate; }
    public LocalDateTime getCheckInTime() { return checkInTime; }
    public LocalDateTime getCheckOutTime() { return checkOutTime; }
    public double getTotalAmount() { return totalAmount; }
    public double getRoomCharges() { return roomCharges; }
    public double getServiceCharges() { return serviceCharges; }
    public double getTaxes() { return taxes; }
    public double getDiscountAmount() { return discountAmount; }
    public String getDiscountReason() { return discountReason; }
    public List<String> getServiceIds() { return new ArrayList<>(serviceIds); }
    public String getSpecialRequests() { return specialRequests; }
    public String getCancellationReason() { return cancellationReason; }
    public LocalDateTime getCancellationDate() { return cancellationDate; }
    public boolean isPaid() { return isPaid; }
    
    // Setters for status changes
    public void setStatus(BookingStatus status) { this.status = status; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(bookingId, booking.bookingId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(bookingId);
    }
    
    @Override
    public String toString() {
        return "Booking{" +
                "bookingId='" + bookingId + '\'' +
                ", guestId='" + guestId + '\'' +
                ", roomNumber='" + roomNumber + '\'' +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                ", numberOfGuests=" + numberOfGuests +
                ", status=" + status +
                ", totalAmount=" + totalAmount +
                ", isPaid=" + isPaid +
                '}';
    }
}

enum BookingStatus {
    CONFIRMED,
    CHECKED_IN,
    CHECKED_OUT,
    COMPLETED,
    CANCELLED
} 