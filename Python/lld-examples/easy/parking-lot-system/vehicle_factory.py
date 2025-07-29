from vehicle import Vehicle, VehicleType, Car, Motorcycle, Truck

class VehicleFactory:
    """Factory class for creating vehicles"""
    
    @staticmethod
    def create_vehicle(vehicle_type: VehicleType, license_plate: str) -> Vehicle:
        """Create vehicle based on type"""
        if vehicle_type == VehicleType.CAR:
            return Car(license_plate)
        elif vehicle_type == VehicleType.MOTORCYCLE:
            return Motorcycle(license_plate)
        elif vehicle_type == VehicleType.TRUCK:
            return Truck(license_plate)
        else:
            raise ValueError(f"Unknown vehicle type: {vehicle_type}") 