"""
Singleton Pattern Implementation in Python

The Singleton pattern ensures that a class has only one instance and provides
global access to that instance.

This module demonstrates different ways to implement the Singleton pattern in Python:
1. Classic Singleton with __new__
2. Decorator-based Singleton
3. Metaclass-based Singleton
4. Module-level Singleton
5. Thread-safe Singleton
"""

import threading
from typing import Optional, Any
from functools import wraps


# 1. Classic Singleton using __new__
class ClassicSingleton:
    """Classic Singleton implementation using __new__ method"""
    
    _instance: Optional['ClassicSingleton'] = None
    
    def __new__(cls) -> 'ClassicSingleton':
        if cls._instance is None:
            cls._instance = super().__new__(cls)
        return cls._instance
    
    def __init__(self) -> None:
        if not hasattr(self, '_initialized'):
            self._initialized = True
            self.value = "Classic Singleton Instance"
    
    def do_something(self) -> None:
        print(f"Classic Singleton: {self.value}")


# 2. Decorator-based Singleton
def singleton(cls):
    """Decorator to make a class a singleton"""
    instances = {}
    
    @wraps(cls)
    def get_instance(*args, **kwargs):
        if cls not in instances:
            instances[cls] = cls(*args, **kwargs)
        return instances[cls]
    
    return get_instance


@singleton
class DecoratorSingleton:
    """Singleton class using decorator"""
    
    def __init__(self) -> None:
        self.value = "Decorator Singleton Instance"
    
    def do_something(self) -> None:
        print(f"Decorator Singleton: {self.value}")


# 3. Metaclass-based Singleton
class SingletonMeta(type):
    """Metaclass for creating singleton classes"""
    
    _instances = {}
    _lock = threading.Lock()
    
    def __call__(cls, *args, **kwargs):
        if cls not in cls._instances:
            with cls._lock:
                if cls not in cls._instances:
                    cls._instances[cls] = super().__call__(*args, **kwargs)
        return cls._instances[cls]


class MetaclassSingleton(metaclass=SingletonMeta):
    """Singleton class using metaclass"""
    
    def __init__(self) -> None:
        self.value = "Metaclass Singleton Instance"
    
    def do_something(self) -> None:
        print(f"Metaclass Singleton: {self.value}")


# 4. Module-level Singleton
class _ModuleSingleton:
    """Private singleton class for module-level singleton"""
    
    def __init__(self) -> None:
        self.value = "Module Singleton Instance"
    
    def do_something(self) -> None:
        print(f"Module Singleton: {self.value}")


# Create the singleton instance at module level
module_singleton = _ModuleSingleton()


# 5. Thread-safe Singleton
class ThreadSafeSingleton:
    """Thread-safe singleton implementation"""
    
    _instance: Optional['ThreadSafeSingleton'] = None
    _lock = threading.Lock()
    
    def __new__(cls) -> 'ThreadSafeSingleton':
        if cls._instance is None:
            with cls._lock:
                if cls._instance is None:
                    cls._instance = super().__new__(cls)
        return cls._instance
    
    def __init__(self) -> None:
        if not hasattr(self, '_initialized'):
            self._initialized = True
            self.value = "Thread-safe Singleton Instance"
    
    def do_something(self) -> None:
        print(f"Thread-safe Singleton: {self.value}")


# 6. Enum-based Singleton (Python 3.4+)
from enum import Enum


class EnumSingleton(Enum):
    """Singleton using Enum"""
    
    INSTANCE = "Enum Singleton Instance"
    
    def do_something(self) -> None:
        print(f"Enum Singleton: {self.value}")


# 7. Property-based Singleton
class PropertySingleton:
    """Singleton using property decorator"""
    
    _instance: Optional['PropertySingleton'] = None
    
    @classmethod
    @property
    def instance(cls) -> 'PropertySingleton':
        if cls._instance is None:
            cls._instance = cls()
        return cls._instance
    
    def __init__(self) -> None:
        if hasattr(self.__class__, '_instance') and self.__class__._instance is not None:
            raise RuntimeError("Use PropertySingleton.instance instead of direct instantiation")
        self.value = "Property Singleton Instance"
    
    def do_something(self) -> None:
        print(f"Property Singleton: {self.value}")


# 8. Context Manager Singleton
class ContextManagerSingleton:
    """Singleton that can be used as a context manager"""
    
    _instance: Optional['ContextManagerSingleton'] = None
    _lock = threading.Lock()
    
    def __new__(cls) -> 'ContextManagerSingleton':
        if cls._instance is None:
            with cls._lock:
                if cls._instance is None:
                    cls._instance = super().__new__(cls)
        return cls._instance
    
    def __init__(self) -> None:
        if not hasattr(self, '_initialized'):
            self._initialized = True
            self.value = "Context Manager Singleton Instance"
    
    def __enter__(self) -> 'ContextManagerSingleton':
        return self
    
    def __exit__(self, exc_type, exc_val, exc_tb) -> None:
        pass
    
    def do_something(self) -> None:
        print(f"Context Manager Singleton: {self.value}")


