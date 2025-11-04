package com.tudai.monopatines.accounts.accounts_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO (Data Transfer Object) para cargar saldo a una cuenta.
 * 
 * Se utiliza en el endpoint PUT /api/accounts/{id}/balance para recibir
 * el monto a cargar desde el cliente. Las validaciones se realizan automáticamente
 * con @Valid en el controller.
 * 
 */
public class BalanceRequest {

    /**
     * Monto a cargar en la cuenta.
     * Debe ser un valor positivo (mayor que cero) y no puede ser null.
     */
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;

    // Constructors
    public BalanceRequest() {
    }

    public BalanceRequest(Double amount) {
        this.amount = amount;
    }

    // Getters and Setters
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}

