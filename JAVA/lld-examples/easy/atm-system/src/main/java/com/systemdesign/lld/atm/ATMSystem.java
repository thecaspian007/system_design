package com.systemdesign.lld.atm;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * ATM System - Main Application
 * 
 * This system demonstrates:
 * - Account management
 * - PIN verification
 * - Balance inquiry
 * - Cash withdrawal
 * - Deposit functionality
 * - Transaction history
 * 
 * @author System Design Repository
 */
public class ATMSystem {
    
    private Bank bank;
    private ATMMachine atmMachine;
    private Scanner scanner;
    
    public ATMSystem() {
        this.bank = new Bank();
        this.atmMachine = new ATMMachine(bank);
        this.scanner = new Scanner(System.in);
        
        // Initialize with sample accounts
        initializeSampleAccounts();
    }
    
    private void initializeSampleAccounts() {
        // Create sample accounts
        Account account1 = new Account("123456789", "1234", 1000.0);
        Account account2 = new Account("987654321", "5678", 2500.0);
        Account account3 = new Account("555666777", "9999", 500.0);
        
        bank.addAccount(account1);
        bank.addAccount(account2);
        bank.addAccount(account3);
        
        System.out.println("Sample accounts created:");
        System.out.println("Account: 123456789, PIN: 1234, Balance: $1000.00");
        System.out.println("Account: 987654321, PIN: 5678, Balance: $2500.00");
        System.out.println("Account: 555666777, PIN: 9999, Balance: $500.00");
    }
    
    public void start() {
        System.out.println("=== Welcome to ATM System ===");
        
        while (true) {
            try {
                showMainMenu();
                int choice = getIntInput();
                
                switch (choice) {
                    case 1:
                        handleCardInsertion();
                        break;
                    case 2:
                        System.out.println("Thank you for using ATM System!");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    
    private void showMainMenu() {
        System.out.println("\n--- ATM Main Menu ---");
        System.out.println("1. Insert Card");
        System.out.println("2. Exit");
        System.out.print("Choose an option: ");
    }
    
    private void handleCardInsertion() {
        System.out.print("Enter your account number: ");
        String accountNumber = scanner.nextLine();
        
        System.out.print("Enter your PIN: ");
        String pin = scanner.nextLine();
        
        if (atmMachine.authenticateUser(accountNumber, pin)) {
            System.out.println("Authentication successful!");
            handleUserSession(accountNumber);
        } else {
            System.out.println("Authentication failed. Invalid account number or PIN.");
        }
    }
    
    private void handleUserSession(String accountNumber) {
        while (true) {
            try {
                showUserMenu();
                int choice = getIntInput();
                
                switch (choice) {
                    case 1:
                        handleBalanceInquiry(accountNumber);
                        break;
                    case 2:
                        handleWithdrawal(accountNumber);
                        break;
                    case 3:
                        handleDeposit(accountNumber);
                        break;
                    case 4:
                        handleTransactionHistory(accountNumber);
                        break;
                    case 5:
                        handlePinChange(accountNumber);
                        break;
                    case 6:
                        System.out.println("Thank you for using ATM. Please take your card.");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    
    private void showUserMenu() {
        System.out.println("\n--- ATM Services ---");
        System.out.println("1. Balance Inquiry");
        System.out.println("2. Cash Withdrawal");
        System.out.println("3. Cash Deposit");
        System.out.println("4. Transaction History");
        System.out.println("5. Change PIN");
        System.out.println("6. Exit");
        System.out.print("Choose an option: ");
    }
    
    private void handleBalanceInquiry(String accountNumber) {
        double balance = atmMachine.checkBalance(accountNumber);
        System.out.printf("Your current balance is: $%.2f%n", balance);
    }
    
    private void handleWithdrawal(String accountNumber) {
        System.out.print("Enter withdrawal amount: $");
        double amount = getDoubleInput();
        
        if (atmMachine.withdrawCash(accountNumber, amount)) {
            System.out.printf("Please take your cash: $%.2f%n", amount);
            System.out.printf("Remaining balance: $%.2f%n", atmMachine.checkBalance(accountNumber));
        } else {
            System.out.println("Withdrawal failed. Insufficient funds or invalid amount.");
        }
    }
    
    private void handleDeposit(String accountNumber) {
        System.out.print("Enter deposit amount: $");
        double amount = getDoubleInput();
        
        if (atmMachine.depositCash(accountNumber, amount)) {
            System.out.printf("Deposit successful: $%.2f%n", amount);
            System.out.printf("New balance: $%.2f%n", atmMachine.checkBalance(accountNumber));
        } else {
            System.out.println("Deposit failed. Invalid amount.");
        }
    }
    
    private void handleTransactionHistory(String accountNumber) {
        System.out.println("\n--- Transaction History ---");
        atmMachine.printTransactionHistory(accountNumber);
    }
    
    private void handlePinChange(String accountNumber) {
        System.out.print("Enter current PIN: ");
        String currentPin = scanner.nextLine();
        
        System.out.print("Enter new PIN: ");
        String newPin = scanner.nextLine();
        
        System.out.print("Confirm new PIN: ");
        String confirmPin = scanner.nextLine();
        
        if (!newPin.equals(confirmPin)) {
            System.out.println("PIN confirmation doesn't match. Please try again.");
            return;
        }
        
        if (atmMachine.changePin(accountNumber, currentPin, newPin)) {
            System.out.println("PIN changed successfully!");
        } else {
            System.out.println("PIN change failed. Current PIN is incorrect.");
        }
    }
    
    private int getIntInput() {
        while (true) {
            try {
                String input = scanner.nextLine();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }
    
    private double getDoubleInput() {
        while (true) {
            try {
                String input = scanner.nextLine();
                double value = Double.parseDouble(input);
                if (value < 0) {
                    System.out.print("Please enter a positive amount: ");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid amount: ");
            }
        }
    }
    
    public static void main(String[] args) {
        ATMSystem atmSystem = new ATMSystem();
        atmSystem.start();
    }
} 