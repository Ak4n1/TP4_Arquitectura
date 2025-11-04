package com.tudai.monopatines.accounts.accounts_service.exception;

/**
 * Excepción lanzada cuando se intenta crear una cuenta con un número identificatorio que ya existe.
 * 
 */
public class AccountAlreadyExistsException extends RuntimeException {
    
    public AccountAlreadyExistsException(String message) {
        super(message);
    }
    
    public static AccountAlreadyExistsException forIdentificationNumber(String identificationNumber) {
        return new AccountAlreadyExistsException("Account with identification number " + identificationNumber + " already exists");
    }
}

