import sys
import os

# Add the current directory to the Python path
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

from parking_lot import ParkingLot
from parking_floor import ParkingFloor
from vehicle import VehicleType
from vehicle_factory import VehicleFactory

class ParkingLotSystem:
    """
    Parking Lot System - Main Application
    
    This system demonstrates:
    - Vehicle parking and exit management
    - Different vehicle types (Car, Motorcycle, Truck)
    - Pricing strategies
    - Parking spot allocation
    - Payment processing
    
    @author System Design Repository
    """
    
    def __init__(self):
        # Initialize parking lot with 3 floors
        self.parking_lot = ParkingLot("Downtown Parking", 3)
        
        # Initialize floors with parking spots
        self._initialize_parking_lot()
    
    def _initialize_parking_lot(self):
        """Initialize parking lot with floors and spots"""
        # Floor 1: Mixed spots
        self.parking_lot.add_floor(ParkingFloor(1, 20, 10, 5))  # 20 car, 10 motorcycle, 5 truck spots
        
        # Floor 2: More cars and motorcycles
        self.parking_lot.add_floor(ParkingFloor(2, 25, 15, 3))
        
        # Floor 3: Mostly cars
        self.parking_lot.add_floor(ParkingFloor(3, 30, 8, 2))
        
        print("Parking lot initialized with 3 floors")
        print(f"Total spots: {self.parking_lot.get_total_spots()}")
    
    def start(self):
        """Start the parking lot system"""
        print(f"=== Welcome to {self.parking_lot.name} ===")
        
        while True:
            try:
                self._show_main_menu()
                choice = self._get_int_input()
                
                if choice == 1:
                    self._handle_vehicle_entry()
                elif choice == 2:
                    self._handle_vehicle_exit()
                elif choice == 3:
                    self._show_parking_status()
                elif choice == 4:
                    self._search_vehicle()
                elif choice == 5:
                    print("Thank you for using our parking system!")
                    break
                else:
                    print("Invalid choice. Please try again.")
                    
            except Exception as e:
                print(f"Error: {e}")
    
    def _show_main_menu(self):
        """Show main menu"""
        print("\n--- Parking Lot Management ---")
        print("1. Park Vehicle")
        print("2. Exit Vehicle")
        print("3. Show Parking Status")
        print("4. Search Vehicle")
        print("5. Exit System")
        print("Choose an option: ", end="")
    
    def _handle_vehicle_entry(self):
        """Handle vehicle entry"""
        print("\n--- Vehicle Entry ---")
        license_plate = input("Enter vehicle license plate: ").upper()
        
        print("Select vehicle type:")
        print("1. Car")
        print("2. Motorcycle")
        print("3. Truck")
        type_choice = self._get_int_input("Choice: ")
        
        vehicle_type = self._get_vehicle_type(type_choice)
        
        if vehicle_type is None:
            print("Invalid vehicle type selected.")
            return
        
        vehicle = VehicleFactory.create_vehicle(vehicle_type, license_plate)
        ticket = self.parking_lot.park_vehicle(vehicle)
        
        if ticket:
            print("Vehicle parked successfully!")
            print(f"Ticket ID: {ticket.ticket_id}")
            print(f"Parking Spot: Floor {ticket.parking_spot.floor_number}, Spot {ticket.parking_spot.spot_number}")
            print(f"Entry Time: {ticket.get_formatted_entry_time()}")
            print(f"Hourly Rate: ${ticket.parking_spot.hourly_rate}")
        else:
            print(f"Sorry, no available parking spot for {vehicle_type.value}")
    
    def _handle_vehicle_exit(self):
        """Handle vehicle exit"""
        print("\n--- Vehicle Exit ---")
        identifier = input("Enter ticket ID or license plate: ").upper()
        
        ticket = self.parking_lot.find_ticket(identifier)
        
        if not ticket:
            print("Vehicle not found in the parking lot.")
            return
        
        if ticket.is_paid:
            print("Vehicle has already been processed.")
            return
        
        fee = self.parking_lot.calculate_parking_fee(ticket)
        print(f"Parking fee: ${fee:.2f}")
        
        if fee > 0:
            print("Please pay the parking fee.")
            payment = self._get_double_input("Enter payment amount: $")
            
            if payment >= fee:
                if self.parking_lot.process_payment(ticket, payment):
                    print("Payment processed successfully!")
                    print(f"Vehicle exited. Change: ${payment - fee:.2f}")
                else:
                    print("Payment processing failed.")
            else:
                print("Insufficient payment.")
        else:
            # Free parking (first hour)
            if self.parking_lot.process_payment(ticket, 0):
                print("Free parking. Vehicle exited successfully!")
            else:
                print("Exit processing failed.")
    
    def _show_parking_status(self):
        """Show parking status"""
        print("\n--- Parking Status ---")
        print(f"Parking Lot: {self.parking_lot.name}")
        print(f"Total Floors: {len(self.parking_lot.floors)}")
        print(f"Total Spots: {self.parking_lot.get_total_spots()}")
        print(f"Available Spots: {self.parking_lot.get_available_spots()}")
        print(f"Occupied Spots: {self.parking_lot.get_occupied_spots()}")
        print()
        
        for floor in self.parking_lot.floors:
            print(f"Floor {floor.floor_number}:")
            print(f"  Cars: {floor.get_available_spots(VehicleType.CAR)}/{floor.get_total_spots(VehicleType.CAR)}")
            print(f"  Motorcycles: {floor.get_available_spots(VehicleType.MOTORCYCLE)}/{floor.get_total_spots(VehicleType.MOTORCYCLE)}")
            print(f"  Trucks: {floor.get_available_spots(VehicleType.TRUCK)}/{floor.get_total_spots(VehicleType.TRUCK)}")
    
    def _search_vehicle(self):
        """Search for a vehicle"""
        print("\n--- Search Vehicle ---")
        license_plate = input("Enter license plate: ").upper()
        
        ticket = self.parking_lot.find_ticket_by_license_plate(license_plate)
        
        if not ticket:
            print("Vehicle not found in the parking lot.")
            return
        
        print("Vehicle found!")
        print(f"License Plate: {ticket.vehicle.license_plate}")
        print(f"Vehicle Type: {ticket.vehicle.type.value}")
        print(f"Parking Spot: Floor {ticket.parking_spot.floor_number}, Spot {ticket.parking_spot.spot_number}")
        print(f"Entry Time: {ticket.get_formatted_entry_time()}")
        print(f"Duration: {ticket.get_parking_duration_in_hours()} hours")
        print(f"Current Fee: ${self.parking_lot.calculate_parking_fee(ticket):.2f}")
    
    def _get_vehicle_type(self, choice: int) -> VehicleType:
        """Get vehicle type from choice"""
        if choice == 1:
            return VehicleType.CAR
        elif choice == 2:
            return VehicleType.MOTORCYCLE
        elif choice == 3:
            return VehicleType.TRUCK
        else:
            return None
    
    def _get_int_input(self, prompt: str = "") -> int:
        """Get integer input with validation"""
        while True:
            try:
                return int(input(prompt))
            except ValueError:
                print("Please enter a valid number: ", end="")
    
    def _get_double_input(self, prompt: str = "") -> float:
        """Get double input with validation"""
        while True:
            try:
                value = float(input(prompt))
                if value < 0:
                    print("Please enter a positive amount: ", end="")
                    continue
                return value
            except ValueError:
                print("Please enter a valid amount: ", end="")

if __name__ == "__main__":
    parking_system = ParkingLotSystem()
    parking_system.start() 