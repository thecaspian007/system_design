package com.systemdesign.lld.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Library Management System Application
 * 
 * This is a comprehensive library management system that demonstrates:
 * - Book management (CRUD operations)
 * - Member management
 * - Book borrowing and returning
 * - Transaction tracking
 * - Fine calculation
 * 
 * Features:
 * - Add/Remove books from library
 * - Register/Manage library members
 * - Issue books to members
 * - Return books and calculate fines
 * - Search books by title, author, or ISBN
 * - Track book availability
 * 
 * @author System Design Repository
 */
@SpringBootApplication
public class LibraryManagementSystemApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(LibraryManagementSystemApplication.class, args);
        System.out.println("Library Management System is running on http://localhost:8080");
        System.out.println("H2 Database Console: http://localhost:8080/h2-console");
    }
} 