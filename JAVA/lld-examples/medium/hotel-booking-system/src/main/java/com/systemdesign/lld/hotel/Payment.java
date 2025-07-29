package com.systemdesign.lld.hotel;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Payment class representing a payment transaction
 */
public class Payment {
    private String paymentId;
    private String bookingId;
    private String guestId;
    private double amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private LocalDateTime paymentDate;
    private LocalDateTime processedDate;
    private String transactionId;
    private String cardLastFourDigits;
    private String notes;
    private String failureReason;
    
    public Payment(String paymentId, String bookingId, String guestId, double amount, PaymentMethod paymentMethod) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.guestId = guestId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = PaymentStatus.PENDING;
        this.paymentDate = LocalDateTime.now();
    }
    
    public void processPayment(String transactionId) {
        if (status != PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment is not in pending status");
        }
        
        this.status = PaymentStatus.COMPLETED;
        this.transactionId = transactionId;
        this.processedDate = LocalDateTime.now();
    }
    
    public void failPayment(String reason) {
        if (status != PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment is not in pending status");
        }
        
        this.status = PaymentStatus.FAILED;
        this.failureReason = reason;
        this.processedDate = LocalDateTime.now();
    }
    
    public void refundPayment() {
        if (status != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Can only refund completed payments");
        }
        
        this.status = PaymentStatus.REFUNDED;
    }
    
    public boolean isSuccessful() {
        return status == PaymentStatus.COMPLETED;
    }
    
    public boolean canBeRefunded() {
        return status == PaymentStatus.COMPLETED;
    }
    
    // Getters
    public String getPaymentId() { return paymentId; }
    public String getBookingId() { return bookingId; }
    public String getGuestId() { return guestId; }
    public double getAmount() { return amount; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public PaymentStatus getStatus() { return status; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public LocalDateTime getProcessedDate() { return processedDate; }
    public String getTransactionId() { return transactionId; }
    public String getCardLastFourDigits() { return cardLastFourDigits; }
    public String getNotes() { return notes; }
    public String getFailureReason() { return failureReason; }
    
    // Setters
    public void setCardLastFourDigits(String cardLastFourDigits) { this.cardLastFourDigits = cardLastFourDigits; }
    public void setNotes(String notes) { this.notes = notes; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(paymentId, payment.paymentId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(paymentId);
    }
    
    @Override
    public String toString() {
        return "Payment{" +
                "paymentId='" + paymentId + '\'' +
                ", bookingId='" + bookingId + '\'' +
                ", amount=" + amount +
                ", paymentMethod=" + paymentMethod +
                ", status=" + status +
                ", paymentDate=" + paymentDate +
                '}';
    }
}

/**
 * HotelService class representing additional hotel services
 */
class HotelService {
    private String serviceId;
    private String serviceName;
    private String description;
    private double price;
    private ServiceType serviceType;
    private boolean isAvailable;
    private int durationMinutes;
    private String location;
    private int maxCapacity;
    private boolean requiresAdvanceBooking;
    
    public HotelService(String serviceId, String serviceName, double price, ServiceType serviceType) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.price = price;
        this.serviceType = serviceType;
        this.isAvailable = true;
        this.requiresAdvanceBooking = false;
        this.maxCapacity = Integer.MAX_VALUE;
    }
    
    public HotelService(String serviceId, String serviceName, String description, double price, 
                       ServiceType serviceType, int durationMinutes, String location) {
        this(serviceId, serviceName, price, serviceType);
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.location = location;
    }
    
    public boolean canBeBooked() {
        return isAvailable && !requiresAdvanceBooking;
    }
    
    public boolean canBeBookedInAdvance() {
        return isAvailable && requiresAdvanceBooking;
    }
    
    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }
    
    public void setRequiresAdvanceBooking(boolean requiresAdvanceBooking) {
        this.requiresAdvanceBooking = requiresAdvanceBooking;
    }
    
    public double calculateCost(int quantity) {
        return price * quantity;
    }
    
    public double calculateCostWithDiscount(int quantity, double discountPercentage) {
        double totalCost = calculateCost(quantity);
        return totalCost * (1 - discountPercentage / 100);
    }
    
    // Getters
    public String getServiceId() { return serviceId; }
    public String getServiceName() { return serviceName; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public ServiceType getServiceType() { return serviceType; }
    public boolean isAvailable() { return isAvailable; }
    public int getDurationMinutes() { return durationMinutes; }
    public String getLocation() { return location; }
    public int getMaxCapacity() { return maxCapacity; }
    public boolean isRequiresAdvanceBooking() { return requiresAdvanceBooking; }
    
    // Setters
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price) { this.price = price; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public void setLocation(String location) { this.location = location; }
    public void setMaxCapacity(int maxCapacity) { this.maxCapacity = maxCapacity; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HotelService that = (HotelService) o;
        return Objects.equals(serviceId, that.serviceId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(serviceId);
    }
    
    @Override
    public String toString() {
        return "HotelService{" +
                "serviceId='" + serviceId + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", price=" + price +
                ", serviceType=" + serviceType +
                ", isAvailable=" + isAvailable +
                '}';
    }
}

enum PaymentMethod {
    CREDIT_CARD,
    DEBIT_CARD,
    CASH,
    BANK_TRANSFER,
    DIGITAL_WALLET
}

enum PaymentStatus {
    PENDING,
    COMPLETED,
    FAILED,
    REFUNDED
}

enum ServiceType {
    DINING,
    WELLNESS,
    TRANSPORT,
    UTILITY,
    ENTERTAINMENT,
    BUSINESS
} 