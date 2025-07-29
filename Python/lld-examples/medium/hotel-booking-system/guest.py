from datetime import datetime
import uuid

class Guest:
    """Guest class representing a hotel guest"""
    
    def __init__(self, name: str, email: str, phone: str):
        self.guest_id = str(uuid.uuid4())[:8].upper()
        self.name = name
        self.email = email
        self.phone = phone
        self.created_at = datetime.now()
        self.booking_history = []
    
    def add_booking(self, booking):
        """Add booking to guest history"""
        self.booking_history.append(booking)
    
    def __str__(self):
        return f"Guest {self.name} ({self.email})"
