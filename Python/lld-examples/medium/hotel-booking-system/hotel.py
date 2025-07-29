from typing import List, Dict, Optional
from datetime import datetime
from room import Room
from booking import Booking
from guest import Guest

class Hotel:
    """Hotel class managing rooms and bookings"""
    
    def __init__(self, name: str, address: str):
        self.name = name
        self.address = address
        self.rooms: Dict[str, Room] = {}
        self.bookings: Dict[str, Booking] = {}
    
    def add_room(self, room: Room):
        """Add room to hotel"""
        self.rooms[room.room_number] = room
    
    def get_room(self, room_number: str) -> Optional[Room]:
        """Get room by number"""
        return self.rooms.get(room_number)
    
    def get_available_rooms(self, check_in: datetime, check_out: datetime) -> List[Room]:
        """Get available rooms for given dates"""
        available_rooms = []
        
        for room in self.rooms.values():
            if room.is_available(check_in, check_out):
                available_rooms.append(room)
        
        return available_rooms
    
    def make_booking(self, guest: Guest, room_number: str, check_in: datetime, check_out: datetime) -> Optional[Booking]:
        """Make a booking"""
        room = self.get_room(room_number)
        
        if not room or not room.is_available(check_in, check_out):
            return None
        
        booking = Booking(guest, room, check_in, check_out)
        self.bookings[booking.booking_id] = booking
        room.add_booking(booking)
        
        return booking
    
    def get_booking(self, booking_id: str) -> Optional[Booking]:
        """Get booking by ID"""
        return self.bookings.get(booking_id)
    
    def cancel_booking(self, booking_id: str) -> bool:
        """Cancel booking"""
        booking = self.get_booking(booking_id)
        
        if booking and booking.can_cancel():
            booking.cancel()
            booking.room.remove_booking(booking)
            return True
        
        return False
    
    def check_in(self, booking_id: str) -> bool:
        """Check in guest"""
        booking = self.get_booking(booking_id)
        
        if booking and booking.can_check_in():
            booking.check_in()
            return True
        
        return False
    
    def check_out(self, booking_id: str) -> bool:
        """Check out guest"""
        booking = self.get_booking(booking_id)
        
        if booking and booking.can_check_out():
            booking.check_out()
            return True
        
        return False
    
    def get_total_rooms(self) -> int:
        """Get total number of rooms"""
        return len(self.rooms)
    
    def get_available_rooms_count(self) -> int:
        """Get count of available rooms"""
        return sum(1 for room in self.rooms.values() if room.is_currently_available())
    
    def get_occupied_rooms_count(self) -> int:
        """Get count of occupied rooms"""
        return self.get_total_rooms() - self.get_available_rooms_count()
