package com.systemdesign.lld.library.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Book Entity representing a book in the library
 */
@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String author;
    
    @Column(unique = true, nullable = false)
    private String isbn;
    
    @Column(nullable = false)
    private String genre;
    
    @Column(name = "publication_date")
    private LocalDate publicationDate;
    
    @Column(nullable = false)
    private Integer totalCopies;
    
    @Column(nullable = false)
    private Integer availableCopies;
    
    @Column(nullable = false)
    private Double price;
    
    @Column(length = 1000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookStatus status;
    
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookTransaction> transactions;
    
    public enum BookStatus {
        AVAILABLE,
        CHECKED_OUT,
        RESERVED,
        DAMAGED,
        LOST
    }
    
    // Business logic methods
    public boolean isAvailable() {
        return availableCopies > 0 && status == BookStatus.AVAILABLE;
    }
    
    public void borrowBook() {
        if (isAvailable()) {
            availableCopies--;
            if (availableCopies == 0) {
                status = BookStatus.CHECKED_OUT;
            }
        } else {
            throw new IllegalStateException("Book is not available for borrowing");
        }
    }
    
    public void returnBook() {
        if (availableCopies < totalCopies) {
            availableCopies++;
            if (status == BookStatus.CHECKED_OUT) {
                status = BookStatus.AVAILABLE;
            }
        } else {
            throw new IllegalStateException("All copies are already returned");
        }
    }
    
    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", genre='" + genre + '\'' +
                ", availableCopies=" + availableCopies +
                ", totalCopies=" + totalCopies +
                ", status=" + status +
                '}';
    }
} 