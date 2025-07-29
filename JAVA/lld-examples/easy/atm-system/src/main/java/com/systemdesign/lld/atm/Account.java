package com.systemdesign.lld.atm;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Account class representing a bank account
 */
public class Account {
    private String accountNumber;
    private String pin;
    private double balance;
    private List<Transaction> transactionHistory;
    private LocalDateTime createdDate;
    private boolean isActive;
    
    public Account(String accountNumber, String pin, double initialBalance) {
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();
        this.createdDate = LocalDateTime.now();
        this.isActive = true;
        
        // Add initial balance transaction
        addTransaction(new Transaction(TransactionType.DEPOSIT, initialBalance, "Initial deposit"));
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public boolean validatePin(String inputPin) {
        return this.pin.equals(inputPin);
    }
    
    public double getBalance() {
        return balance;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
    }
    
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            return false;
        }
        
        if (balance >= amount) {
            balance -= amount;
            addTransaction(new Transaction(TransactionType.WITHDRAWAL, amount, "ATM withdrawal"));
            return true;
        }
        return false;
    }
    
    public boolean deposit(double amount) {
        if (amount <= 0) {
            return false;
        }
        
        balance += amount;
        addTransaction(new Transaction(TransactionType.DEPOSIT, amount, "ATM deposit"));
        return true;
    }
    
    public boolean changePin(String currentPin, String newPin) {
        if (validatePin(currentPin)) {
            this.pin = newPin;
            addTransaction(new Transaction(TransactionType.PIN_CHANGE, 0, "PIN changed"));
            return true;
        }
        return false;
    }
    
    public void addTransaction(Transaction transaction) {
        transactionHistory.add(transaction);
    }
    
    public List<Transaction> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }
    
    public List<Transaction> getRecentTransactions(int count) {
        List<Transaction> allTransactions = getTransactionHistory();
        int size = allTransactions.size();
        int startIndex = Math.max(0, size - count);
        return allTransactions.subList(startIndex, size);
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    @Override
    public String toString() {
        return "Account{" +
                "accountNumber='" + accountNumber + '\'' +
                ", balance=" + balance +
                ", isActive=" + isActive +
                ", transactionCount=" + transactionHistory.size() +
                '}';
    }
} 