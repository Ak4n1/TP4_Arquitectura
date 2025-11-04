package com.tudai.monopatines.accounts.accounts_service.exception;

/**
 * Excepción lanzada cuando se intenta descontar un saldo mayor al disponible.
 * 
 */
public class InsufficientBalanceException extends RuntimeException {
    
    public InsufficientBalanceException(String message) {
        super(message);
    }
    
    public InsufficientBalanceException(Double currentBalance, Double requestedAmount) {
        super("Insufficient balance. Current balance: " + currentBalance + ", Requested amount: " + requestedAmount);
    }
}

