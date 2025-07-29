from datetime import datetime
from enum import Enum
import uuid

class BookingStatus(Enum):
    """Booking status enum"""
    PENDING = "Pending"
    CONFIRMED = "Confirmed"
    CHECKED_IN = "Checked In"
    CHECKED_OUT = "Checked Out"
    CANCELLED = "Cancelled"

class Booking:
    """Booking class representing a hotel booking"""
    
    def __init__(self, guest, room, check_in_date: datetime, check_out_date: datetime):
        self.booking_id = str(uuid.uuid4())[:8].upper()
        self.guest = guest
        self.room = room
        self.check_in_date = check_in_date
        self.check_out_date = check_out_date
        self.status = BookingStatus.CONFIRMED
        self.booking_date = datetime.now()
        
        # Calculate total amount
        nights = (check_out_date - check_in_date).days
        self.total_amount = nights * room.price_per_night
    
    def overlaps_with(self, check_in: datetime, check_out: datetime) -> bool:
        """Check if booking overlaps with given dates"""
        return not (check_out <= self.check_in_date or check_in >= self.check_out_date)
    
    def can_cancel(self) -> bool:
        """Check if booking can be cancelled"""
        return self.status in [BookingStatus.PENDING, BookingStatus.CONFIRMED]
    
    def can_check_in(self) -> bool:
        """Check if guest can check in"""
        return self.status == BookingStatus.CONFIRMED
    
    def can_check_out(self) -> bool:
        """Check if guest can check out"""
        return self.status == BookingStatus.CHECKED_IN
    
    def cancel(self):
        """Cancel booking"""
        self.status = BookingStatus.CANCELLED
    
    def check_in(self):
        """Check in guest"""
        self.status = BookingStatus.CHECKED_IN
    
    def check_out(self):
        """Check out guest"""
        self.status = BookingStatus.CHECKED_OUT
    
    def __str__(self):
        return f"Booking {self.booking_id} - {self.guest.name} - Room {self.room.room_number}"
