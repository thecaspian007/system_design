package com.systemdesign.lld.hotel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Hotel Booking System - Main Application
 * 
 * This system demonstrates:
 * - Room management and availability
 * - Guest registration and management
 * - Booking and reservation system
 * - Payment processing
 * - Check-in/check-out operations
 * - Hotel services management
 * 
 * @author System Design Repository
 */
public class HotelBookingSystem {
    
    private Map<String, Guest> guests;
    private Map<String, Room> rooms;
    private Map<String, Booking> bookings;
    private Map<String, Payment> payments;
    private Map<String, HotelService> services;
    private Scanner scanner;
    
    public HotelBookingSystem() {
        this.guests = new ConcurrentHashMap<>();
        this.rooms = new ConcurrentHashMap<>();
        this.bookings = new ConcurrentHashMap<>();
        this.payments = new ConcurrentHashMap<>();
        this.services = new ConcurrentHashMap<>();
        this.scanner = new Scanner(System.in);
        
        initializeSystem();
    }
    
    private void initializeSystem() {
        // Initialize rooms
        initializeRooms();
        
        // Initialize services
        initializeServices();
        
        // Initialize sample guests
        guests.put("G001", new Guest("G001", "John Doe", "john@email.com", "+1234567890", "123 Main St"));
        guests.put("G002", new Guest("G002", "Jane Smith", "jane@email.com", "+1234567891", "456 Oak Ave"));
        
        System.out.println("Hotel Booking System initialized!");
        System.out.println("Rooms: " + rooms.size());
        System.out.println("Services: " + services.size());
    }
    
    private void initializeRooms() {
        // Standard rooms
        for (int i = 101; i <= 110; i++) {
            rooms.put(String.valueOf(i), new Room(String.valueOf(i), RoomType.STANDARD, 99.99));
        }
        
        // Deluxe rooms
        for (int i = 201; i <= 210; i++) {
            rooms.put(String.valueOf(i), new Room(String.valueOf(i), RoomType.DELUXE, 149.99));
        }
        
        // Suite rooms
        for (int i = 301; i <= 305; i++) {
            rooms.put(String.valueOf(i), new Room(String.valueOf(i), RoomType.SUITE, 299.99));
        }
        
        // Presidential suite
        rooms.put("401", new Room("401", RoomType.PRESIDENTIAL, 599.99));
    }
    
    private void initializeServices() {
        services.put("SPA", new HotelService("SPA", "Spa Treatment", 150.00, ServiceType.WELLNESS));
        services.put("MASSAGE", new HotelService("MASSAGE", "Massage Therapy", 120.00, ServiceType.WELLNESS));
        services.put("RESTAURANT", new HotelService("RESTAURANT", "Fine Dining", 75.00, ServiceType.DINING));
        services.put("LAUNDRY", new HotelService("LAUNDRY", "Laundry Service", 25.00, ServiceType.UTILITY));
        services.put("TRANSPORT", new HotelService("TRANSPORT", "Airport Transfer", 50.00, ServiceType.TRANSPORT));
        services.put("ROOM_SERVICE", new HotelService("ROOM_SERVICE", "24/7 Room Service", 30.00, ServiceType.DINING));
    }
    
