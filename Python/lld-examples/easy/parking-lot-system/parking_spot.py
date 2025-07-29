from typing import Optional
from vehicle import Vehicle, VehicleType

class ParkingSpot:
    """Parking spot class"""
    
    def __init__(self, spot_number: int, floor_number: int, vehicle_type: VehicleType):
        self.spot_number = spot_number
        self.floor_number = floor_number
        self.vehicle_type = vehicle_type
        self.is_occupied = False
        self.parked_vehicle: Optional[Vehicle] = None
        self.hourly_rate = vehicle_type.hourly_rate
    
    def is_available(self) -> bool:
        """Check if spot is available"""
        return not self.is_occupied
    
    def can_park_vehicle(self, vehicle: Vehicle) -> bool:
        """Check if vehicle can park in this spot"""
        return self.is_available() and vehicle.type == self.vehicle_type
    
    def park_vehicle(self, vehicle: Vehicle) -> bool:
        """Park vehicle in this spot"""
        if self.can_park_vehicle(vehicle):
            self.is_occupied = True
            self.parked_vehicle = vehicle
            return True
        return False
    
    def remove_vehicle(self):
        """Remove vehicle from spot"""
        self.is_occupied = False
        self.parked_vehicle = None
    
    def __str__(self):
        return f"Spot{{floor={self.floor_number}, spot={self.spot_number}, type={self.vehicle_type}, occupied={self.is_occupied}}}" 