package com.systemdesign.lld.atm;

import java.util.HashMap;
import java.util.Map;

/**
 * Bank class that manages multiple accounts
 */
public class Bank {
    private Map<String, Account> accounts;
    
    public Bank() {
        this.accounts = new HashMap<>();
    }
    
    public void addAccount(Account account) {
        accounts.put(account.getAccountNumber(), account);
    }
    
    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }
    
    public boolean authenticateAccount(String accountNumber, String pin) {
        Account account = getAccount(accountNumber);
        return account != null && account.isActive() && account.validatePin(pin);
    }
    
    public boolean accountExists(String accountNumber) {
        return accounts.containsKey(accountNumber);
    }
    
    public int getTotalAccounts() {
        return accounts.size();
    }
    
    public double getTotalBalance() {
        return accounts.values().stream()
                .mapToDouble(Account::getBalance)
                .sum();
    }
    
    public void deactivateAccount(String accountNumber) {
        Account account = getAccount(accountNumber);
        if (account != null) {
            account.setActive(false);
        }
    }
    
    public void activateAccount(String accountNumber) {
        Account account = getAccount(accountNumber);
        if (account != null) {
            account.setActive(true);
        }
    }
} 