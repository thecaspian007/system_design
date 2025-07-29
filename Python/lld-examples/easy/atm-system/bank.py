from typing import Dict, Optional
from account import Account

class Bank:
    """
    Bank class that manages multiple accounts
    """
    
    def __init__(self):
        self.accounts: Dict[str, Account] = {}
    
    def add_account(self, account: Account):
        """Add account to bank"""
        self.accounts[account.account_number] = account
    
    def get_account(self, account_number: str) -> Optional[Account]:
        """Get account by account number"""
        return self.accounts.get(account_number)
    
    def authenticate_account(self, account_number: str, pin: str) -> bool:
        """Authenticate account with PIN"""
        account = self.get_account(account_number)
        return account is not None and account.is_active and account.validate_pin(pin)
    
    def account_exists(self, account_number: str) -> bool:
        """Check if account exists"""
        return account_number in self.accounts
    
    def get_total_accounts(self) -> int:
        """Get total number of accounts"""
        return len(self.accounts)
    
    def get_total_balance(self) -> float:
        """Get total balance across all accounts"""
        return sum(account.balance for account in self.accounts.values())
    
    def deactivate_account(self, account_number: str):
        """Deactivate account"""
        account = self.get_account(account_number)
        if account:
            account.deactivate()
    
    def activate_account(self, account_number: str):
        """Activate account"""
        account = self.get_account(account_number)
        if account:
            account.activate() 