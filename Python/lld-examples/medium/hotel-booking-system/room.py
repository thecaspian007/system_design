from enum import Enum
from typing import List, Optional
from datetime import datetime

class RoomType(Enum):
    """Room type enum"""
    STANDARD = "Standard"
    DELUXE = "Deluxe"
    SUITE = "Suite"
    PRESIDENTIAL = "Presidential"

class RoomStatus(Enum):
    """Room status enum"""
    AVAILABLE = "Available"
    OCCUPIED = "Occupied"
    MAINTENANCE = "Maintenance"
    OUT_OF_ORDER = "Out of Order"

class Room:
    """Room class representing a hotel room"""
    
    def __init__(self, room_number: str, room_type: RoomType, price_per_night: float):
        self.room_number = room_number
        self.room_type = room_type
        self.price_per_night = price_per_night
        self.status = RoomStatus.AVAILABLE
        self.bookings: List = []  # List of bookings
        self.amenities: List[str] = []
    
    def is_available(self, check_in: datetime, check_out: datetime) -> bool:
        """Check if room is available for given dates"""
        if self.status != RoomStatus.AVAILABLE:
            return False
        
        for booking in self.bookings:
            if booking.overlaps_with(check_in, check_out):
                return False
        
        return True
    
    def is_currently_available(self) -> bool:
        """Check if room is currently available"""
        return self.status == RoomStatus.AVAILABLE
    
    def add_booking(self, booking):
        """Add booking to room"""
        self.bookings.append(booking)
    
    def remove_booking(self, booking):
        """Remove booking from room"""
        if booking in self.bookings:
            self.bookings.remove(booking)
    
    def set_status(self, status: RoomStatus):
        """Set room status"""
        self.status = status
    
    def add_amenity(self, amenity: str):
        """Add amenity to room"""
        self.amenities.append(amenity)
    
    def __str__(self):
        return f"Room {self.room_number} - {self.room_type.value} - ${self.price_per_night}/night"
