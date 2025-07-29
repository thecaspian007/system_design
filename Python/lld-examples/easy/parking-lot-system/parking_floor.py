from typing import List, Optional
from parking_spot import ParkingSpot
from vehicle import Vehicle, VehicleType

class ParkingFloor:
    """Parking floor class"""
    
    def __init__(self, floor_number: int, car_spots: int, motorcycle_spots: int, truck_spots: int):
        self.floor_number = floor_number
        self.parking_spots: List[ParkingSpot] = []
        
        self._initialize_parking_spots(car_spots, motorcycle_spots, truck_spots)
    
    def _initialize_parking_spots(self, car_spots: int, motorcycle_spots: int, truck_spots: int):
        """Initialize parking spots"""
        spot_number = 1
        
        # Add car spots
        for _ in range(car_spots):
            self.parking_spots.append(ParkingSpot(spot_number, self.floor_number, VehicleType.CAR))
            spot_number += 1
        
        # Add motorcycle spots
        for _ in range(motorcycle_spots):
            self.parking_spots.append(ParkingSpot(spot_number, self.floor_number, VehicleType.MOTORCYCLE))
            spot_number += 1
        
        # Add truck spots
        for _ in range(truck_spots):
            self.parking_spots.append(ParkingSpot(spot_number, self.floor_number, VehicleType.TRUCK))
            spot_number += 1
    
    def find_available_spot(self, vehicle_type: VehicleType) -> Optional[ParkingSpot]:
        """Find available spot for vehicle type"""
        for spot in self.parking_spots:
            if spot.vehicle_type == vehicle_type and spot.is_available():
                return spot
        return None
    
    def park_vehicle(self, vehicle: Vehicle) -> bool:
        """Park vehicle on this floor"""
        spot = self.find_available_spot(vehicle.type)
        if spot:
            return spot.park_vehicle(vehicle)
        return False
    
    def remove_vehicle(self, vehicle: Vehicle) -> bool:
        """Remove vehicle from this floor"""
        for spot in self.parking_spots:
            if spot.is_occupied and spot.parked_vehicle.license_plate == vehicle.license_plate:
                spot.remove_vehicle()
                return True
        return False
    
    def find_spot_by_vehicle(self, license_plate: str) -> Optional[ParkingSpot]:
        """Find spot by vehicle license plate"""
        for spot in self.parking_spots:
            if spot.is_occupied and spot.parked_vehicle.license_plate == license_plate:
                return spot
        return None
    
    def get_available_spots(self, vehicle_type: VehicleType) -> int:
        """Get available spots count for vehicle type"""
        return sum(1 for spot in self.parking_spots 
                  if spot.vehicle_type == vehicle_type and spot.is_available())
    
    def get_total_spots(self, vehicle_type: VehicleType) -> int:
        """Get total spots count for vehicle type"""
        return sum(1 for spot in self.parking_spots 
                  if spot.vehicle_type == vehicle_type)
    
    def get_total_spots_all(self) -> int:
        """Get total spots on this floor"""
        return len(self.parking_spots)
    
    def get_available_spots_all(self) -> int:
        """Get available spots on this floor"""
        return sum(1 for spot in self.parking_spots if spot.is_available())
    
    def get_occupied_spots_all(self) -> int:
        """Get occupied spots on this floor"""
        return self.get_total_spots_all() - self.get_available_spots_all() 