from typing import List, Dict, Optional
from table import Table
from menu import Menu
from customer import Customer

class Restaurant:
    """Restaurant class managing tables, menu, and operations"""
    
    def __init__(self, name: str, address: str):
        self.name = name
        self.address = address
        self.tables: Dict[int, Table] = {}
        self.menu = Menu()
        self.reservations: List = []
    
    def add_table(self, table: Table):
        """Add table to restaurant"""
        self.tables[table.table_number] = table
    
    def get_table(self, table_number: int) -> Optional[Table]:
        """Get table by number"""
        return self.tables.get(table_number)
    
    def get_all_tables(self) -> List[Table]:
        """Get all tables"""
        return list(self.tables.values())
    
    def get_available_tables(self, party_size: int) -> List[Table]:
        """Get available tables for party size"""
        available_tables = []
        for table in self.tables.values():
            if table.is_available() and table.capacity >= party_size:
                available_tables.append(table)
        return available_tables
    
    def reserve_table(self, table_number: int, customer: Customer) -> bool:
        """Reserve a table"""
        table = self.get_table(table_number)
        if table and table.is_available():
            table.reserve_table(customer)
            return True
        return False
    
    def get_total_tables(self) -> int:
        """Get total number of tables"""
        return len(self.tables)
    
    def get_available_tables_count(self) -> int:
        """Get count of available tables"""
        return sum(1 for table in self.tables.values() if table.is_available())
    
    def get_occupied_tables_count(self) -> int:
        """Get count of occupied tables"""
        return self.get_total_tables() - self.get_available_tables_count() 