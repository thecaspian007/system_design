"""
Abstract Factory Pattern Implementation

The Abstract Factory pattern provides an interface for creating families of related objects
without specifying their concrete classes.
"""

from abc import ABC, abstractmethod
import platform


# Abstract Products
class Button(ABC):
    @abstractmethod
    def paint(self):
        pass


class Checkbox(ABC):
    @abstractmethod
    def paint(self):
        pass


class TextBox(ABC):
    @abstractmethod
    def paint(self):
        pass


# Concrete Products - Windows Family
class WindowsButton(Button):
    def paint(self):
        print("Rendering Windows Button")


class WindowsCheckbox(Checkbox):
    def paint(self):
        print("Rendering Windows Checkbox")


class WindowsTextBox(TextBox):
    def paint(self):
        print("Rendering Windows TextBox")


# Concrete Products - MacOS Family
class MacOSButton(Button):
    def paint(self):
        print("Rendering MacOS Button")


class MacOSCheckbox(Checkbox):
    def paint(self):
        print("Rendering MacOS Checkbox")


class MacOSTextBox(TextBox):
    def paint(self):
        print("Rendering MacOS TextBox")


# Concrete Products - Linux Family
class LinuxButton(Button):
    def paint(self):
        print("Rendering Linux Button")


class LinuxCheckbox(Checkbox):
    def paint(self):
        print("Rendering Linux Checkbox")


class LinuxTextBox(TextBox):
    def paint(self):
        print("Rendering Linux TextBox")


# Abstract Factory
class GUIFactory(ABC):
    @abstractmethod
    def create_button(self) -> Button:
        pass
    
    @abstractmethod
    def create_checkbox(self) -> Checkbox:
        pass
    
    @abstractmethod
    def create_textbox(self) -> TextBox:
        pass


# Concrete Factories
class WindowsFactory(GUIFactory):
    def create_button(self) -> Button:
        return WindowsButton()
    
    def create_checkbox(self) -> Checkbox:
        return WindowsCheckbox()
    
    def create_textbox(self) -> TextBox:
        return WindowsTextBox()


class MacOSFactory(GUIFactory):
    def create_button(self) -> Button:
        return MacOSButton()
    
    def create_checkbox(self) -> Checkbox:
        return MacOSCheckbox()
    
    def create_textbox(self) -> TextBox:
        return MacOSTextBox()


class LinuxFactory(GUIFactory):
    def create_button(self) -> Button:
        return LinuxButton()
    
    def create_checkbox(self) -> Checkbox:
        return LinuxCheckbox()
    
    def create_textbox(self) -> TextBox:
        return LinuxTextBox()


# Client Application
class Application:
    def __init__(self, factory: GUIFactory):
        self.button = factory.create_button()
        self.checkbox = factory.create_checkbox()
        self.textbox = factory.create_textbox()
    
    def paint(self):
        self.button.paint()
        self.checkbox.paint()
        self.textbox.paint()


# Factory Provider
class FactoryProvider:
    @staticmethod
    def get_factory(os_type: str) -> GUIFactory:
        factories = {
            "windows": WindowsFactory(),
            "macos": MacOSFactory(),
            "linux": LinuxFactory()
        }
        
        factory = factories.get(os_type.lower())
        if not factory:
            raise ValueError(f"Unknown OS type: {os_type}")
        
        return factory


# Additional example - Vehicle Factory
class Engine(ABC):
    @abstractmethod
    def start(self):
        pass


class Transmission(ABC):
    @abstractmethod
    def shift(self):
        pass


class Wheel(ABC):
    @abstractmethod
    def rotate(self):
        pass


# Car Components
class CarEngine(Engine):
    def start(self):
        print("Car engine started")


class CarTransmission(Transmission):
    def shift(self):
        print("Car transmission shifted")


class CarWheel(Wheel):
    def rotate(self):
        print("Car wheel rotating")


# Truck Components
class TruckEngine(Engine):
    def start(self):
        print("Truck engine started")


class TruckTransmission(Transmission):
    def shift(self):
        print("Truck transmission shifted")


class TruckWheel(Wheel):
    def rotate(self):
        print("Truck wheel rotating")


# Vehicle Factory
class VehicleFactory(ABC):
    @abstractmethod
    def create_engine(self) -> Engine:
        pass
    
    @abstractmethod
    def create_transmission(self) -> Transmission:
        pass
    
    @abstractmethod
    def create_wheel(self) -> Wheel:
        pass


class CarFactory(VehicleFactory):
    def create_engine(self) -> Engine:
        return CarEngine()
    
    def create_transmission(self) -> Transmission:
        return CarTransmission()
    
    def create_wheel(self) -> Wheel:
        return CarWheel()


class TruckFactory(VehicleFactory):
    def create_engine(self) -> Engine:
        return TruckEngine()
    
    def create_transmission(self) -> Transmission:
        return TruckTransmission()
    
    def create_wheel(self) -> Wheel:
        return TruckWheel()


class Vehicle:
    def __init__(self, factory: VehicleFactory):
        self.engine = factory.create_engine()
        self.transmission = factory.create_transmission()
        self.wheel = factory.create_wheel()
    
    def start(self):
        self.engine.start()
        self.transmission.shift()
        self.wheel.rotate()


def main():
    print("=== Abstract Factory Pattern Demo ===")
    
    # Simulate different operating systems
    operating_systems = ["windows", "macos", "linux"]
    
    for os in operating_systems:
        print(f"\n--- Creating GUI for {os.upper()} ---")
        
        factory = FactoryProvider.get_factory(os)
        app = Application(factory)
        app.paint()
    
    # Alternative approach - detecting OS at runtime
    print("\n=== Auto-detecting OS ===")
    current_os = platform.system().lower()
    
    if current_os == "windows":
        os_type = "windows"
    elif current_os == "darwin":
        os_type = "macos"
    elif current_os == "linux":
        os_type = "linux"
    else:
        os_type = "linux"  # default
    
    print(f"Detected OS: {os_type}")
    auto_factory = FactoryProvider.get_factory(os_type)
    auto_app = Application(auto_factory)
    auto_app.paint()
    
    # Vehicle Factory Example
    print("\n=== Vehicle Factory Example ===")
    
    print("\n--- Creating Car ---")
    car_factory = CarFactory()
    car = Vehicle(car_factory)
    car.start()
    
    print("\n--- Creating Truck ---")
    truck_factory = TruckFactory()
    truck = Vehicle(truck_factory)
    truck.start()


if __name__ == "__main__":
    main() 