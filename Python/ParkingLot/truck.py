from vehicle import Vehicle
from vehicle_type import VehicleType

class Truck(Vehicle):
    def __init__(self, license_plate: str):
        super().__init__(license_plate, VehicleType.TRUCK)