    public void start() {
        System.out.println("\n=== Welcome to Hotel Booking System ===");
        
        while (true) {
            try {
                showMainMenu();
                int choice = getIntInput();
                handleMainChoice(choice);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    
    private void showMainMenu() {
        System.out.println("\n--- Hotel Management System ---");
        System.out.println("1. Guest Management");
        System.out.println("2. Room Management");
        System.out.println("3. Booking Management");
        System.out.println("4. Check-in/Check-out");
        System.out.println("5. Services Management");
        System.out.println("6. Payment Management");
        System.out.println("7. Reports");
        System.out.println("8. Exit");
        System.out.print("Choose an option: ");
    }
    
    private void handleMainChoice(int choice) {
        switch (choice) {
            case 1: guestManagement(); break;
            case 2: roomManagement(); break;
            case 3: bookingManagement(); break;
            case 4: checkInCheckOut(); break;
            case 5: servicesManagement(); break;
            case 6: paymentManagement(); break;
            case 7: generateReports(); break;
            case 8: 
                System.out.println("Thank you for using Hotel Booking System!");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }
    
    private void guestManagement() {
        System.out.println("\n--- Guest Management ---");
        System.out.println("1. Register New Guest");
        System.out.println("2. View Guest Details");
        System.out.println("3. Update Guest Information");
        System.out.println("4. View All Guests");
        System.out.println("5. Back to Main Menu");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1: registerGuest(); break;
            case 2: viewGuestDetails(); break;
            case 3: updateGuestInfo(); break;
            case 4: viewAllGuests(); break;
            case 5: return;
            default: System.out.println("Invalid choice.");
        }
    }
    
    private void registerGuest() {
        System.out.print("Enter guest ID: ");
        String guestId = scanner.nextLine();
        
        if (guests.containsKey(guestId)) {
            System.out.println("Guest ID already exists.");
            return;
        }
        
        System.out.print("Enter guest name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        
        System.out.print("Enter phone: ");
        String phone = scanner.nextLine();
        
        System.out.print("Enter address: ");
        String address = scanner.nextLine();
        
        Guest guest = new Guest(guestId, name, email, phone, address);
        guests.put(guestId, guest);
        
        System.out.println("Guest registered successfully!");
    }
    
    private void viewGuestDetails() {
        System.out.print("Enter guest ID: ");
        String guestId = scanner.nextLine();
        
        Guest guest = guests.get(guestId);
        if (guest == null) {
            System.out.println("Guest not found.");
            return;
        }
        
        System.out.println("\n--- Guest Details ---");
        System.out.println("ID: " + guest.getGuestId());
        System.out.println("Name: " + guest.getName());
        System.out.println("Email: " + guest.getEmail());
        System.out.println("Phone: " + guest.getPhone());
        System.out.println("Address: " + guest.getAddress());
        System.out.println("Registration Date: " + guest.getRegistrationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        System.out.println("Total Bookings: " + guest.getBookingHistory().size());
    }
    
    private void updateGuestInfo() {
        System.out.print("Enter guest ID: ");
        String guestId = scanner.nextLine();
        
        Guest guest = guests.get(guestId);
        if (guest == null) {
            System.out.println("Guest not found.");
            return;
        }
        
        System.out.println("What would you like to update?");
        System.out.println("1. Email");
        System.out.println("2. Phone");
        System.out.println("3. Address");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                System.out.print("Enter new email: ");
                String newEmail = scanner.nextLine();
                guest.setEmail(newEmail);
                System.out.println("Email updated successfully!");
                break;
            case 2:
                System.out.print("Enter new phone: ");
                String newPhone = scanner.nextLine();
                guest.setPhone(newPhone);
                System.out.println("Phone updated successfully!");
                break;
            case 3:
                System.out.print("Enter new address: ");
                String newAddress = scanner.nextLine();
                guest.setAddress(newAddress);
                System.out.println("Address updated successfully!");
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }
    
    private void viewAllGuests() {
        System.out.println("\n--- All Guests ---");
        if (guests.isEmpty()) {
            System.out.println("No guests registered.");
            return;
        }
        
        for (Guest guest : guests.values()) {
            System.out.println(guest);
        }
    }
    
    private void roomManagement() {
        System.out.println("\n--- Room Management ---");
        System.out.println("1. View All Rooms");
        System.out.println("2. View Room Details");
        System.out.println("3. Check Room Availability");
        System.out.println("4. Update Room Status");
        System.out.println("5. Back to Main Menu");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1: viewAllRooms(); break;
            case 2: viewRoomDetails(); break;
            case 3: checkRoomAvailability(); break;
            case 4: updateRoomStatus(); break;
            case 5: return;
            default: System.out.println("Invalid choice.");
        }
    }
    
    private void viewAllRooms() {
        System.out.println("\n--- All Rooms ---");
        
        Map<RoomType, List<Room>> roomsByType = rooms.values().stream()
                .collect(Collectors.groupingBy(Room::getType));
        
        for (RoomType type : RoomType.values()) {
            List<Room> roomsOfType = roomsByType.get(type);
            if (roomsOfType != null && !roomsOfType.isEmpty()) {
                System.out.println("\n" + type + " Rooms:");
                for (Room room : roomsOfType) {
                    System.out.println("  " + room);
                }
            }
        }
    }
    
    private void viewRoomDetails() {
        System.out.print("Enter room number: ");
        String roomNumber = scanner.nextLine();
        
        Room room = rooms.get(roomNumber);
        if (room == null) {
            System.out.println("Room not found.");
            return;
        }
        
        System.out.println("\n--- Room Details ---");
        System.out.println("Room Number: " + room.getRoomNumber());
        System.out.println("Type: " + room.getType());
        System.out.println("Price per night: $" + room.getPricePerNight());
        System.out.println("Status: " + room.getStatus());
        System.out.println("Amenities: " + room.getAmenities());
        System.out.println("Max Occupancy: " + room.getMaxOccupancy());
        
        // Show current booking if occupied
        if (room.getStatus() == RoomStatus.OCCUPIED) {
            Booking currentBooking = bookings.values().stream()
                    .filter(b -> b.getRoomNumber().equals(roomNumber) && 
                                b.getStatus() == BookingStatus.CHECKED_IN)
                    .findFirst()
                    .orElse(null);
            
            if (currentBooking != null) {
                System.out.println("Current Guest: " + guests.get(currentBooking.getGuestId()).getName());
                System.out.println("Check-in Date: " + currentBooking.getCheckInDate());
                System.out.println("Check-out Date: " + currentBooking.getCheckOutDate());
            }
        }
    }
    
    private void checkRoomAvailability() {
        System.out.print("Enter check-in date (yyyy-MM-dd): ");
        String checkInStr = scanner.nextLine();
        
        System.out.print("Enter check-out date (yyyy-MM-dd): ");
        String checkOutStr = scanner.nextLine();
        
        try {
            LocalDate checkIn = LocalDate.parse(checkInStr);
            LocalDate checkOut = LocalDate.parse(checkOutStr);
            
            if (checkOut.isBefore(checkIn)) {
                System.out.println("Check-out date must be after check-in date.");
                return;
            }
            
            List<Room> availableRooms = getAvailableRooms(checkIn, checkOut);
            
            if (availableRooms.isEmpty()) {
                System.out.println("No rooms available for the selected dates.");
                return;
            }
            
            System.out.println("\n--- Available Rooms ---");
            for (Room room : availableRooms) {
                System.out.println(room);
            }
            
        } catch (Exception e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd");
        }
    }
    
    private void updateRoomStatus() {
        System.out.print("Enter room number: ");
        String roomNumber = scanner.nextLine();
        
        Room room = rooms.get(roomNumber);
        if (room == null) {
            System.out.println("Room not found.");
            return;
        }
        
        System.out.println("Current status: " + room.getStatus());
        System.out.println("Select new status:");
        System.out.println("1. AVAILABLE");
        System.out.println("2. OCCUPIED");
        System.out.println("3. MAINTENANCE");
        System.out.println("4. OUT_OF_ORDER");
        System.out.print("Choose status: ");
        
        int choice = getIntInput();
        
        RoomStatus newStatus = switch (choice) {
            case 1 -> RoomStatus.AVAILABLE;
            case 2 -> RoomStatus.OCCUPIED;
            case 3 -> RoomStatus.MAINTENANCE;
            case 4 -> RoomStatus.OUT_OF_ORDER;
            default -> null;
        };
        
        if (newStatus != null) {
            room.setStatus(newStatus);
            System.out.println("Room status updated successfully!");
        } else {
            System.out.println("Invalid choice.");
        }
    }
    
    private void bookingManagement() {
        System.out.println("\n--- Booking Management ---");
        System.out.println("1. Create New Booking");
        System.out.println("2. View Booking Details");
        System.out.println("3. Update Booking");
        System.out.println("4. Cancel Booking");
        System.out.println("5. View All Bookings");
        System.out.println("6. Back to Main Menu");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1: createBooking(); break;
            case 2: viewBookingDetails(); break;
            case 3: updateBooking(); break;
            case 4: cancelBooking(); break;
            case 5: viewAllBookings(); break;
            case 6: return;
            default: System.out.println("Invalid choice.");
        }
    }
    
    private void createBooking() {
        System.out.print("Enter guest ID: ");
        String guestId = scanner.nextLine();
        
        Guest guest = guests.get(guestId);
        if (guest == null) {
            System.out.println("Guest not found. Please register the guest first.");
            return;
        }
        
        System.out.print("Enter check-in date (yyyy-MM-dd): ");
        String checkInStr = scanner.nextLine();
        
        System.out.print("Enter check-out date (yyyy-MM-dd): ");
        String checkOutStr = scanner.nextLine();
        
        try {
            LocalDate checkIn = LocalDate.parse(checkInStr);
            LocalDate checkOut = LocalDate.parse(checkOutStr);
            
            if (checkOut.isBefore(checkIn)) {
                System.out.println("Check-out date must be after check-in date.");
                return;
            }
            
            List<Room> availableRooms = getAvailableRooms(checkIn, checkOut);
            
            if (availableRooms.isEmpty()) {
                System.out.println("No rooms available for the selected dates.");
                return;
            }
            
            System.out.println("\n--- Available Rooms ---");
            for (int i = 0; i < availableRooms.size(); i++) {
                System.out.println((i + 1) + ". " + availableRooms.get(i));
            }
            
            System.out.print("Select room (enter number): ");
            int roomChoice = getIntInput();
            
            if (roomChoice < 1 || roomChoice > availableRooms.size()) {
                System.out.println("Invalid room selection.");
                return;
            }
            
            Room selectedRoom = availableRooms.get(roomChoice - 1);
            
            System.out.print("Enter number of guests: ");
            int numGuests = getIntInput();
            
            if (numGuests > selectedRoom.getMaxOccupancy()) {
                System.out.println("Number of guests exceeds room capacity.");
                return;
            }
            
            String bookingId = UUID.randomUUID().toString().substring(0, 8);
            Booking booking = new Booking(bookingId, guestId, selectedRoom.getRoomNumber(), 
                                        checkIn, checkOut, numGuests);
            
            bookings.put(bookingId, booking);
            guest.addBooking(bookingId);
            
            System.out.println("Booking created successfully!");
            System.out.println("Booking ID: " + bookingId);
            System.out.println("Total Amount: $" + booking.getTotalAmount());
            
        } catch (Exception e) {
            System.out.println("Error creating booking: " + e.getMessage());
        }
    }
    
    private void viewBookingDetails() {
        System.out.print("Enter booking ID: ");
        String bookingId = scanner.nextLine();
        
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            System.out.println("Booking not found.");
            return;
        }
        
        Guest guest = guests.get(booking.getGuestId());
        Room room = rooms.get(booking.getRoomNumber());
        
        System.out.println("\n--- Booking Details ---");
        System.out.println("Booking ID: " + booking.getBookingId());
        System.out.println("Guest: " + guest.getName());
        System.out.println("Room: " + room.getRoomNumber() + " (" + room.getType() + ")");
        System.out.println("Check-in Date: " + booking.getCheckInDate());
        System.out.println("Check-out Date: " + booking.getCheckOutDate());
        System.out.println("Number of Guests: " + booking.getNumGuests());
        System.out.println("Total Amount: $" + booking.getTotalAmount());
        System.out.println("Status: " + booking.getStatus());
        System.out.println("Booking Date: " + booking.getBookingDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        
        if (!booking.getServices().isEmpty()) {
            System.out.println("Additional Services:");
            for (String serviceId : booking.getServices()) {
                HotelService service = services.get(serviceId);
                if (service != null) {
                    System.out.println("  - " + service.getName() + " ($" + service.getPrice() + ")");
                }
            }
        }
    }
    
    private void updateBooking() {
        System.out.print("Enter booking ID: ");
        String bookingId = scanner.nextLine();
        
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            System.out.println("Booking not found.");
            return;
        }
        
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            System.out.println("Booking cannot be updated in current status: " + booking.getStatus());
            return;
        }
        
        System.out.println("What would you like to update?");
        System.out.println("1. Add Service");
        System.out.println("2. Remove Service");
        System.out.println("3. Update Number of Guests");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1: addServiceToBooking(booking); break;
            case 2: removeServiceFromBooking(booking); break;
            case 3: updateGuestCount(booking); break;
            default: System.out.println("Invalid choice.");
        }
    }
    
