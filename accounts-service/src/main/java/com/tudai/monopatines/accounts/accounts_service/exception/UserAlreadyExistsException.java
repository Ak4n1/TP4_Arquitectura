package com.tudai.monopatines.accounts.accounts_service.exception;

/**
 * Excepción lanzada cuando se intenta crear un usuario con un email que ya existe.
 * 
 */
public class UserAlreadyExistsException extends RuntimeException {
    
    public UserAlreadyExistsException(String message) {
        super(message);
    }
    
    public static UserAlreadyExistsException forEmail(String email) {
        return new UserAlreadyExistsException("User with email " + email + " already exists");
    }
}

