"""
Decorator Pattern Implementation

The Decorator pattern allows behavior to be added to objects dynamically
without altering their structure. It acts as a wrapper to existing class.
"""

from abc import ABC, abstractmethod


# Base component interface
class Coffee(ABC):
    @abstractmethod
    def get_description(self) -> str:
        pass
    
    @abstractmethod
    def get_cost(self) -> float:
        pass


# Concrete component
class SimpleCoffee(Coffee):
    def get_description(self) -> str:
        return "Simple Coffee"
    
    def get_cost(self) -> float:
        return 2.00


# Base Decorator
class CoffeeDecorator(Coffee):
    def __init__(self, coffee: Coffee):
        self.coffee = coffee
    
    def get_description(self) -> str:
        return self.coffee.get_description()
    
    def get_cost(self) -> float:
        return self.coffee.get_cost()


# Concrete Decorators
class MilkDecorator(CoffeeDecorator):
    def __init__(self, coffee: Coffee):
        super().__init__(coffee)
    
    def get_description(self) -> str:
        return self.coffee.get_description() + ", Milk"
    
    def get_cost(self) -> float:
        return self.coffee.get_cost() + 0.50


class SugarDecorator(CoffeeDecorator):
    def __init__(self, coffee: Coffee):
        super().__init__(coffee)
    
    def get_description(self) -> str:
        return self.coffee.get_description() + ", Sugar"
    
    def get_cost(self) -> float:
        return self.coffee.get_cost() + 0.25


class ChocolateDecorator(CoffeeDecorator):
    def __init__(self, coffee: Coffee):
        super().__init__(coffee)
    
    def get_description(self) -> str:
        return self.coffee.get_description() + ", Chocolate"
    
    def get_cost(self) -> float:
        return self.coffee.get_cost() + 0.75


class WhippedCreamDecorator(CoffeeDecorator):
    def __init__(self, coffee: Coffee):
        super().__init__(coffee)
    
    def get_description(self) -> str:
        return self.coffee.get_description() + ", Whipped Cream"
    
    def get_cost(self) -> float:
        return self.coffee.get_cost() + 1.00


# Another example - Text Formatting
class Text(ABC):
    @abstractmethod
    def format(self) -> str:
        pass


class PlainText(Text):
    def __init__(self, text: str):
        self.text = text
    
    def format(self) -> str:
        return self.text


class TextDecorator(Text):
    def __init__(self, text: Text):
        self.text = text
    
    def format(self) -> str:
        return self.text.format()


class BoldDecorator(TextDecorator):
    def __init__(self, text: Text):
        super().__init__(text)
    
    def format(self) -> str:
        return f"<b>{self.text.format()}</b>"


class ItalicDecorator(TextDecorator):
    def __init__(self, text: Text):
        super().__init__(text)
    
    def format(self) -> str:
        return f"<i>{self.text.format()}</i>"


class UnderlineDecorator(TextDecorator):
    def __init__(self, text: Text):
        super().__init__(text)
    
    def format(self) -> str:
        return f"<u>{self.text.format()}</u>"


# File encryption example
class DataSource(ABC):
    @abstractmethod
    def write_data(self, data: str):
        pass
    
    @abstractmethod
    def read_data(self) -> str:
        pass


class FileDataSource(DataSource):
    def __init__(self, filename: str):
        self.filename = filename
        self.data = ""
    
    def write_data(self, data: str):
        self.data = data
        print(f"Writing data to file: {self.filename}")
    
    def read_data(self) -> str:
        print(f"Reading data from file: {self.filename}")
        return self.data


class DataSourceDecorator(DataSource):
    def __init__(self, source: DataSource):
        self.wrapper = source
    
    def write_data(self, data: str):
        self.wrapper.write_data(data)
    
    def read_data(self) -> str:
        return self.wrapper.read_data()


class EncryptionDecorator(DataSourceDecorator):
    def __init__(self, source: DataSource):
        super().__init__(source)
    
    def write_data(self, data: str):
        print("Encrypting data...")
        super().write_data(self._encode(data))
    
    def read_data(self) -> str:
        print("Decrypting data...")
        return self._decode(super().read_data())
    
    def _encode(self, data: str) -> str:
        return f"encrypted({data})"
    
    def _decode(self, data: str) -> str:
        return data.replace("encrypted(", "").replace(")", "")


class CompressionDecorator(DataSourceDecorator):
    def __init__(self, source: DataSource):
        super().__init__(source)
    
    def write_data(self, data: str):
        print("Compressing data...")
        super().write_data(self._compress(data))
    
    def read_data(self) -> str:
        print("Decompressing data...")
        return self._decompress(super().read_data())
    
    def _compress(self, data: str) -> str:
        return f"compressed({data})"
    
    def _decompress(self, data: str) -> str:
        return data.replace("compressed(", "").replace(")", "")


def main():
    print("=== Decorator Pattern Demo ===")
    
    # Coffee Example
    print("\n--- Coffee Shop Example ---")
    
    coffee = SimpleCoffee()
    print(f"{coffee.get_description()} ${coffee.get_cost()}")
    
    coffee = MilkDecorator(coffee)
    print(f"{coffee.get_description()} ${coffee.get_cost()}")
    
    coffee = SugarDecorator(coffee)
    print(f"{coffee.get_description()} ${coffee.get_cost()}")
    
    coffee = ChocolateDecorator(coffee)
    print(f"{coffee.get_description()} ${coffee.get_cost()}")
    
    coffee = WhippedCreamDecorator(coffee)
    print(f"{coffee.get_description()} ${coffee.get_cost()}")
    
    # Text Formatting Example
    print("\n--- Text Formatting Example ---")
    
    text = PlainText("Hello World")
    print(f"Plain: {text.format()}")
    
    text = BoldDecorator(text)
    print(f"Bold: {text.format()}")
    
    text = ItalicDecorator(text)
    print(f"Bold + Italic: {text.format()}")
    
    text = UnderlineDecorator(text)
    print(f"Bold + Italic + Underline: {text.format()}")
    
    # File Encryption Example
    print("\n--- File Encryption Example ---")
    
    source = FileDataSource("data.txt")
    source.write_data("Hello World")
    print(f"Read: {source.read_data()}")
    
    print("\n--- With Encryption ---")
    source = EncryptionDecorator(FileDataSource("encrypted.txt"))
    source.write_data("Hello World")
    print(f"Read: {source.read_data()}")
    
    print("\n--- With Compression + Encryption ---")
    source = CompressionDecorator(
        EncryptionDecorator(
            FileDataSource("secure.txt")
        )
    )
    source.write_data("Hello World")
    print(f"Read: {source.read_data()}")


if __name__ == "__main__":
    main() 