"""
Observer Pattern Implementation

The Observer pattern defines a one-to-many dependency between objects
so that when one object changes state, all its dependents are notified
and updated automatically.
"""

from abc import ABC, abstractmethod
from typing import List


# Observer interface
class Observer(ABC):
    @abstractmethod
    def update(self, message: str):
        pass


# Subject interface
class Subject(ABC):
    @abstractmethod
    def register_observer(self, observer: Observer):
        pass
    
    @abstractmethod
    def remove_observer(self, observer: Observer):
        pass
    
    @abstractmethod
    def notify_observers(self):
        pass


# Concrete Subject - News Agency
class NewsAgency(Subject):
    def __init__(self):
        self.observers: List[Observer] = []
        self.news: str = ""
    
    def register_observer(self, observer: Observer):
        self.observers.append(observer)
        print(f"Observer registered: {observer.__class__.__name__}")
    
    def remove_observer(self, observer: Observer):
        if observer in self.observers:
            self.observers.remove(observer)
            print(f"Observer removed: {observer.__class__.__name__}")
    
    def notify_observers(self):
        for observer in self.observers:
            observer.update(self.news)
    
    def set_news(self, news: str):
        self.news = news
        self.notify_observers()
    
    def get_news(self) -> str:
        return self.news


# Concrete Observers
class NewsChannel(Observer):
    def __init__(self, channel_name: str):
        self.channel_name = channel_name
    
    def update(self, message: str):
        print(f"{self.channel_name} received news: {message}")


class MobileApp(Observer):
    def __init__(self, app_name: str):
        self.app_name = app_name
    
    def update(self, message: str):
        print(f"{self.app_name} app notification: {message}")


class EmailSubscriber(Observer):
    def __init__(self, email: str):
        self.email = email
    
    def update(self, message: str):
        print(f"Email sent to {self.email}: {message}")


# Weather Station Example
class WeatherObserver(ABC):
    @abstractmethod
    def update(self, temperature: float, humidity: float, pressure: float):
        pass


class WeatherStation:
    def __init__(self):
        self.observers: List[WeatherObserver] = []
        self.temperature: float = 0.0
        self.humidity: float = 0.0
        self.pressure: float = 0.0
    
    def register_observer(self, observer: WeatherObserver):
        self.observers.append(observer)
    
    def remove_observer(self, observer: WeatherObserver):
        if observer in self.observers:
            self.observers.remove(observer)
    
    def notify_observers(self):
        for observer in self.observers:
            observer.update(self.temperature, self.humidity, self.pressure)
    
    def measurements_changed(self):
        self.notify_observers()
    
    def set_measurements(self, temperature: float, humidity: float, pressure: float):
        self.temperature = temperature
        self.humidity = humidity
        self.pressure = pressure
        self.measurements_changed()


class CurrentConditionsDisplay(WeatherObserver):
    def __init__(self, weather_station: WeatherStation):
        self.temperature: float = 0.0
        self.humidity: float = 0.0
        self.weather_station = weather_station
        weather_station.register_observer(self)
    
    def update(self, temperature: float, humidity: float, pressure: float):
        self.temperature = temperature
        self.humidity = humidity
        self.display()
    
    def display(self):
        print(f"Current conditions: {self.temperature}°C and {self.humidity}% humidity")


class StatisticsDisplay(WeatherObserver):
    def __init__(self, weather_station: WeatherStation):
        self.max_temp: float = 0.0
        self.min_temp: float = 200.0
        self.temp_sum: float = 0.0
        self.num_readings: int = 0
        self.weather_station = weather_station
        weather_station.register_observer(self)
    
    def update(self, temperature: float, humidity: float, pressure: float):
        self.temp_sum += temperature
        self.num_readings += 1
        
        if temperature > self.max_temp:
            self.max_temp = temperature
        
        if temperature < self.min_temp:
            self.min_temp = temperature
        
        self.display()
    
    def display(self):
        avg_temp = self.temp_sum / self.num_readings
        print(f"Avg/Max/Min temperature = {avg_temp:.2f}/{self.max_temp}/{self.min_temp}")


