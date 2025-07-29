package com.systemdesign.lld.library.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * BookTransaction Entity representing a book borrowing/returning transaction
 */
@Entity
@Table(name = "book_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookTransaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    
    @Column(name = "borrow_date", nullable = false)
    private LocalDate borrowDate;
    
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;
    
    @Column(name = "return_date")
    private LocalDate returnDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;
    
    @Column(name = "fine_amount")
    private Double fineAmount = 0.0;
    
    @Column(name = "fine_paid")
    private Boolean finePaid = false;
    
    @Column(name = "renewal_count")
    private Integer renewalCount = 0;
    
    @Column(name = "max_renewals")
    private Integer maxRenewals = 2;
    
    @Column(length = 500)
    private String notes;
    
    public enum TransactionStatus {
        ACTIVE,     // Book is currently borrowed
        RETURNED,   // Book has been returned
        OVERDUE,    // Book is overdue
        LOST,       // Book is reported lost
        DAMAGED     // Book is returned damaged
    }
    
    // Business logic methods
    public boolean isOverdue() {
        return status == TransactionStatus.ACTIVE && 
               LocalDate.now().isAfter(dueDate);
    }
    
    public long getDaysOverdue() {
        if (isOverdue()) {
            return ChronoUnit.DAYS.between(dueDate, LocalDate.now());
        }
        return 0;
    }
    
    public double calculateFine() {
        if (isOverdue()) {
            long daysOverdue = getDaysOverdue();
            return daysOverdue * 1.0; // $1 per day fine
        }
        return 0.0;
    }
    
    public boolean canRenew() {
        return status == TransactionStatus.ACTIVE &&
               renewalCount < maxRenewals &&
               !isOverdue() &&
               book.isAvailable();
    }
    
    public void renewBook() {
        if (canRenew()) {
            renewalCount++;
            dueDate = dueDate.plusDays(14); // Extend by 2 weeks
        } else {
            throw new IllegalStateException("Book cannot be renewed");
        }
    }
    
    public void returnBook() {
        if (status == TransactionStatus.ACTIVE) {
            returnDate = LocalDate.now();
            status = TransactionStatus.RETURNED;
            
            // Calculate fine if overdue
            if (isOverdue()) {
                fineAmount = calculateFine();
                if (fineAmount > 0) {
                    member.addFine(fineAmount);
                }
            }
            
            // Update book and member
            book.returnBook();
            member.returnBook();
        } else {
            throw new IllegalStateException("Book is not currently borrowed");
        }
    }
    
    public void markAsLost() {
        if (status == TransactionStatus.ACTIVE) {
            status = TransactionStatus.LOST;
            returnDate = LocalDate.now();
            
            // Apply lost book fine (usually the book price)
            fineAmount = book.getPrice();
            member.addFine(fineAmount);
            
            // Update member's borrowed count
            member.returnBook();
        } else {
            throw new IllegalStateException("Only active transactions can be marked as lost");
        }
    }
    
    public void markAsDamaged(String damageNotes) {
        if (status == TransactionStatus.ACTIVE) {
            status = TransactionStatus.DAMAGED;
            returnDate = LocalDate.now();
            notes = damageNotes;
            
            // Apply damage fine (percentage of book price)
            fineAmount = book.getPrice() * 0.5; // 50% of book price
            member.addFine(fineAmount);
            
            // Update book and member
            book.returnBook();
            member.returnBook();
        } else {
            throw new IllegalStateException("Only active transactions can be marked as damaged");
        }
    }
    
    @PrePersist
    public void prePersist() {
        if (borrowDate == null) {
            borrowDate = LocalDate.now();
        }
        if (dueDate == null) {
            // Default due date is 14 days from borrow date
            dueDate = borrowDate.plusDays(14);
        }
        if (status == null) {
            status = TransactionStatus.ACTIVE;
        }
    }
    
    @PreUpdate
    public void preUpdate() {
        // Update status if overdue
        if (status == TransactionStatus.ACTIVE && isOverdue()) {
            status = TransactionStatus.OVERDUE;
        }
    }
    
    @Override
    public String toString() {
        return "BookTransaction{" +
                "id=" + id +
                ", book=" + (book != null ? book.getTitle() : "null") +
                ", member=" + (member != null ? member.getFullName() : "null") +
                ", borrowDate=" + borrowDate +
                ", dueDate=" + dueDate +
                ", returnDate=" + returnDate +
                ", status=" + status +
                ", fineAmount=" + fineAmount +
                ", renewalCount=" + renewalCount +
                '}';
    }
} 