package com.tudai.monopatines.accounts.accounts_service.dto;

/**
 * DTO (Data Transfer Object) para la respuesta del saldo de una cuenta.
 * 
 * Se utiliza en los endpoints relacionados con operaciones de saldo
 * (cargar, descontar, consultar) para retornar el saldo actualizado
 * de la cuenta al cliente.
 * 
 */
public class BalanceResponse {

    /**
     * ID de la cuenta cuyo saldo se está consultando o modificando.
     */
    private Long accountId;

    /**
     * Saldo actual de la cuenta después de la operación.
     */
    private Double currentBalance;

    // Constructors
    public BalanceResponse() {
    }

    public BalanceResponse(Long accountId, Double currentBalance) {
        this.accountId = accountId;
        this.currentBalance = currentBalance;
    }

    // Getters and Setters
    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Double currentBalance) {
        this.currentBalance = currentBalance;
    }
}