class ForecastDisplay(WeatherObserver):
    def __init__(self, weather_station: WeatherStation):
        self.current_pressure: float = 29.92
        self.last_pressure: float = 29.92
        self.weather_station = weather_station
        weather_station.register_observer(self)
    
    def update(self, temperature: float, humidity: float, pressure: float):
        self.last_pressure = self.current_pressure
        self.current_pressure = pressure
        self.display()
    
    def display(self):
        print("Forecast: ", end="")
        if self.current_pressure > self.last_pressure:
            print("Improving weather on the way!")
        elif self.current_pressure == self.last_pressure:
            print("More of the same")
        else:
            print("Watch out for cooler, rainy weather")


# Stock Market Example
class StockObserver(ABC):
    @abstractmethod
    def update(self, symbol: str, price: float):
        pass


class Stock:
    def __init__(self, symbol: str, price: float):
        self.symbol = symbol
        self.price = price
        self.observers: List[StockObserver] = []
    
    def add_observer(self, observer: StockObserver):
        self.observers.append(observer)
    
    def remove_observer(self, observer: StockObserver):
        if observer in self.observers:
            self.observers.remove(observer)
    
    def notify_observers(self):
        for observer in self.observers:
            observer.update(self.symbol, self.price)
    
    def set_price(self, price: float):
        self.price = price
        self.notify_observers()
    
    def get_symbol(self) -> str:
        return self.symbol
    
    def get_price(self) -> float:
        return self.price


class StockTrader(StockObserver):
    def __init__(self, name: str):
        self.name = name
    
    def update(self, symbol: str, price: float):
        print(f"Trader {self.name} notified: {symbol} is now ${price}")


class StockDisplay(StockObserver):
    def update(self, symbol: str, price: float):
        print(f"Stock Display: {symbol} - ${price}")


def main():
    print("=== Observer Pattern Demo ===")
    
    # News Agency Example
    print("\n--- News Agency Example ---")
    agency = NewsAgency()
    
    cnn = NewsChannel("CNN")
    bbc = NewsChannel("BBC")
    news_app = MobileApp("NewsApp")
    subscriber = EmailSubscriber("john@example.com")
    
    agency.register_observer(cnn)
    agency.register_observer(bbc)
    agency.register_observer(news_app)
    agency.register_observer(subscriber)
    
    agency.set_news("Breaking: Major earthquake hits Japan")
    
    print("\n--- Removing BBC ---")
    agency.remove_observer(bbc)
    agency.set_news("Update: Relief efforts underway")
    
    # Weather Station Example
    print("\n--- Weather Station Example ---")
    weather_station = WeatherStation()
    
    current_display = CurrentConditionsDisplay(weather_station)
    stats_display = StatisticsDisplay(weather_station)
    forecast_display = ForecastDisplay(weather_station)
    
    weather_station.set_measurements(25.0, 65.0, 30.4)
    weather_station.set_measurements(22.0, 70.0, 29.2)
    weather_station.set_measurements(28.0, 90.0, 29.2)
    
    # Stock Market Example
    print("\n--- Stock Market Example ---")
    apple_stock = Stock("AAPL", 150.00)
    google_stock = Stock("GOOGL", 2500.00)
    
    trader1 = StockTrader("Alice")
    trader2 = StockTrader("Bob")
    display = StockDisplay()
    
    apple_stock.add_observer(trader1)
    apple_stock.add_observer(trader2)
    apple_stock.add_observer(display)
    
    google_stock.add_observer(trader1)
    google_stock.add_observer(display)
    
    print("\n--- Stock Price Changes ---")
    apple_stock.set_price(155.00)
    google_stock.set_price(2550.00)
    
    apple_stock.remove_observer(trader2)
    apple_stock.set_price(160.00)


if __name__ == "__main__":
    main() 