    private void addServiceToBooking(Booking booking) {
        System.out.println("\n--- Available Services ---");
        for (HotelService service : services.values()) {
            System.out.println(service.getServiceId() + ": " + service.getName() + " - $" + service.getPrice());
        }
        
        System.out.print("Enter service ID to add: ");
        String serviceId = scanner.nextLine();
        
        HotelService service = services.get(serviceId);
        if (service == null) {
            System.out.println("Service not found.");
            return;
        }
        
        booking.addService(serviceId);
        System.out.println("Service added successfully!");
        System.out.println("Updated total amount: $" + booking.getTotalAmount());
    }
    
    private void removeServiceFromBooking(Booking booking) {
        if (booking.getServices().isEmpty()) {
            System.out.println("No services to remove.");
            return;
        }
        
        System.out.println("\n--- Current Services ---");
        for (String serviceId : booking.getServices()) {
            HotelService service = services.get(serviceId);
            if (service != null) {
                System.out.println(serviceId + ": " + service.getName() + " - $" + service.getPrice());
            }
        }
        
        System.out.print("Enter service ID to remove: ");
        String serviceId = scanner.nextLine();
        
        if (booking.removeService(serviceId)) {
            System.out.println("Service removed successfully!");
            System.out.println("Updated total amount: $" + booking.getTotalAmount());
        } else {
            System.out.println("Service not found in booking.");
        }
    }
    
