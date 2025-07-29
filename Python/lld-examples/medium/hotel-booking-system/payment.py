from enum import Enum

class PaymentMethod(Enum):
    """Payment method enum"""
    CREDIT_CARD = "Credit Card"
    DEBIT_CARD = "Debit Card"
    CASH = "Cash"
    ONLINE = "Online"
