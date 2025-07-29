"""
Factory Pattern Implementation in Python

The Factory pattern creates objects without specifying the exact class to create.
It provides a common interface for creating objects in a superclass,
but allows subclasses to alter the type of objects that will be created.

This module demonstrates different approaches to implementing the Factory pattern:
1. Simple Factory
2. Factory Method
3. Abstract Factory
4. Function-based Factory
5. Class-based Factory with Registration
"""

from abc import ABC, abstractmethod
from typing import Dict, Type, Optional, Any
from enum import Enum


# 1. Simple Factory Pattern
class Vehicle(ABC):
    """Abstract base class for vehicles"""
    
    @abstractmethod
    def start(self) -> None:
        pass
    
    @abstractmethod
    def stop(self) -> None:
        pass
    
    @abstractmethod
    def get_type(self) -> str:
        pass


class Car(Vehicle):
    """Concrete Car implementation"""
    
    def start(self) -> None:
        print("Car engine started")
    
    def stop(self) -> None:
        print("Car engine stopped")
    
    def get_type(self) -> str:
        return "Car"


class Motorcycle(Vehicle):
    """Concrete Motorcycle implementation"""
    
    def start(self) -> None:
        print("Motorcycle engine started")
    
    def stop(self) -> None:
        print("Motorcycle engine stopped")
    
    def get_type(self) -> str:
        return "Motorcycle"


class Truck(Vehicle):
    """Concrete Truck implementation"""
    
    def start(self) -> None:
        print("Truck engine started")
    
    def stop(self) -> None:
        print("Truck engine stopped")
    
    def get_type(self) -> str:
        return "Truck"


class VehicleType(Enum):
    """Enum for vehicle types"""
    CAR = "car"
    MOTORCYCLE = "motorcycle"
    TRUCK = "truck"


class VehicleFactory:
    """Simple Factory for creating vehicles"""
    
    @staticmethod
    def create_vehicle(vehicle_type: str) -> Vehicle:
        """Create a vehicle based on type string"""
        vehicle_type = vehicle_type.lower()
        
        if vehicle_type == "car":
            return Car()
        elif vehicle_type == "motorcycle":
            return Motorcycle()
        elif vehicle_type == "truck":
            return Truck()
        else:
            raise ValueError(f"Unknown vehicle type: {vehicle_type}")
    
    @staticmethod
    def create_vehicle_enum(vehicle_type: VehicleType) -> Vehicle:
        """Create a vehicle based on enum type"""
        vehicle_map = {
            VehicleType.CAR: Car,
            VehicleType.MOTORCYCLE: Motorcycle,
            VehicleType.TRUCK: Truck
        }
        
        vehicle_class = vehicle_map.get(vehicle_type)
        if vehicle_class:
            return vehicle_class()
        else:
            raise ValueError(f"Unknown vehicle type: {vehicle_type}")


# 2. Factory Method Pattern
class VehicleCreator(ABC):
    """Abstract creator class"""
    
    @abstractmethod
    def create_vehicle(self) -> Vehicle:
        """Factory method to create vehicle"""
        pass
    
    def operate_vehicle(self) -> str:
        """Template method using factory method"""
        vehicle = self.create_vehicle()
        return f"Operating {vehicle.get_type()}"


class CarCreator(VehicleCreator):
    """Concrete creator for cars"""
    
    def create_vehicle(self) -> Vehicle:
        return Car()


class MotorcycleCreator(VehicleCreator):
    """Concrete creator for motorcycles"""
    
    def create_vehicle(self) -> Vehicle:
        return Motorcycle()


class TruckCreator(VehicleCreator):
    """Concrete creator for trucks"""
    
    def create_vehicle(self) -> Vehicle:
        return Truck()


# 3. Abstract Factory Pattern
class Engine(ABC):
    """Abstract engine class"""
    
    @abstractmethod
    def start(self) -> str:
        pass


class Transmission(ABC):
    """Abstract transmission class"""
    
    @abstractmethod
    def shift(self) -> str:
        pass


class Wheels(ABC):
    """Abstract wheels class"""
    
    @abstractmethod
    def rotate(self) -> str:
        pass


# Concrete products for Car family
class CarEngine(Engine):
    def start(self) -> str:
        return "Car engine started"


