from abc import ABC
from vehicle_type import VehicleType

class Vehicle(ABC):
    def __init__(self, license_plate: str, vehicle_type: VehicleType):
        self.license_plate = license_plate
        self.vehicle_type = VehicleType

    def get_type(self) -> VehicleType: 
        return self.type
