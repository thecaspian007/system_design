package com.systemdesign.lld.atm;

/**
 * Enum representing different types of transactions
 */
public enum TransactionType {
    DEPOSIT("Deposit"),
    WITHDRAWAL("Withdrawal"),
    BALANCE_INQUIRY("Balance Inquiry"),
    PIN_CHANGE("PIN Change"),
    TRANSFER("Transfer");
    
    private final String displayName;
    
    TransactionType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
} 