class CarTransmission(Transmission):
    def shift(self) -> str:
        return "Car transmission shifted"


class CarWheels(Wheels):
    def rotate(self) -> str:
        return "Car wheels rotating"


# Concrete products for Motorcycle family
class MotorcycleEngine(Engine):
    def start(self) -> str:
        return "Motorcycle engine started"


class MotorcycleTransmission(Transmission):
    def shift(self) -> str:
        return "Motorcycle transmission shifted"


class MotorcycleWheels(Wheels):
    def rotate(self) -> str:
        return "Motorcycle wheels rotating"


# Concrete products for Truck family
class TruckEngine(Engine):
    def start(self) -> str:
        return "Truck engine started"


class TruckTransmission(Transmission):
    def shift(self) -> str:
        return "Truck transmission shifted"


class TruckWheels(Wheels):
    def rotate(self) -> str:
        return "Truck wheels rotating"


class VehicleComponentFactory(ABC):
    """Abstract factory for vehicle components"""
    
    @abstractmethod
    def create_engine(self) -> Engine:
        pass
    
    @abstractmethod
    def create_transmission(self) -> Transmission:
        pass
    
    @abstractmethod
    def create_wheels(self) -> Wheels:
        pass


class CarComponentFactory(VehicleComponentFactory):
    """Concrete factory for car components"""
    
    def create_engine(self) -> Engine:
        return CarEngine()
    
    def create_transmission(self) -> Transmission:
        return CarTransmission()
    
    def create_wheels(self) -> Wheels:
        return CarWheels()


class MotorcycleComponentFactory(VehicleComponentFactory):
    """Concrete factory for motorcycle components"""
    
    def create_engine(self) -> Engine:
        return MotorcycleEngine()
    
    def create_transmission(self) -> Transmission:
        return MotorcycleTransmission()
    
    def create_wheels(self) -> Wheels:
        return MotorcycleWheels()


class TruckComponentFactory(VehicleComponentFactory):
    """Concrete factory for truck components"""
    
    def create_engine(self) -> Engine:
        return TruckEngine()
    
    def create_transmission(self) -> Transmission:
        return TruckTransmission()
    
    def create_wheels(self) -> Wheels:
        return TruckWheels()


class VehicleAssembler:
    """Class that uses abstract factory to assemble vehicles"""
    
    def __init__(self, factory: VehicleComponentFactory):
        self.factory = factory
    
    def assemble_vehicle(self) -> Dict[str, str]:
        """Assemble vehicle using factory"""
        engine = self.factory.create_engine()
        transmission = self.factory.create_transmission()
        wheels = self.factory.create_wheels()
        
        return {
            'engine': engine.start(),
            'transmission': transmission.shift(),
            'wheels': wheels.rotate()
        }


# 4. Function-based Factory
def create_vehicle_function(vehicle_type: str, **kwargs) -> Vehicle:
    """Function-based factory for creating vehicles"""
    
    vehicle_registry = {
        'car': Car,
        'motorcycle': Motorcycle,
        'truck': Truck
    }
    
    vehicle_class = vehicle_registry.get(vehicle_type.lower())
    if not vehicle_class:
        raise ValueError(f"Unknown vehicle type: {vehicle_type}")
    
    return vehicle_class(**kwargs)


# 5. Registration-based Factory
class RegisteredVehicleFactory:
    """Factory that allows registration of vehicle types"""
    
    _vehicle_registry: Dict[str, Type[Vehicle]] = {}
    
    @classmethod
    def register(cls, vehicle_type: str, vehicle_class: Type[Vehicle]) -> None:
        """Register a vehicle type with its class"""
        cls._vehicle_registry[vehicle_type.lower()] = vehicle_class
    
    @classmethod
    def unregister(cls, vehicle_type: str) -> None:
        """Unregister a vehicle type"""
        cls._vehicle_registry.pop(vehicle_type.lower(), None)
    
    @classmethod
    def create_vehicle(cls, vehicle_type: str) -> Vehicle:
        """Create a vehicle of registered type"""
        vehicle_class = cls._vehicle_registry.get(vehicle_type.lower())
        if not vehicle_class:
            raise ValueError(f"Unknown vehicle type: {vehicle_type}")
        return vehicle_class()
    
    @classmethod
    def get_registered_types(cls) -> list:
        """Get all registered vehicle types"""
        return list(cls._vehicle_registry.keys())


