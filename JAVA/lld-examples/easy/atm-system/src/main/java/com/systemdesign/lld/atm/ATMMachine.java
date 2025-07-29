package com.systemdesign.lld.atm;

import java.util.List;

/**
 * ATMMachine class that handles ATM operations
 */
public class ATMMachine {
    private Bank bank;
    private double cashAvailable;
    private static final double DAILY_WITHDRAWAL_LIMIT = 1000.0;
    
    public ATMMachine(Bank bank) {
        this.bank = bank;
        this.cashAvailable = 50000.0; // ATM starts with $50,000
    }
    
    public boolean authenticateUser(String accountNumber, String pin) {
        return bank.authenticateAccount(accountNumber, pin);
    }
    
    public double checkBalance(String accountNumber) {
        Account account = bank.getAccount(accountNumber);
        if (account != null) {
            account.addTransaction(new Transaction(TransactionType.BALANCE_INQUIRY, 0, "Balance inquiry"));
            return account.getBalance();
        }
        return 0.0;
    }
    
    public boolean withdrawCash(String accountNumber, double amount) {
        Account account = bank.getAccount(accountNumber);
        
        if (account == null || !account.isActive()) {
            return false;
        }
        
        // Check withdrawal limits
        if (amount > DAILY_WITHDRAWAL_LIMIT) {
            System.out.println("Withdrawal amount exceeds daily limit of $" + DAILY_WITHDRAWAL_LIMIT);
            return false;
        }
        
        // Check if ATM has enough cash
        if (amount > cashAvailable) {
            System.out.println("ATM does not have sufficient cash. Please try a smaller amount.");
            return false;
        }
        
        // Check if account has sufficient balance
        if (account.withdraw(amount)) {
            cashAvailable -= amount;
            return true;
        }
        
        return false;
    }
    
    public boolean depositCash(String accountNumber, double amount) {
        Account account = bank.getAccount(accountNumber);
        
        if (account == null || !account.isActive()) {
            return false;
        }
        
        if (account.deposit(amount)) {
            cashAvailable += amount;
            return true;
        }
        
        return false;
    }
    
    public boolean changePin(String accountNumber, String currentPin, String newPin) {
        Account account = bank.getAccount(accountNumber);
        
        if (account == null || !account.isActive()) {
            return false;
        }
        
        return account.changePin(currentPin, newPin);
    }
    
    public void printTransactionHistory(String accountNumber) {
        Account account = bank.getAccount(accountNumber);
        
        if (account == null) {
            System.out.println("Account not found.");
            return;
        }
        
        List<Transaction> transactions = account.getRecentTransactions(10);
        
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }
        
        System.out.println("Recent Transactions (Last 10):");
        System.out.println("Transaction ID | Date & Time | Type | Amount | Description");
        System.out.println("=".repeat(80));
        
        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
    }
    
    public double getCashAvailable() {
        return cashAvailable;
    }
    
    public void refillCash(double amount) {
        cashAvailable += amount;
        System.out.println("ATM refilled with $" + amount + ". Total cash available: $" + cashAvailable);
    }
    
    public boolean isOutOfService() {
        return cashAvailable < 100.0; // Consider out of service if less than $100
    }
} 