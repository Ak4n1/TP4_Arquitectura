package com.tudai.monopatines.accounts.accounts_service.dto;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para la respuesta de una cuenta.
 * 
 * Se utiliza en todos los endpoints GET, POST y PUT para retornar los datos
 * de la cuenta al cliente. Separa la capa de persistencia (entidad JPA) de
 * la capa de presentación (API REST), evitando exponer anotaciones JPA y
 * relaciones directamente en la respuesta HTTP.
 * 
 */
public class AccountResponse {

    /**
     * ID único de la cuenta en la base de datos.
     */
    private Long id;

    /**
     * Número identificatorio único de la cuenta.
     */
    private String identificationNumber;

    /**
     * ID de la cuenta de Mercado Pago asociada.
     */
    private String mercadoPagoAccountId;

    /**
     * Saldo actual de la cuenta.
     */
    private Double currentBalance;

    /**
     * Indica si la cuenta está activa (true) o anulada (false).
     */
    private Boolean active;

    /**
     * Fecha y hora de creación de la cuenta.
     */
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de anulación de la cuenta (null si la cuenta está activa).
     */
    private LocalDateTime cancelledAt;

    // Constructors
    public AccountResponse() {
    }

    public AccountResponse(Long id, String identificationNumber, String mercadoPagoAccountId,
                          Double currentBalance, Boolean active, LocalDateTime createdAt, LocalDateTime cancelledAt) {
        this.id = id;
        this.identificationNumber = identificationNumber;
        this.mercadoPagoAccountId = mercadoPagoAccountId;
        this.currentBalance = currentBalance;
        this.active = active;
        this.createdAt = createdAt;
        this.cancelledAt = cancelledAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }
}

