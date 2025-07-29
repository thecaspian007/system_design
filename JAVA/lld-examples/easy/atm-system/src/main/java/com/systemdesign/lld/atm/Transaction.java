package com.systemdesign.lld.atm;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Transaction class representing individual transactions
 */
public class Transaction {
    private static int transactionCounter = 1;
    
    private String transactionId;
    private TransactionType type;
    private double amount;
    private String description;
    private LocalDateTime timestamp;
    
    public Transaction(TransactionType type, double amount, String description) {
        this.transactionId = "TXN" + String.format("%06d", transactionCounter++);
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public TransactionType getType() {
        return type;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public String getFormattedTimestamp() {
        return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    @Override
    public String toString() {
        return String.format("%s | %s | %s | $%.2f | %s",
                transactionId,
                getFormattedTimestamp(),
                type.getDisplayName(),
                amount,
                description);
    }
} 