# Database Connection Singleton Example
class DatabaseConnection:
    """Example of a real-world singleton - Database connection"""
    
    _instance: Optional['DatabaseConnection'] = None
    _lock = threading.Lock()
    
    def __new__(cls) -> 'DatabaseConnection':
        if cls._instance is None:
            with cls._lock:
                if cls._instance is None:
                    cls._instance = super().__new__(cls)
        return cls._instance
    
    def __init__(self) -> None:
        if not hasattr(self, '_initialized'):
            self._initialized = True
            self.host = "localhost"
            self.port = 5432
            self.database = "mydb"
            self.connection_string = f"postgresql://{self.host}:{self.port}/{self.database}"
            print(f"Database connection initialized: {self.connection_string}")
    
    def connect(self) -> None:
        print(f"Connecting to database: {self.connection_string}")
    
    def disconnect(self) -> None:
        print("Disconnecting from database")
    
    def execute_query(self, query: str) -> str:
        print(f"Executing query: {query}")
        return f"Result for: {query}"


# Configuration Singleton Example
class ConfigurationManager:
    """Example of a configuration manager singleton"""
    
    _instance: Optional['ConfigurationManager'] = None
    _lock = threading.Lock()
    
    def __new__(cls) -> 'ConfigurationManager':
        if cls._instance is None:
            with cls._lock:
                if cls._instance is None:
                    cls._instance = super().__new__(cls)
        return cls._instance
    
    def __init__(self) -> None:
        if not hasattr(self, '_initialized'):
            self._initialized = True
            self.config = {
                'app_name': 'MyApp',
                'version': '1.0.0',
                'debug': True,
                'database_url': 'postgresql://localhost:5432/mydb'
            }
    
    def get_config(self, key: str) -> Any:
        return self.config.get(key)
    
    def set_config(self, key: str, value: Any) -> None:
        self.config[key] = value
    
    def get_all_config(self) -> dict:
        return self.config.copy()


def test_singleton_implementations():
    """Test function to demonstrate different singleton implementations"""
    
    print("=== Singleton Pattern Demo ===")
    
    # Test Classic Singleton
    print("\n--- Classic Singleton ---")
    classic1 = ClassicSingleton()
    classic2 = ClassicSingleton()
    print(f"Same instance: {classic1 is classic2}")
    classic1.do_something()
    
    # Test Decorator Singleton
    print("\n--- Decorator Singleton ---")
    decorator1 = DecoratorSingleton()
    decorator2 = DecoratorSingleton()
    print(f"Same instance: {decorator1 is decorator2}")
    decorator1.do_something()
    
    # Test Metaclass Singleton
    print("\n--- Metaclass Singleton ---")
    metaclass1 = MetaclassSingleton()
    metaclass2 = MetaclassSingleton()
    print(f"Same instance: {metaclass1 is metaclass2}")
    metaclass1.do_something()
    
    # Test Module Singleton
    print("\n--- Module Singleton ---")
    module_singleton.do_something()
    
    # Test Thread-safe Singleton
    print("\n--- Thread-safe Singleton ---")
    thread_safe1 = ThreadSafeSingleton()
    thread_safe2 = ThreadSafeSingleton()
    print(f"Same instance: {thread_safe1 is thread_safe2}")
    thread_safe1.do_something()
    
    # Test Enum Singleton
    print("\n--- Enum Singleton ---")
    enum1 = EnumSingleton.INSTANCE
    enum2 = EnumSingleton.INSTANCE
    print(f"Same instance: {enum1 is enum2}")
    enum1.do_something()
    
    # Test Context Manager Singleton
    print("\n--- Context Manager Singleton ---")
    with ContextManagerSingleton() as cm1:
        with ContextManagerSingleton() as cm2:
            print(f"Same instance: {cm1 is cm2}")
            cm1.do_something()
    
    # Test Database Connection Singleton
    print("\n--- Database Connection Singleton ---")
    db1 = DatabaseConnection()
    db2 = DatabaseConnection()
    print(f"Same instance: {db1 is db2}")
    db1.connect()
    db1.execute_query("SELECT * FROM users")
    db2.disconnect()
    
    # Test Configuration Manager Singleton
    print("\n--- Configuration Manager Singleton ---")
    config1 = ConfigurationManager()
    config2 = ConfigurationManager()
    print(f"Same instance: {config1 is config2}")
    print(f"App name: {config1.get_config('app_name')}")
    config1.set_config('environment', 'development')
    print(f"Environment from config2: {config2.get_config('environment')}")
    
    # Test thread safety
    print("\n--- Thread Safety Test ---")
    
    def create_singleton():
        singleton = ThreadSafeSingleton()
        print(f"Thread {threading.current_thread().name}: {id(singleton)}")
    
    threads = []
    for i in range(5):
        thread = threading.Thread(target=create_singleton, name=f"Thread-{i}")
        threads.append(thread)
        thread.start()
    
    for thread in threads:
        thread.join()


if __name__ == "__main__":
    test_singleton_implementations() 