# Register default vehicle types
RegisteredVehicleFactory.register('car', Car)
RegisteredVehicleFactory.register('motorcycle', Motorcycle)
RegisteredVehicleFactory.register('truck', Truck)


# 6. Decorator-based Factory Registration
def vehicle_factory(vehicle_type: str):
    """Decorator to register vehicle types"""
    def decorator(cls):
        RegisteredVehicleFactory.register(vehicle_type, cls)
        return cls
    return decorator


@vehicle_factory('sports_car')
class SportsCar(Vehicle):
    """Sports car registered via decorator"""
    
    def start(self) -> None:
        print("Sports car engine roared to life")
    
    def stop(self) -> None:
        print("Sports car engine stopped")
    
    def get_type(self) -> str:
        return "Sports Car"


# 7. Configuration-based Factory
class ConfigurableVehicleFactory:
    """Factory that creates vehicles based on configuration"""
    
    def __init__(self, config: Dict[str, Any]):
        self.config = config
    
    def create_vehicle(self, vehicle_type: str) -> Vehicle:
        """Create vehicle with configuration"""
        vehicle_config = self.config.get(vehicle_type, {})
        
        # Create base vehicle
        base_factory = VehicleFactory()
        vehicle = base_factory.create_vehicle(vehicle_type)
        
        # Apply configuration
        for key, value in vehicle_config.items():
            if hasattr(vehicle, key):
                setattr(vehicle, key, value)
        
        return vehicle


def test_factory_patterns():
    """Test function to demonstrate different factory patterns"""
    
    print("=== Factory Pattern Demo ===")
    
    # Test Simple Factory
    print("\n--- Simple Factory ---")
    car = VehicleFactory.create_vehicle("car")
    motorcycle = VehicleFactory.create_vehicle("motorcycle")
    truck = VehicleFactory.create_vehicle("truck")
    
    for vehicle in [car, motorcycle, truck]:
        print(f"Testing {vehicle.get_type()}:")
        vehicle.start()
        vehicle.stop()
        print()
    
    # Test Enum Factory
    print("\n--- Enum Factory ---")
    enum_car = VehicleFactory.create_vehicle_enum(VehicleType.CAR)
    enum_car.start()
    enum_car.stop()
    
    # Test Factory Method
    print("\n--- Factory Method ---")
    creators = [CarCreator(), MotorcycleCreator(), TruckCreator()]
    
    for creator in creators:
        print(creator.operate_vehicle())
    
    # Test Abstract Factory
    print("\n--- Abstract Factory ---")
    factories = [
        CarComponentFactory(),
        MotorcycleComponentFactory(),
        TruckComponentFactory()
    ]
    
    for factory in factories:
        assembler = VehicleAssembler(factory)
        components = assembler.assemble_vehicle()
        print(f"Assembled vehicle with:")
        for component, action in components.items():
            print(f"  {component}: {action}")
        print()
    
    # Test Function Factory
    print("\n--- Function Factory ---")
    func_car = create_vehicle_function("car")
    func_car.start()
    func_car.stop()
    
    # Test Registration Factory
    print("\n--- Registration Factory ---")
    print(f"Registered types: {RegisteredVehicleFactory.get_registered_types()}")
    
    reg_car = RegisteredVehicleFactory.create_vehicle("car")
    sports_car = RegisteredVehicleFactory.create_vehicle("sports_car")
    
    for vehicle in [reg_car, sports_car]:
        print(f"Testing {vehicle.get_type()}:")
        vehicle.start()
        vehicle.stop()
        print()
    
    # Test Configurable Factory
    print("\n--- Configurable Factory ---")
    config = {
        'car': {'color': 'red', 'model': 'sedan'},
        'truck': {'color': 'blue', 'model': 'pickup'}
    }
    
    configurable_factory = ConfigurableVehicleFactory(config)
    configured_car = configurable_factory.create_vehicle("car")
    
    print(f"Configured car: {configured_car.get_type()}")
    if hasattr(configured_car, 'color'):
        print(f"Color: {configured_car.color}")
    if hasattr(configured_car, 'model'):
        print(f"Model: {configured_car.model}")


if __name__ == "__main__":
    test_factory_patterns() 