package com.tudai.monopatines.accounts.accounts_service.exception;

/**
 * Excepción lanzada cuando no se encuentra una cuenta en el sistema.
 * 
 */
public class AccountNotFoundException extends RuntimeException {
    
    public AccountNotFoundException(String message) {
        super(message);
    }
    
    public AccountNotFoundException(Long id) {
        super("Account with id " + id + " not found");
    }
}

