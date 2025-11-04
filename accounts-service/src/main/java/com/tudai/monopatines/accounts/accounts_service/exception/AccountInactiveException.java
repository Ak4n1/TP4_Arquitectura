package com.tudai.monopatines.accounts.accounts_service.exception;

/**
 * Excepción lanzada cuando se intenta realizar una operación en una cuenta inactiva (anulada).
 * 
 */
public class AccountInactiveException extends RuntimeException {
    
    public AccountInactiveException(String message) {
        super(message);
    }
    
    public AccountInactiveException(Long accountId) {
        super("Account with id " + accountId + " is inactive (cancelled)");
    }
}

