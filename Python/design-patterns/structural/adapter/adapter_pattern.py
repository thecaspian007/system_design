"""
Adapter Pattern Implementation

The Adapter pattern allows objects with incompatible interfaces to work together.
It acts as a wrapper between two objects, catching calls for one object and
transforming them to format and interface recognizable by the second object.
"""

from abc import ABC, abstractmethod


# Target interface that the client expects
class MediaPlayer(ABC):
    @abstractmethod
    def play(self, audio_type: str, file_name: str):
        pass


# Adaptee - class with incompatible interface
class AdvancedMediaPlayer:
    def play_vlc(self, file_name: str):
        print(f"Playing vlc file: {file_name}")
    
    def play_mp4(self, file_name: str):
        print(f"Playing mp4 file: {file_name}")


# Adapter class implementing the target interface
class MediaAdapter(MediaPlayer):
    def __init__(self, audio_type: str):
        if audio_type.lower() in ["vlc", "mp4"]:
            self.advanced_player = AdvancedMediaPlayer()
        else:
            self.advanced_player = None
    
    def play(self, audio_type: str, file_name: str):
        if audio_type.lower() == "vlc":
            self.advanced_player.play_vlc(file_name)
        elif audio_type.lower() == "mp4":
            self.advanced_player.play_mp4(file_name)


# Client class
class AudioPlayer(MediaPlayer):
    def __init__(self):
        self.media_adapter = None
    
    def play(self, audio_type: str, file_name: str):
        # Built-in support for mp3 files
        if audio_type.lower() == "mp3":
            print(f"Playing mp3 file: {file_name}")
        # Using adapter for other formats
        elif audio_type.lower() in ["vlc", "mp4"]:
            self.media_adapter = MediaAdapter(audio_type)
            self.media_adapter.play(audio_type, file_name)
        else:
            print(f"Invalid media. {audio_type} format not supported")


# Another example - Database Connection Adapter
class DatabaseTarget(ABC):
    @abstractmethod
    def connect(self):
        pass
    
    @abstractmethod
    def execute_query(self, query: str):
        pass
    
    @abstractmethod
    def disconnect(self):
        pass


# Legacy database class
class LegacyDatabase:
    def establish_connection(self):
        print("Legacy database connection established")
    
    def run_query(self, sql: str):
        print(f"Executing legacy query: {sql}")
    
    def close_connection(self):
        print("Legacy database connection closed")


# Database Adapter
class DatabaseAdapter(DatabaseTarget):
    def __init__(self, legacy_database: LegacyDatabase):
        self.legacy_database = legacy_database
    
    def connect(self):
        self.legacy_database.establish_connection()
    
    def execute_query(self, query: str):
        self.legacy_database.run_query(query)
    
    def disconnect(self):
        self.legacy_database.close_connection()


# Third example - Shape Adapter
class Shape(ABC):
    @abstractmethod
    def draw(self):
        pass


class Rectangle(Shape):
    def draw(self):
        print("Drawing Rectangle")


class Circle(Shape):
    def draw(self):
        print("Drawing Circle")


# Legacy shape classes with different interface
class LegacyTriangle:
    def draw_triangle(self):
        print("Drawing Legacy Triangle")


class LegacyLine:
    def draw_line(self):
        print("Drawing Legacy Line")


# Shape Adapter
class ShapeAdapter(Shape):
    def __init__(self, legacy_shape):
        self.legacy_shape = legacy_shape
    
    def draw(self):
        if isinstance(self.legacy_shape, LegacyTriangle):
            self.legacy_shape.draw_triangle()
        elif isinstance(self.legacy_shape, LegacyLine):
            self.legacy_shape.draw_line()


# Payment System Adapter Example
class PaymentProcessor(ABC):
    @abstractmethod
    def process_payment(self, amount: float, currency: str = "USD"):
        pass


class PayPalPayment:
    def make_payment(self, amount: float):
        print(f"Processing PayPal payment of ${amount}")


class StripePayment:
    def charge(self, amount_cents: int):
        print(f"Processing Stripe payment of {amount_cents} cents")


class PayPalAdapter(PaymentProcessor):
    def __init__(self, paypal: PayPalPayment):
        self.paypal = paypal
    
    def process_payment(self, amount: float, currency: str = "USD"):
        self.paypal.make_payment(amount)


class StripeAdapter(PaymentProcessor):
    def __init__(self, stripe: StripePayment):
        self.stripe = stripe
    
    def process_payment(self, amount: float, currency: str = "USD"):
        # Convert dollars to cents
        amount_cents = int(amount * 100)
        self.stripe.charge(amount_cents)


def main():
    print("=== Adapter Pattern Demo ===")
    
    # Media Player Example
    print("\n--- Media Player Example ---")
    audio_player = AudioPlayer()
    
    audio_player.play("mp3", "beyond_the_horizon.mp3")
    audio_player.play("mp4", "alone.mp4")
    audio_player.play("vlc", "far_far_away.vlc")
    audio_player.play("avi", "mind_me.avi")
    
    # Database Example
    print("\n--- Database Adapter Example ---")
    legacy_db = LegacyDatabase()
    modern_db = DatabaseAdapter(legacy_db)
    
    modern_db.connect()
    modern_db.execute_query("SELECT * FROM users")
    modern_db.disconnect()
    
    # Shape Adapter Example
    print("\n--- Shape Adapter Example ---")
    
    # Regular shapes
    rectangle = Rectangle()
    circle = Circle()
    
    # Legacy shapes using adapter
    triangle = ShapeAdapter(LegacyTriangle())
    line = ShapeAdapter(LegacyLine())
    
    # Draw all shapes using the same interface
    shapes = [rectangle, circle, triangle, line]
    
    for shape in shapes:
        shape.draw()
    
    # Payment System Example
    print("\n--- Payment System Adapter Example ---")
    
    paypal = PayPalPayment()
    stripe = StripePayment()
    
    paypal_adapter = PayPalAdapter(paypal)
    stripe_adapter = StripeAdapter(stripe)
    
    # Both can be used through the same interface
    payment_processors = [paypal_adapter, stripe_adapter]
    
    for processor in payment_processors:
        processor.process_payment(25.99)


if __name__ == "__main__":
    main() 