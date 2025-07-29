from typing import Optional
from enum import Enum

class TableStatus(Enum):
    """Table status enum"""
    AVAILABLE = "Available"
    OCCUPIED = "Occupied"
    RESERVED = "Reserved"
    OUT_OF_SERVICE = "Out of Service"

class Table:
    """Table class representing a restaurant table"""
    
    def __init__(self, table_number: int, capacity: int):
        self.table_number = table_number
        self.capacity = capacity
        self.status = TableStatus.AVAILABLE
        self.current_customer = None
        self.current_order = None
        self.reservation_time = None
    
    def is_available(self) -> bool:
        """Check if table is available"""
        return self.status == TableStatus.AVAILABLE
    
    def reserve_table(self, customer):
        """Reserve table for customer"""
        if self.is_available():
            self.status = TableStatus.OCCUPIED
            self.current_customer = customer
            return True
        return False
    
    def clear_table(self):
        """Clear table after customer leaves"""
        self.status = TableStatus.AVAILABLE
        self.current_customer = None
        self.current_order = None
        self.reservation_time = None
    
    def set_out_of_service(self):
        """Set table out of service"""
        self.status = TableStatus.OUT_OF_SERVICE
    
    def set_available(self):
        """Set table available"""
        self.status = TableStatus.AVAILABLE
    
    def __str__(self):
        return f"Table {self.table_number} - {self.capacity} seats - {self.status.value}" 