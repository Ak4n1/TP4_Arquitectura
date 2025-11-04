package com.tudai.monopatines.accounts.accounts_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * DTO (Data Transfer Object) para crear o actualizar una cuenta.
 * 
 * Se utiliza en los endpoints POST y PUT para recibir los datos de la cuenta
 * desde el cliente. Las validaciones se realizan automáticamente con @Valid
 * en el controller.
 * 
 */
public class AccountRequest {

    /**
     * Número identificatorio único de la cuenta.
     * Debe ser único en el sistema y no puede estar vacío.
     */
    @NotBlank(message = "Identification number is required")
    private String identificationNumber;

    /**
     * ID de la cuenta de Mercado Pago asociada.
     * Una misma cuenta de Mercado Pago puede estar asociada a múltiples cuentas del servicio.
     * No puede estar vacío.
     */
    @NotBlank(message = "Mercado Pago account ID is required")
    private String mercadoPagoAccountId;

    /**
     * Saldo inicial de la cuenta.
     * Debe ser un valor positivo o cero (no puede ser negativo).
     * Si es null, se establece en 0.0 por defecto.
     */
    @NotNull(message = "Current balance is required")
    @PositiveOrZero(message = "Current balance cannot be negative")
    private Double currentBalance;

    // Constructors
    public AccountRequest() {
    }

    public AccountRequest(String identificationNumber, String mercadoPagoAccountId, Double currentBalance) {
        this.identificationNumber = identificationNumber;
        this.mercadoPagoAccountId = mercadoPagoAccountId;
        this.currentBalance = currentBalance;
    }

    // Getters and Setters
    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public String getMercadoPagoAccountId() {
        return mercadoPagoAccountId;
    }

    public void setMercadoPagoAccountId(String mercadoPagoAccountId) {
        this.mercadoPagoAccountId = mercadoPagoAccountId;
    }

    public Double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Double currentBalance) {
        this.currentBalance = currentBalance;
    }
}

