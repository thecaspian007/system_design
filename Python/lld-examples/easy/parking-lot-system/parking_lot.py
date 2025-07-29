from typing import List, Dict, Optional
from parking_floor import ParkingFloor
from parking_ticket import ParkingTicket
from vehicle import Vehicle

class ParkingLot:
    """Main parking lot class"""
    
    def __init__(self, name: str, max_floors: int):
        self.name = name
        self.max_floors = max_floors
        self.floors: List[ParkingFloor] = []
        self.active_tickets: Dict[str, ParkingTicket] = {}  # ticket_id -> ticket
        self.tickets_by_license: Dict[str, ParkingTicket] = {}  # license_plate -> ticket
    
    def add_floor(self, floor: ParkingFloor):
        """Add floor to parking lot"""
        self.floors.append(floor)
    
    def park_vehicle(self, vehicle: Vehicle) -> Optional[ParkingTicket]:
        """Park vehicle in parking lot"""
        # Check if vehicle is already parked
        if vehicle.license_plate in self.tickets_by_license:
            return None  # Vehicle already parked
        
        # Find available spot
        for floor in self.floors:
            spot = floor.find_available_spot(vehicle.type)
            if spot:
                if spot.park_vehicle(vehicle):
                    ticket = ParkingTicket(vehicle, spot)
                    self.active_tickets[ticket.ticket_id] = ticket
                    self.tickets_by_license[vehicle.license_plate] = ticket
                    return ticket
        
        return None  # No available spot
    
    def process_payment(self, ticket: ParkingTicket, amount: float) -> bool:
        """Process payment for parking ticket"""
        if ticket and not ticket.is_paid:
            ticket.mark_as_paid(amount)
            
            # Remove vehicle from spot
            ticket.parking_spot.remove_vehicle()
            
            # Remove from active tracking
            if ticket.ticket_id in self.active_tickets:
                del self.active_tickets[ticket.ticket_id]
            if ticket.vehicle.license_plate in self.tickets_by_license:
                del self.tickets_by_license[ticket.vehicle.license_plate]
            
            return True
        return False
    
    def calculate_parking_fee(self, ticket: ParkingTicket) -> float:
        """Calculate parking fee"""
        if not ticket:
            return 0.0
        
        hours = ticket.get_parking_duration_in_hours()
        hourly_rate = ticket.parking_spot.hourly_rate
        
        # First hour free
        if hours <= 1:
            return 0.0
        
        return (hours - 1) * hourly_rate
    
    def find_ticket(self, ticket_id_or_license: str) -> Optional[ParkingTicket]:
        """Find ticket by ID or license plate"""
        # Try to find by ticket ID first
        ticket = self.active_tickets.get(ticket_id_or_license)
        if ticket:
            return ticket
        
        # Try to find by license plate
        return self.tickets_by_license.get(ticket_id_or_license)
    
    def find_ticket_by_license_plate(self, license_plate: str) -> Optional[ParkingTicket]:
        """Find ticket by license plate"""
        return self.tickets_by_license.get(license_plate)
    
    def get_total_spots(self) -> int:
        """Get total spots in parking lot"""
        return sum(floor.get_total_spots_all() for floor in self.floors)
    
    def get_available_spots(self) -> int:
        """Get available spots in parking lot"""
        return sum(floor.get_available_spots_all() for floor in self.floors)
    
    def get_occupied_spots(self) -> int:
        """Get occupied spots in parking lot"""
        return self.get_total_spots() - self.get_available_spots()
    
    def get_active_tickets(self) -> List[ParkingTicket]:
        """Get all active tickets"""
        return list(self.active_tickets.values()) 