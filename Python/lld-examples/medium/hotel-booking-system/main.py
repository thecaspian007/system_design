import sys
import os
from datetime import datetime, timedelta

sys.path.append(os.path.dirname(os.path.abspath(__file__)))

from hotel import Hotel
from room import Room, RoomType
from booking import Booking
from guest import Guest
from payment import PaymentMethod

class HotelBookingSystem:
    """
    Hotel Booking System - Main Application
    
    This system demonstrates:
    - Room management and availability
    - Guest registration and management
    - Booking creation and management
    - Payment processing
    - Room service and housekeeping
    
    @author System Design Repository
    """
    
    def __init__(self):
        self.hotel = Hotel("Grand Plaza Hotel", "123 Main St, City")
        self._initialize_hotel()
        
    def _initialize_hotel(self):
        """Initialize hotel with sample rooms"""
        # Add different room types
        for i in range(1, 11):
            room = Room(f"10{i}", RoomType.STANDARD, 100.0)
            self.hotel.add_room(room)
        
        for i in range(1, 6):
            room = Room(f"20{i}", RoomType.DELUXE, 150.0)
            self.hotel.add_room(room)
            
        for i in range(1, 4):
            room = Room(f"30{i}", RoomType.SUITE, 250.0)
            self.hotel.add_room(room)
        
        print(f"Hotel initialized with {self.hotel.get_total_rooms()} rooms")
    
    def start(self):
        """Start the hotel booking system"""
        print(f"=== Welcome to {self.hotel.name} ===")
        
        while True:
            try:
                self._show_main_menu()
                choice = self._get_int_input()
                
                if choice == 1:
                    self._check_availability()
                elif choice == 2:
                    self._make_booking()
                elif choice == 3:
                    self._view_booking()
                elif choice == 4:
                    self._cancel_booking()
                elif choice == 5:
                    self._check_in()
                elif choice == 6:
                    self._check_out()
                elif choice == 7:
                    self._hotel_status()
                elif choice == 8:
                    print("Thank you for using our hotel booking system!")
                    break
                else:
                    print("Invalid choice. Please try again.")
                    
            except Exception as e:
                print(f"Error: {e}")
    
    def _show_main_menu(self):
        """Show main menu"""
        print("\n--- Hotel Booking System ---")
        print("1. Check Room Availability")
        print("2. Make Booking")
        print("3. View Booking")
        print("4. Cancel Booking")
        print("5. Check In")
        print("6. Check Out")
        print("7. Hotel Status")
        print("8. Exit")
        print("Choose an option: ", end="")
    
    def _check_availability(self):
        """Check room availability"""
        print("\n--- Check Room Availability ---")
        
        check_in_str = input("Enter check-in date (YYYY-MM-DD): ")
        check_out_str = input("Enter check-out date (YYYY-MM-DD): ")
        
        try:
            check_in = datetime.strptime(check_in_str, "%Y-%m-%d")
            check_out = datetime.strptime(check_out_str, "%Y-%m-%d")
            
            if check_in >= check_out:
                print("Check-out date must be after check-in date.")
                return
            
            available_rooms = self.hotel.get_available_rooms(check_in, check_out)
            
            if not available_rooms:
                print("No rooms available for the selected dates.")
                return
            
            print(f"Available rooms ({len(available_rooms)}):")
            for room in available_rooms:
                print(f"  Room {room.room_number} - {room.room_type.value} - ${room.price_per_night}/night")
                
        except ValueError:
            print("Invalid date format. Please use YYYY-MM-DD.")
    
    def _make_booking(self):
        """Make a booking"""
        print("\n--- Make Booking ---")
        
        # Guest information
        guest_name = input("Enter guest name: ")
        guest_email = input("Enter guest email: ")
        guest_phone = input("Enter guest phone: ")
        
        guest = Guest(guest_name, guest_email, guest_phone)
        
        # Booking details
        room_number = input("Enter room number: ")
        check_in_str = input("Enter check-in date (YYYY-MM-DD): ")
        check_out_str = input("Enter check-out date (YYYY-MM-DD): ")
        
        try:
            check_in = datetime.strptime(check_in_str, "%Y-%m-%d")
            check_out = datetime.strptime(check_out_str, "%Y-%m-%d")
            
            booking = self.hotel.make_booking(guest, room_number, check_in, check_out)
            
            if booking:
                print("Booking created successfully!")
                print(f"Booking ID: {booking.booking_id}")
                print(f"Total Amount: ${booking.total_amount:.2f}")
            else:
                print("Booking failed. Room may not be available.")
                
        except ValueError:
            print("Invalid date format. Please use YYYY-MM-DD.")
    
    def _view_booking(self):
        """View booking details"""
        print("\n--- View Booking ---")
        booking_id = input("Enter booking ID: ")
        
        booking = self.hotel.get_booking(booking_id)
        
        if booking:
            print(f"Booking ID: {booking.booking_id}")
            print(f"Guest: {booking.guest.name}")
            print(f"Room: {booking.room.room_number}")
            print(f"Check-in: {booking.check_in_date.strftime('%Y-%m-%d')}")
            print(f"Check-out: {booking.check_out_date.strftime('%Y-%m-%d')}")
            print(f"Status: {booking.status.value}")
            print(f"Total Amount: ${booking.total_amount:.2f}")
        else:
            print("Booking not found.")
    
    def _cancel_booking(self):
        """Cancel booking"""
        print("\n--- Cancel Booking ---")
        booking_id = input("Enter booking ID: ")
        
        if self.hotel.cancel_booking(booking_id):
            print("Booking cancelled successfully!")
        else:
            print("Booking cancellation failed.")
    
    def _check_in(self):
        """Check in guest"""
        print("\n--- Check In ---")
        booking_id = input("Enter booking ID: ")
        
        if self.hotel.check_in(booking_id):
            print("Check-in successful!")
        else:
            print("Check-in failed.")
    
    def _check_out(self):
        """Check out guest"""
        print("\n--- Check Out ---")
        booking_id = input("Enter booking ID: ")
        
        if self.hotel.check_out(booking_id):
            print("Check-out successful!")
        else:
            print("Check-out failed.")
    
    def _hotel_status(self):
        """Show hotel status"""
        print("\n--- Hotel Status ---")
        print(f"Hotel: {self.hotel.name}")
        print(f"Total Rooms: {self.hotel.get_total_rooms()}")
        print(f"Available Rooms: {self.hotel.get_available_rooms_count()}")
        print(f"Occupied Rooms: {self.hotel.get_occupied_rooms_count()}")
    
    def _get_int_input(self, prompt="") -> int:
        """Get integer input with validation"""
        while True:
            try:
                return int(input(prompt))
            except ValueError:
                print("Please enter a valid number: ", end="")

if __name__ == "__main__":
    hotel_system = HotelBookingSystem()
    hotel_system.start()
