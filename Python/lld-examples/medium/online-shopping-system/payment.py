from enum import Enum
from datetime import datetime
import uuid

class PaymentMethod(Enum):
    """Payment method enum"""
    CREDIT_CARD = "Credit Card"
    PAYPAL = "PayPal"
    BANK_TRANSFER = "Bank Transfer"
    CASH_ON_DELIVERY = "Cash on Delivery"

class PaymentStatus(Enum):
    """Payment status enum"""
    PENDING = "Pending"
    COMPLETED = "Completed"
    FAILED = "Failed"
    REFUNDED = "Refunded"

class Payment:
    """Payment class representing a payment transaction"""
    
    def __init__(self, order_id: str, amount: float, payment_method: PaymentMethod):
        self.payment_id = str(uuid.uuid4())[:8].upper()
        self.order_id = order_id
        self.amount = amount
        self.payment_method = payment_method
        self.status = PaymentStatus.PENDING
        self.payment_date = datetime.now()
        self.transaction_id = ""
    
    def process_payment(self) -> bool:
        """Process payment"""
        # Simulate payment processing
        self.status = PaymentStatus.COMPLETED
        self.transaction_id = f"TXN{self.payment_id}"
        return True
    
    def refund_payment(self) -> bool:
        """Refund payment"""
        if self.status == PaymentStatus.COMPLETED:
            self.status = PaymentStatus.REFUNDED
            return True
        return False
    
    def get_formatted_payment_date(self) -> str:
        """Get formatted payment date"""
        return self.payment_date.strftime("%Y-%m-%d %H:%M:%S")
    
    def __str__(self):
        return f"Payment {self.payment_id} - {self.status.value} - ${self.amount:.2f}" 