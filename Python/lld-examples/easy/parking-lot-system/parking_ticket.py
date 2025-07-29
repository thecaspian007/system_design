from datetime import datetime
from typing import Optional
import uuid
from vehicle import Vehicle
from parking_spot import ParkingSpot

class ParkingTicket:
    """Parking ticket class"""
    
    def __init__(self, vehicle: Vehicle, parking_spot: ParkingSpot):
        self.ticket_id = str(uuid.uuid4())[:8].upper()
        self.vehicle = vehicle
        self.parking_spot = parking_spot
        self.entry_time = datetime.now()
        self.exit_time: Optional[datetime] = None
        self.is_paid = False
        self.amount_paid = 0.0
    
    def get_formatted_entry_time(self) -> str:
        """Get formatted entry time"""
        return self.entry_time.strftime("%Y-%m-%d %H:%M:%S")
    
    def get_formatted_exit_time(self) -> str:
        """Get formatted exit time"""
        if self.exit_time:
            return self.exit_time.strftime("%Y-%m-%d %H:%M:%S")
        return "Not exited"
    
    def get_parking_duration_in_hours(self) -> int:
        """Get parking duration in hours"""
        end_time = self.exit_time if self.exit_time else datetime.now()
        duration = end_time - self.entry_time
        return max(1, int(duration.total_seconds() / 3600))  # Minimum 1 hour
    
    def mark_as_paid(self, amount: float):
        """Mark ticket as paid"""
        self.is_paid = True
        self.amount_paid = amount
        self.exit_time = datetime.now()
    
    def __str__(self):
        return f"Ticket {self.ticket_id} - {self.vehicle} - Spot {self.parking_spot.spot_number} - {self.get_formatted_entry_time()}" 