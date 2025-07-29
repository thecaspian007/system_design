from datetime import datetime
from typing import List
from transaction import Transaction, TransactionType

class Account:
    """
    Account class representing a bank account
    """
    
    def __init__(self, account_number: str, pin: str, initial_balance: float = 0.0):
        self.account_number = account_number
        self.pin = pin
        self.balance = initial_balance
        self.is_active = True
        self.transactions: List[Transaction] = []
        self.created_at = datetime.now()
        
        # Add initial balance transaction
        if initial_balance > 0:
            self.add_transaction(Transaction(TransactionType.DEPOSIT, initial_balance, "Initial deposit"))
    
    def validate_pin(self, pin: str) -> bool:
        """Validate PIN"""
        return self.pin == pin
    
    def set_pin(self, new_pin: str):
        """Set new PIN"""
        self.pin = new_pin
        self.add_transaction(Transaction(TransactionType.PIN_CHANGE, 0, "PIN changed"))
    
    def withdraw(self, amount: float) -> bool:
        """Withdraw amount from account"""
        if not self.is_active:
            return False
        
        if amount <= 0:
            return False
        
        if self.balance >= amount:
            self.balance -= amount
            self.add_transaction(Transaction(TransactionType.WITHDRAWAL, amount, "Cash withdrawal"))
            return True
        
        return False
    
    def deposit(self, amount: float) -> bool:
        """Deposit amount to account"""
        if not self.is_active:
            return False
        
        if amount <= 0:
            return False
        
        self.balance += amount
        self.add_transaction(Transaction(TransactionType.DEPOSIT, amount, "Cash deposit"))
        return True
    
    def add_transaction(self, transaction: Transaction):
        """Add transaction to history"""
        self.transactions.append(transaction)
    
    def get_transaction_history(self) -> List[Transaction]:
        """Get transaction history"""
        return self.transactions.copy()
    
    def get_balance(self) -> float:
        """Get current balance"""
        return self.balance
    
    def deactivate(self):
        """Deactivate account"""
        self.is_active = False
    
    def activate(self):
        """Activate account"""
        self.is_active = True
    
    def __str__(self):
        return f"Account {self.account_number}: Balance ${self.balance:.2f} ({'Active' if self.is_active else 'Inactive'})" 