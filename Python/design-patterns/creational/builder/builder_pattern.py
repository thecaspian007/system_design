"""
Builder Pattern Implementation

The Builder pattern constructs complex objects step by step.
It allows you to produce different types and representations of an object
using the same construction code.
"""

from abc import ABC, abstractmethod
from typing import Optional


# Product class
class Computer:
    def __init__(self):
        self.cpu: Optional[str] = None
        self.memory: Optional[str] = None
        self.storage: Optional[str] = None
        self.gpu: Optional[str] = None
        self.motherboard: Optional[str] = None
        self.power_supply: Optional[str] = None
        self.has_wifi: bool = False
        self.has_bluetooth: bool = False
    
    def __str__(self):
        return (f"Computer(cpu='{self.cpu}', memory='{self.memory}', "
                f"storage='{self.storage}', gpu='{self.gpu}', "
                f"motherboard='{self.motherboard}', power_supply='{self.power_supply}', "
                f"has_wifi={self.has_wifi}, has_bluetooth={self.has_bluetooth})")


# Builder class
class ComputerBuilder:
    def __init__(self):
        self.computer = Computer()
    
    def set_cpu(self, cpu: str):
        self.computer.cpu = cpu
        return self
    
    def set_memory(self, memory: str):
        self.computer.memory = memory
        return self
    
    def set_storage(self, storage: str):
        self.computer.storage = storage
        return self
    
    def set_gpu(self, gpu: str):
        self.computer.gpu = gpu
        return self
    
    def set_motherboard(self, motherboard: str):
        self.computer.motherboard = motherboard
        return self
    
    def set_power_supply(self, power_supply: str):
        self.computer.power_supply = power_supply
        return self
    
    def set_wifi(self, has_wifi: bool):
        self.computer.has_wifi = has_wifi
        return self
    
    def set_bluetooth(self, has_bluetooth: bool):
        self.computer.has_bluetooth = has_bluetooth
        return self
    
    def build(self) -> Computer:
        return self.computer


# Alternative Builder using interface
class HouseBuilder(ABC):
    @abstractmethod
    def set_foundation(self, foundation: str):
        pass
    
    @abstractmethod
    def set_structure(self, structure: str):
        pass
    
    @abstractmethod
    def set_roof(self, roof: str):
        pass
    
    @abstractmethod
    def set_interior(self, interior: str):
        pass
    
    @abstractmethod
    def build(self):
        pass


class House:
    def __init__(self, foundation: str, structure: str, roof: str, interior: str):
        self.foundation = foundation
        self.structure = structure
        self.roof = roof
        self.interior = interior
    
    def __str__(self):
        return (f"House(foundation='{self.foundation}', structure='{self.structure}', "
                f"roof='{self.roof}', interior='{self.interior}')")


class ConcreteHouseBuilder(HouseBuilder):
    def __init__(self):
        self.foundation: Optional[str] = None
        self.structure: Optional[str] = None
        self.roof: Optional[str] = None
        self.interior: Optional[str] = None
    
    def set_foundation(self, foundation: str):
        self.foundation = foundation
        return self
    
    def set_structure(self, structure: str):
        self.structure = structure
        return self
    
    def set_roof(self, roof: str):
        self.roof = roof
        return self
    
    def set_interior(self, interior: str):
        self.interior = interior
        return self
    
    def build(self) -> House:
        return House(self.foundation, self.structure, self.roof, self.interior)


# Director class
class HouseDirector:
    def __init__(self, builder: HouseBuilder):
        self.builder = builder
    
    def construct_simple_house(self) -> House:
        return (self.builder
                .set_foundation("Concrete Foundation")
                .set_structure("Wood Frame")
                .set_roof("Asphalt Shingles")
                .set_interior("Basic Interior")
                .build())
    
    def construct_luxury_house(self) -> House:
        return (self.builder
                .set_foundation("Reinforced Concrete Foundation")
                .set_structure("Steel Frame")
                .set_roof("Tile Roof")
                .set_interior("Luxury Interior")
                .build())


# Pizza Builder Example
class Pizza:
    def __init__(self):
        self.size: Optional[str] = None
        self.crust: Optional[str] = None
        self.toppings: list = []
        self.cheese: Optional[str] = None
        self.sauce: Optional[str] = None
    
    def __str__(self):
        return (f"Pizza(size='{self.size}', crust='{self.crust}', "
                f"toppings={self.toppings}, cheese='{self.cheese}', sauce='{self.sauce}')")


