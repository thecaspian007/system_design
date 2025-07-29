from typing import List, Optional
from bank import Bank
from account import Account
from transaction import Transaction, TransactionType

class ATMMachine:
    """
    ATMMachine class that handles ATM operations
    """
    
    DAILY_WITHDRAWAL_LIMIT = 1000.0
    
    def __init__(self, bank: Bank):
        self.bank = bank
        self.cash_available = 50000.0  # ATM starts with $50,000
    
    def authenticate_user(self, account_number: str, pin: str) -> bool:
        """Authenticate user"""
        return self.bank.authenticate_account(account_number, pin)
    
    def check_balance(self, account_number: str) -> float:
        """Check balance"""
        account = self.bank.get_account(account_number)
        if account:
            account.add_transaction(Transaction(TransactionType.BALANCE_INQUIRY, 0, "Balance inquiry"))
            return account.get_balance()
        return 0.0
    
    def withdraw_cash(self, account_number: str, amount: float) -> bool:
        """Withdraw cash"""
        account = self.bank.get_account(account_number)
        
        if not account or not account.is_active:
            return False
        
        # Check withdrawal limits
        if amount > self.DAILY_WITHDRAWAL_LIMIT:
            print(f"Withdrawal amount exceeds daily limit of ${self.DAILY_WITHDRAWAL_LIMIT}")
            return False
        
        # Check if ATM has enough cash
        if amount > self.cash_available:
            print("ATM does not have sufficient cash. Please try a smaller amount.")
            return False
        
        # Check if account has sufficient balance
        if account.withdraw(amount):
            self.cash_available -= amount
            return True
        
        return False
    
    def deposit_cash(self, account_number: str, amount: float) -> bool:
        """Deposit cash"""
        account = self.bank.get_account(account_number)
        
        if not account or not account.is_active:
            return False
        
        if amount <= 0:
            return False
        
        return account.deposit(amount)
    
    def get_transaction_history(self, account_number: str) -> List[Transaction]:
        """Get transaction history"""
        account = self.bank.get_account(account_number)
        if account:
            return account.get_transaction_history()
        return []
    
    def get_cash_available(self) -> float:
        """Get available cash in ATM"""
        return self.cash_available
    
    def replenish_cash(self, amount: float):
        """Replenish cash in ATM"""
        self.cash_available += amount 