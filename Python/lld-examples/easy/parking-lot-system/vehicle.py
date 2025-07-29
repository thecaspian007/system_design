from enum import Enum
from abc import ABC, abstractmethod

class VehicleType(Enum):
    """Enum representing different types of vehicles"""
    CAR = ("Car", 2.0)
    MOTORCYCLE = ("Motorcycle", 1.0)
    TRUCK = ("Truck", 4.0)
    
    def __init__(self, display_name: str, hourly_rate: float):
        self.display_name = display_name
        self.hourly_rate = hourly_rate
    
    def __str__(self):
        return self.display_name

class Vehicle(ABC):
    """Abstract Vehicle class"""
    
    def __init__(self, license_plate: str, vehicle_type: VehicleType):
        self.license_plate = license_plate
        self.type = vehicle_type
    
    def __str__(self):
        return f"{self.type.display_name} ({self.license_plate})"

class Car(Vehicle):
    """Car implementation"""
    
    def __init__(self, license_plate: str):
        super().__init__(license_plate, VehicleType.CAR)

class Motorcycle(Vehicle):
    """Motorcycle implementation"""
    
    def __init__(self, license_plate: str):
        super().__init__(license_plate, VehicleType.MOTORCYCLE)

class Truck(Vehicle):
    """Truck implementation"""
    
    def __init__(self, license_plate: str):
        super().__init__(license_plate, VehicleType.TRUCK) 