class PizzaBuilder:
    def __init__(self):
        self.pizza = Pizza()
    
    def set_size(self, size: str):
        self.pizza.size = size
        return self
    
    def set_crust(self, crust: str):
        self.pizza.crust = crust
        return self
    
    def add_topping(self, topping: str):
        self.pizza.toppings.append(topping)
        return self
    
    def set_cheese(self, cheese: str):
        self.pizza.cheese = cheese
        return self
    
    def set_sauce(self, sauce: str):
        self.pizza.sauce = sauce
        return self
    
    def build(self) -> Pizza:
        return self.pizza


# SQL Query Builder Example
class SQLQuery:
    def __init__(self):
        self.query_parts = []
    
    def __str__(self):
        return " ".join(self.query_parts)


class SQLQueryBuilder:
    def __init__(self):
        self.query = SQLQuery()
    
    def select(self, columns: str):
        self.query.query_parts.append(f"SELECT {columns}")
        return self
    
    def from_table(self, table: str):
        self.query.query_parts.append(f"FROM {table}")
        return self
    
    def where(self, condition: str):
        self.query.query_parts.append(f"WHERE {condition}")
        return self
    
    def order_by(self, column: str, direction: str = "ASC"):
        self.query.query_parts.append(f"ORDER BY {column} {direction}")
        return self
    
    def limit(self, count: int):
        self.query.query_parts.append(f"LIMIT {count}")
        return self
    
    def build(self) -> SQLQuery:
        return self.query


def main():
    print("=== Builder Pattern Demo ===")
    
    # Computer Builder Example
    print("\n--- Computer Builder Example ---")
    
    gaming_computer = (ComputerBuilder()
                       .set_cpu("Intel i9")
                       .set_memory("32GB DDR4")
                       .set_storage("1TB SSD")
                       .set_gpu("RTX 4090")
                       .set_motherboard("ASUS ROG")
                       .set_power_supply("850W")
                       .set_wifi(True)
                       .set_bluetooth(True)
                       .build())
    
    office_computer = (ComputerBuilder()
                       .set_cpu("Intel i5")
                       .set_memory("16GB DDR4")
                       .set_storage("512GB SSD")
                       .set_motherboard("Basic Motherboard")
                       .set_power_supply("500W")
                       .set_wifi(True)
                       .build())
    
    print(f"Gaming Computer: {gaming_computer}")
    print(f"Office Computer: {office_computer}")
    
    # House Builder with Director
    print("\n--- House Builder with Director ---")
    
    house_builder = ConcreteHouseBuilder()
    director = HouseDirector(house_builder)
    
    simple_house = director.construct_simple_house()
    
    # Need a fresh builder for the luxury house
    luxury_builder = ConcreteHouseBuilder()
    luxury_director = HouseDirector(luxury_builder)
    luxury_house = luxury_director.construct_luxury_house()
    
    print(f"Simple House: {simple_house}")
    print(f"Luxury House: {luxury_house}")
    
    # Pizza Builder Example
    print("\n--- Pizza Builder Example ---")
    
    margherita = (PizzaBuilder()
                  .set_size("Medium")
                  .set_crust("Thin")
                  .add_topping("Basil")
                  .add_topping("Tomato")
                  .set_cheese("Mozzarella")
                  .set_sauce("Tomato Sauce")
                  .build())
    
    pepperoni = (PizzaBuilder()
                 .set_size("Large")
                 .set_crust("Thick")
                 .add_topping("Pepperoni")
                 .add_topping("Mushrooms")
                 .add_topping("Olives")
                 .set_cheese("Mozzarella")
                 .set_sauce("Tomato Sauce")
                 .build())
    
    print(f"Margherita Pizza: {margherita}")
    print(f"Pepperoni Pizza: {pepperoni}")
    
    # SQL Query Builder Example
    print("\n--- SQL Query Builder Example ---")
    
    query1 = (SQLQueryBuilder()
              .select("*")
              .from_table("users")
              .where("age > 18")
              .order_by("name")
              .limit(10)
              .build())
    
    query2 = (SQLQueryBuilder()
              .select("name, email")
              .from_table("customers")
              .where("country = 'USA'")
              .order_by("created_at", "DESC")
              .build())
    
    print(f"Query 1: {query1}")
    print(f"Query 2: {query2}")


if __name__ == "__main__":
    main() 