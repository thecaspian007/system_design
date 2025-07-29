from datetime import datetime
import sys
import os

# Add the current directory to the Python path
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

from atm_machine import ATMMachine
from bank import Bank
from account import Account

class ATMSystem:
    """
    ATM System - Main Application
    
    This system demonstrates:
    - Account management
    - PIN verification
    - Balance inquiry
    - Cash withdrawal
    - Deposit functionality
    - Transaction history
    
    @author System Design Repository
    """
    
    def __init__(self):
        self.bank = Bank()
        self.atm_machine = ATMMachine(self.bank)
        
        # Initialize with sample accounts
        self._initialize_sample_accounts()
    
    def _initialize_sample_accounts(self):
        """Initialize sample accounts for demonstration"""
        account1 = Account("123456789", "1234", 1000.0)
        account2 = Account("987654321", "5678", 2500.0)
        account3 = Account("555666777", "9999", 500.0)
        
        self.bank.add_account(account1)
        self.bank.add_account(account2)
        self.bank.add_account(account3)
        
        print("Sample accounts created:")
        print("Account: 123456789, PIN: 1234, Balance: $1000.00")
        print("Account: 987654321, PIN: 5678, Balance: $2500.00")
        print("Account: 555666777, PIN: 9999, Balance: $500.00")
    
    def start(self):
        """Start the ATM system"""
        print("=== Welcome to ATM System ===")
        
        while True:
            try:
                account = self._authenticate_user()
                if account:
                    self._show_main_menu(account)
                else:
                    print("Authentication failed. Please try again.")
            except KeyboardInterrupt:
                print("\nThank you for using ATM System!")
                break
            except Exception as e:
                print(f"Error: {e}")
    
    def _authenticate_user(self):
        """Authenticate user with account number and PIN"""
        print("\n--- ATM Authentication ---")
        account_number = input("Enter account number: ")
        pin = input("Enter PIN: ")
        
        if self.atm_machine.authenticate_user(account_number, pin):
            return self.bank.get_account(account_number)
        return None
    
    def _show_main_menu(self, account):
        """Show main menu for authenticated user"""
        while True:
            print(f"\n--- Welcome, Account: {account.account_number} ---")
            print("1. Check Balance")
            print("2. Withdraw Cash")
            print("3. Deposit Cash")
            print("4. View Transaction History")
            print("5. Change PIN")
            print("6. Logout")
            
            choice = self._get_int_input("Choose an option: ")
            
            if choice == 1:
                self._check_balance(account)
            elif choice == 2:
                self._withdraw_cash(account)
            elif choice == 3:
                self._deposit_cash(account)
            elif choice == 4:
                self._view_transaction_history(account)
            elif choice == 5:
                self._change_pin(account)
            elif choice == 6:
                print("Logged out successfully!")
                break
            else:
                print("Invalid choice. Please try again.")
    
    def _check_balance(self, account):
        """Check account balance"""
        print("\n--- Balance Inquiry ---")
        balance = self.atm_machine.check_balance(account.account_number)
        print(f"Your current balance is: ${balance:.2f}")
    
    def _withdraw_cash(self, account):
        """Withdraw cash from account"""
        print("\n--- Cash Withdrawal ---")
        amount = self._get_double_input("Enter amount to withdraw: $")
        
        if self.atm_machine.withdraw_cash(account.account_number, amount):
            print(f"Cash withdrawal successful! Amount: ${amount:.2f}")
            print(f"Remaining balance: ${account.balance:.2f}")
        else:
            print("Cash withdrawal failed. Please check your balance and try again.")
    
    def _deposit_cash(self, account):
        """Deposit cash to account"""
        print("\n--- Cash Deposit ---")
        amount = self._get_double_input("Enter amount to deposit: $")
        
        if self.atm_machine.deposit_cash(account.account_number, amount):
            print(f"Cash deposit successful! Amount: ${amount:.2f}")
            print(f"New balance: ${account.balance:.2f}")
        else:
            print("Cash deposit failed. Please try again.")
    
    def _view_transaction_history(self, account):
        """View transaction history"""
        print("\n--- Transaction History ---")
        transactions = self.atm_machine.get_transaction_history(account.account_number)
        
        if not transactions:
            print("No transactions found.")
            return
        
        print("Recent transactions:")
        for transaction in transactions[-10:]:  # Show last 10 transactions
            print(transaction)
    
    def _change_pin(self, account):
        """Change account PIN"""
        print("\n--- Change PIN ---")
        current_pin = input("Enter current PIN: ")
        
        if not account.validate_pin(current_pin):
            print("Current PIN is incorrect.")
            return
        
        new_pin = input("Enter new PIN: ")
        confirm_pin = input("Confirm new PIN: ")
        
        if new_pin != confirm_pin:
            print("PIN confirmation does not match.")
            return
        
        if len(new_pin) < 4 or not new_pin.isdigit():
            print("PIN must be at least 4 digits.")
            return
        
        account.set_pin(new_pin)
        print("PIN changed successfully!")
    
    def _get_int_input(self, prompt="Enter number: "):
        """Get integer input with validation"""
        while True:
            try:
                return int(input(prompt))
            except ValueError:
                print("Please enter a valid number.")
    
    def _get_double_input(self, prompt="Enter amount: "):
        """Get double input with validation"""
        while True:
            try:
                value = float(input(prompt))
                if value < 0:
                    print("Please enter a positive amount.")
                    continue
                return value
            except ValueError:
                print("Please enter a valid amount.")

if __name__ == "__main__":
    atm_system = ATMSystem()
    atm_system.start() 