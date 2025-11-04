package com.tudai.monopatines.accounts.accounts_service.exception;

/**
 * Excepción lanzada cuando no se encuentra un usuario en el sistema.
 * 
 */
public class UserNotFoundException extends RuntimeException {
    
    public UserNotFoundException(String message) {
        super(message);
    }
    
    public UserNotFoundException(Long id) {
        super("User with id " + id + " not found");
    }
}