    private void updateGuestCount(Booking booking) {
        Room room = rooms.get(booking.getRoomNumber());
        
        System.out.println("Current number of guests: " + booking.getNumGuests());
        System.out.println("Maximum occupancy: " + room.getMaxOccupancy());
        System.out.print("Enter new number of guests: ");
        
        int newGuestCount = getIntInput();
        
        if (newGuestCount > room.getMaxOccupancy()) {
            System.out.println("Number of guests exceeds room capacity.");
            return;
        }
        
        booking.setNumGuests(newGuestCount);
        System.out.println("Guest count updated successfully!");
    }
    
    private void cancelBooking() {
        System.out.print("Enter booking ID: ");
        String bookingId = scanner.nextLine();
        
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            System.out.println("Booking not found.");
            return;
        }
        
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            System.out.println("Booking is already cancelled.");
            return;
        }
        
        if (booking.getStatus() == BookingStatus.CHECKED_IN) {
            System.out.println("Cannot cancel booking - guest is already checked in.");
            return;
        }
        
        booking.cancel();
        System.out.println("Booking cancelled successfully!");
        
        // Calculate refund amount (simplified logic)
        double refundAmount = booking.getTotalAmount() * 0.8; // 20% cancellation fee
        System.out.println("Refund amount: $" + refundAmount);
    }
    
    private void viewAllBookings() {
        System.out.println("\n--- All Bookings ---");
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }
        
        for (Booking booking : bookings.values()) {
            Guest guest = guests.get(booking.getGuestId());
            System.out.println(booking.getBookingId() + " - " + guest.getName() + 
                             " - Room " + booking.getRoomNumber() + " - " + booking.getStatus());
        }
    }
    
    private void checkInCheckOut() {
        System.out.println("\n--- Check-in/Check-out ---");
        System.out.println("1. Check-in Guest");
        System.out.println("2. Check-out Guest");
        System.out.println("3. View Current Occupancy");
        System.out.println("4. Back to Main Menu");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1: checkInGuest(); break;
            case 2: checkOutGuest(); break;
            case 3: viewCurrentOccupancy(); break;
            case 4: return;
            default: System.out.println("Invalid choice.");
        }
    }
    
    private void checkInGuest() {
        System.out.print("Enter booking ID: ");
        String bookingId = scanner.nextLine();
        
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            System.out.println("Booking not found.");
            return;
        }
        
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            System.out.println("Booking is not confirmed or already processed.");
            return;
        }
        
        LocalDate today = LocalDate.now();
        if (today.isBefore(booking.getCheckInDate())) {
            System.out.println("Check-in date is in the future.");
            return;
        }
        
        Room room = rooms.get(booking.getRoomNumber());
        if (room.getStatus() != RoomStatus.AVAILABLE) {
            System.out.println("Room is not available for check-in.");
            return;
        }
        
        booking.checkIn();
        room.setStatus(RoomStatus.OCCUPIED);
        
        Guest guest = guests.get(booking.getGuestId());
        System.out.println("Guest " + guest.getName() + " checked in successfully!");
        System.out.println("Room: " + room.getRoomNumber());
        System.out.println("Check-out Date: " + booking.getCheckOutDate());
    }
    
    private void checkOutGuest() {
        System.out.print("Enter booking ID: ");
        String bookingId = scanner.nextLine();
        
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            System.out.println("Booking not found.");
            return;
        }
        
        if (booking.getStatus() != BookingStatus.CHECKED_IN) {
            System.out.println("Guest is not checked in.");
            return;
        }
        
        booking.checkOut();
        Room room = rooms.get(booking.getRoomNumber());
        room.setStatus(RoomStatus.AVAILABLE);
        
        Guest guest = guests.get(booking.getGuestId());
        System.out.println("Guest " + guest.getName() + " checked out successfully!");
        System.out.println("Total Amount: $" + booking.getTotalAmount());
        
        // Process payment if not already done
        if (payments.values().stream().noneMatch(p -> p.getBookingId().equals(bookingId))) {
            System.out.println("Payment required for checkout.");
            processPayment(booking);
        }
    }
    
    private void viewCurrentOccupancy() {
        System.out.println("\n--- Current Occupancy ---");
        
        List<Booking> activeBookings = bookings.values().stream()
                .filter(b -> b.getStatus() == BookingStatus.CHECKED_IN)
                .collect(Collectors.toList());
        
        if (activeBookings.isEmpty()) {
            System.out.println("No guests currently checked in.");
            return;
        }
        
        for (Booking booking : activeBookings) {
            Guest guest = guests.get(booking.getGuestId());
            Room room = rooms.get(booking.getRoomNumber());
            System.out.println("Room " + room.getRoomNumber() + " - " + guest.getName() + 
                             " (Check-out: " + booking.getCheckOutDate() + ")");
        }
    }
    
    private void servicesManagement() {
        System.out.println("\n--- Services Management ---");
        System.out.println("1. View All Services");
        System.out.println("2. Add Service to Booking");
        System.out.println("3. View Service Usage");
        System.out.println("4. Back to Main Menu");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1: viewAllServices(); break;
            case 2: addServiceToExistingBooking(); break;
            case 3: viewServiceUsage(); break;
            case 4: return;
            default: System.out.println("Invalid choice.");
        }
    }
    
    private void viewAllServices() {
        System.out.println("\n--- All Services ---");
        
        Map<ServiceType, List<HotelService>> servicesByType = services.values().stream()
                .collect(Collectors.groupingBy(HotelService::getType));
        
        for (ServiceType type : ServiceType.values()) {
            List<HotelService> servicesOfType = servicesByType.get(type);
            if (servicesOfType != null && !servicesOfType.isEmpty()) {
                System.out.println("\n" + type + " Services:");
                for (HotelService service : servicesOfType) {
                    System.out.println("  " + service);
                }
            }
        }
    }
    
    private void addServiceToExistingBooking() {
        System.out.print("Enter booking ID: ");
        String bookingId = scanner.nextLine();
        
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            System.out.println("Booking not found.");
            return;
        }
        
        addServiceToBooking(booking);
    }
    
    private void viewServiceUsage() {
        System.out.println("\n--- Service Usage Report ---");
        
        Map<String, Long> serviceUsage = bookings.values().stream()
                .flatMap(booking -> booking.getServices().stream())
                .collect(Collectors.groupingBy(serviceId -> serviceId, Collectors.counting()));
        
        if (serviceUsage.isEmpty()) {
            System.out.println("No services have been used.");
            return;
        }
        
        serviceUsage.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(entry -> {
                    HotelService service = services.get(entry.getKey());
                    if (service != null) {
                        System.out.println(service.getName() + ": " + entry.getValue() + " times");
                    }
                });
    }
    
    private void paymentManagement() {
        System.out.println("\n--- Payment Management ---");
        System.out.println("1. Process Payment");
        System.out.println("2. View Payment Details");
        System.out.println("3. View All Payments");
        System.out.println("4. Back to Main Menu");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1: processPaymentMenu(); break;
            case 2: viewPaymentDetails(); break;
            case 3: viewAllPayments(); break;
            case 4: return;
            default: System.out.println("Invalid choice.");
        }
    }
    
    private void processPaymentMenu() {
        System.out.print("Enter booking ID: ");
        String bookingId = scanner.nextLine();
        
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            System.out.println("Booking not found.");
            return;
        }
        
        processPayment(booking);
    }
    
    private void processPayment(Booking booking) {
        System.out.println("\n--- Payment Processing ---");
        System.out.println("Total Amount: $" + booking.getTotalAmount());
        
        System.out.println("Select Payment Method:");
        System.out.println("1. Credit Card");
        System.out.println("2. Debit Card");
        System.out.println("3. Cash");
        System.out.println("4. Bank Transfer");
        System.out.print("Choose method: ");
        
        int choice = getIntInput();
        
        PaymentMethod method = switch (choice) {
            case 1 -> PaymentMethod.CREDIT_CARD;
            case 2 -> PaymentMethod.DEBIT_CARD;
            case 3 -> PaymentMethod.CASH;
            case 4 -> PaymentMethod.BANK_TRANSFER;
            default -> null;
        };
        
        if (method == null) {
            System.out.println("Invalid payment method.");
            return;
        }
        
        String paymentId = UUID.randomUUID().toString().substring(0, 8);
        Payment payment = new Payment(paymentId, booking.getBookingId(), 
                                    booking.getTotalAmount(), method);
        
        // Simulate payment processing
        if (Math.random() > 0.1) { // 90% success rate
            payment.markAsCompleted();
            payments.put(paymentId, payment);
            System.out.println("Payment processed successfully!");
            System.out.println("Payment ID: " + paymentId);
        } else {
            payment.markAsFailed();
            System.out.println("Payment failed. Please try again.");
        }
    }
    
    private void viewPaymentDetails() {
        System.out.print("Enter payment ID: ");
        String paymentId = scanner.nextLine();
        
        Payment payment = payments.get(paymentId);
        if (payment == null) {
            System.out.println("Payment not found.");
            return;
        }
        
        System.out.println("\n--- Payment Details ---");
        System.out.println("Payment ID: " + payment.getPaymentId());
        System.out.println("Booking ID: " + payment.getBookingId());
        System.out.println("Amount: $" + payment.getAmount());
        System.out.println("Method: " + payment.getMethod());
        System.out.println("Status: " + payment.getStatus());
        System.out.println("Payment Date: " + payment.getPaymentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }
    
    private void viewAllPayments() {
        System.out.println("\n--- All Payments ---");
        if (payments.isEmpty()) {
            System.out.println("No payments found.");
            return;
        }
        
        for (Payment payment : payments.values()) {
            System.out.println(payment);
        }
    }
    
    private void generateReports() {
        System.out.println("\n--- Reports ---");
        System.out.println("1. Occupancy Report");
        System.out.println("2. Revenue Report");
        System.out.println("3. Guest Statistics");
        System.out.println("4. Service Usage Report");
        System.out.println("5. Back to Main Menu");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1: generateOccupancyReport(); break;
            case 2: generateRevenueReport(); break;
            case 3: generateGuestStatistics(); break;
            case 4: generateServiceUsageReport(); break;
            case 5: return;
            default: System.out.println("Invalid choice.");
        }
    }
    
    private void generateOccupancyReport() {
        System.out.println("\n--- Occupancy Report ---");
        
        long totalRooms = rooms.size();
        long occupiedRooms = rooms.values().stream()
                .filter(room -> room.getStatus() == RoomStatus.OCCUPIED)
                .count();
        
        long availableRooms = rooms.values().stream()
                .filter(room -> room.getStatus() == RoomStatus.AVAILABLE)
                .count();
        
        long maintenanceRooms = rooms.values().stream()
                .filter(room -> room.getStatus() == RoomStatus.MAINTENANCE)
                .count();
        
        System.out.println("Total Rooms: " + totalRooms);
        System.out.println("Occupied Rooms: " + occupiedRooms);
        System.out.println("Available Rooms: " + availableRooms);
        System.out.println("Maintenance Rooms: " + maintenanceRooms);
        System.out.println("Occupancy Rate: " + String.format("%.1f%%", 
                (double) occupiedRooms / totalRooms * 100));
    }
    
    private void generateRevenueReport() {
        System.out.println("\n--- Revenue Report ---");
        
        double totalRevenue = payments.values().stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.COMPLETED)
                .mapToDouble(Payment::getAmount)
                .sum();
        
        long totalBookings = bookings.values().stream()
                .filter(booking -> booking.getStatus() == BookingStatus.COMPLETED)
                .count();
        
        System.out.println("Total Revenue: $" + String.format("%.2f", totalRevenue));
        System.out.println("Total Completed Bookings: " + totalBookings);
        
        if (totalBookings > 0) {
            System.out.println("Average Revenue per Booking: $" + 
                    String.format("%.2f", totalRevenue / totalBookings));
        }
    }
    
    private void generateGuestStatistics() {
        System.out.println("\n--- Guest Statistics ---");
        
        System.out.println("Total Registered Guests: " + guests.size());
        
        long activeBookings = bookings.values().stream()
                .filter(booking -> booking.getStatus() == BookingStatus.CHECKED_IN)
                .count();
        
        System.out.println("Currently Checked-in Guests: " + activeBookings);
        
        // Top guests by booking count
        Guest topGuest = guests.values().stream()
                .max(Comparator.comparingInt(guest -> guest.getBookingHistory().size()))
                .orElse(null);
        
        if (topGuest != null) {
            System.out.println("Top Guest: " + topGuest.getName() + 
                    " (" + topGuest.getBookingHistory().size() + " bookings)");
        }
    }
    
    private void generateServiceUsageReport() {
        System.out.println("\n--- Service Usage Report ---");
        
        Map<String, Long> serviceUsage = bookings.values().stream()
                .flatMap(booking -> booking.getServices().stream())
                .collect(Collectors.groupingBy(serviceId -> serviceId, Collectors.counting()));
        
        if (serviceUsage.isEmpty()) {
            System.out.println("No services have been used.");
            return;
        }
        
        System.out.println("Service Usage (sorted by popularity):");
        serviceUsage.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(entry -> {
                    HotelService service = services.get(entry.getKey());
                    if (service != null) {
                        System.out.println("  " + service.getName() + ": " + entry.getValue() + " times");
                    }
                });
    }
    
    private List<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        Set<String> occupiedRooms = bookings.values().stream()
                .filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED || 
                                 booking.getStatus() == BookingStatus.CHECKED_IN)
                .filter(booking -> !(checkOut.isBefore(booking.getCheckInDate()) || 
                                   checkIn.isAfter(booking.getCheckOutDate())))
                .map(Booking::getRoomNumber)
                .collect(Collectors.toSet());
        
        return rooms.values().stream()
                .filter(room -> room.getStatus() == RoomStatus.AVAILABLE)
                .filter(room -> !occupiedRooms.contains(room.getRoomNumber()))
                .collect(Collectors.toList());
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
        HotelBookingSystem system = new HotelBookingSystem();
        system.start();
    }
}

// Supporting classes would be defined in separate files
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

enum BookingStatus {
    CONFIRMED,
    CHECKED_IN,
    CHECKED_OUT,
    COMPLETED,
    CANCELLED
}

enum PaymentMethod {
    CREDIT_CARD,
    DEBIT_CARD,
    CASH,
    BANK_TRANSFER
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
    ENTERTAINMENT
} 