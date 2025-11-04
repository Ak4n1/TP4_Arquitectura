package com.tudai.monopatines.accounts.accounts_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;

/**
 * Entidad que representa una cuenta del servicio de monopatines.
 * Una cuenta está asociada a una cuenta de Mercado Pago y puede tener
 * varios usuarios asociados que utilizarán los créditos cargados en la cuenta.
 * 
 */
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Número identificatorio único de la cuenta del servicio.
     */
    @Column(name = "identification_number", unique = true, nullable = false)
    @NotNull(message = "El número identificatorio es requerido")
    private String identificationNumber;

    /**
     * ID de la cuenta de Mercado Pago asociada.
     * Puede repetirse ya que una cuenta de Mercado Pago puede estar
     * asociada a varias cuentas del servicio.
     */
    @Column(name = "mercado_pago_account_id", nullable = false)
    @NotNull(message = "El ID de cuenta de Mercado Pago es requerido")
    private String mercadoPagoAccountId;

    /**
     * Saldo actual de créditos en la cuenta.
     * Se va descontando en función del tiempo de uso del monopatín.
     */
    @Column(name = "current_balance", nullable = false)
    @NotNull(message = "El saldo actual es requerido")
    @PositiveOrZero(message = "El saldo no puede ser negativo")
    private Double currentBalance;

    /**
     * Indica si la cuenta está activa o anulada.
     * Si está anulada, no se pueden iniciar nuevos viajes.
     */
    @Column(name = "active", nullable = false)
    @NotNull(message = "El estado activo es requerido")
    private Boolean active;

    /**
     * Fecha y hora de alta de la cuenta en el sistema.
     */
    @Column(name = "created_at", nullable = false)
    @NotNull(message = "La fecha de alta es requerida")
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de anulación de la cuenta (si fue anulada).
     * Null si la cuenta nunca fue anulada.
     */
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    // Constructors
    public Account() {
        this.active = true;
        this.currentBalance = 0.0;
        this.createdAt = LocalDateTime.now();
    }

    public Account(String identificationNumber, String mercadoPagoAccountId) {
        this();
        this.identificationNumber = identificationNumber;
        this.mercadoPagoAccountId = mercadoPagoAccountId;
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

    /**
     * Anula la cuenta, estableciendo la fecha de anulación y marcándola como inactiva.
     */
    public void cancel() {
        this.active = false;
        this.cancelledAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", identificationNumber='" + identificationNumber + '\'' +
                ", mercadoPagoAccountId='" + mercadoPagoAccountId + '\'' +
                ", currentBalance=" + currentBalance +
                ", active=" + active +
                ", createdAt=" + createdAt +
                ", cancelledAt=" + cancelledAt +
                '}';
    }
}

