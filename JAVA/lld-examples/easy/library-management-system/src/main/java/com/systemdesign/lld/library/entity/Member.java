package com.systemdesign.lld.library.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Member Entity representing a library member
 */
@Entity
@Table(name = "members")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String phone;
    
    @Column(nullable = false)
    private String address;
    
    @Column(name = "membership_number", unique = true, nullable = false)
    private String membershipNumber;
    
    @Column(name = "membership_date", nullable = false)
    private LocalDate membershipDate;
    
    @Column(name = "membership_expiry")
    private LocalDate membershipExpiry;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipType membershipType;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status;
    
    @Column(name = "books_borrowed", nullable = false)
    private Integer booksBorrowed = 0;
    
    @Column(name = "max_books_allowed", nullable = false)
    private Integer maxBooksAllowed;
    
    @Column(name = "total_fine", nullable = false)
    private Double totalFine = 0.0;
    
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookTransaction> transactions;
    
    public enum MembershipType {
        STUDENT(5),
        FACULTY(10),
        STAFF(7),
        GENERAL(3);
        
        private final int maxBooksAllowed;
        
        MembershipType(int maxBooksAllowed) {
            this.maxBooksAllowed = maxBooksAllowed;
        }
        
        public int getMaxBooksAllowed() {
            return maxBooksAllowed;
        }
    }
    
    public enum MemberStatus {
        ACTIVE,
        SUSPENDED,
        EXPIRED,
        BLOCKED
    }
    
    // Business logic methods
    public boolean canBorrowBook() {
        return status == MemberStatus.ACTIVE && 
               booksBorrowed < maxBooksAllowed &&
               membershipExpiry.isAfter(LocalDate.now()) &&
               totalFine <= 100.0; // Max fine limit
    }
    
    public void borrowBook() {
        if (canBorrowBook()) {
            booksBorrowed++;
        } else {
            throw new IllegalStateException("Member cannot borrow more books");
        }
    }
    
    public void returnBook() {
        if (booksBorrowed > 0) {
            booksBorrowed--;
        } else {
            throw new IllegalStateException("No books to return");
        }
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public boolean isMembershipExpired() {
        return membershipExpiry.isBefore(LocalDate.now());
    }
    
    public void addFine(Double amount) {
        totalFine += amount;
        if (totalFine > 100.0) {
            status = MemberStatus.SUSPENDED;
        }
    }
    
    public void payFine(Double amount) {
        totalFine = Math.max(0.0, totalFine - amount);
        if (totalFine <= 100.0 && status == MemberStatus.SUSPENDED) {
            status = MemberStatus.ACTIVE;
        }
    }
    
    @PrePersist
    public void prePersist() {
        if (membershipDate == null) {
            membershipDate = LocalDate.now();
        }
        if (membershipExpiry == null) {
            membershipExpiry = membershipDate.plusYears(1);
        }
        if (maxBooksAllowed == null) {
            maxBooksAllowed = membershipType.getMaxBooksAllowed();
        }
        if (status == null) {
            status = MemberStatus.ACTIVE;
        }
    }
    
    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", membershipNumber='" + membershipNumber + '\'' +
                ", membershipType=" + membershipType +
                ", status=" + status +
                ", booksBorrowed=" + booksBorrowed +
                ", maxBooksAllowed=" + maxBooksAllowed +
                ", totalFine=" + totalFine +
                '}';
    }
} 