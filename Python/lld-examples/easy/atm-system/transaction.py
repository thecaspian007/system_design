from datetime import datetime
from enum import Enum

class TransactionType(Enum):
    """Enum representing different types of transactions"""
    DEPOSIT = "Deposit"
    WITHDRAWAL = "Withdrawal"
    BALANCE_INQUIRY = "Balance Inquiry"
    PIN_CHANGE = "PIN Change"
    TRANSFER = "Transfer"
    
    def __str__(self):
        return self.value

class Transaction:
    """
    Transaction class representing individual transactions
    """
    
    _transaction_counter = 1
    
    def __init__(self, transaction_type: TransactionType, amount: float, description: str):
        self.transaction_id = f"TXN{Transaction._transaction_counter:06d}"
        Transaction._transaction_counter += 1
        self.type = transaction_type
        self.amount = amount
        self.description = description
        self.timestamp = datetime.now()
    
    def get_formatted_timestamp(self) -> str:
        """Get formatted timestamp"""
        return self.timestamp.strftime("%Y-%m-%d %H:%M:%S")
    
    def __str__(self):
        return f"{self.transaction_id} | {self.get_formatted_timestamp()} | {self.type.value} | ${self.amount:.2f} | {self.description}" 