package com.tudai.monopatines.accounts.accounts_service.dto;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para la respuesta de una asociación cuenta-usuario.
 * 
 * Se utiliza en los endpoints POST y DELETE de asociación/desasociación para
 * retornar información sobre la relación creada o eliminada.
 * 
 */
public class AccountUserResponse {

    /**
     * ID de la cuenta involucrada en la asociación.
     */
    private Long accountId;

    /**
     * ID del usuario involucrado en la asociación.
     */
    private Long userId;

    /**
     * Fecha y hora en que se asoció el usuario a la cuenta.
     */
    private LocalDateTime associatedAt;

    /**
     * Mensaje descriptivo sobre la operación realizada.
     */
    private String message;

    // Constructors
    public AccountUserResponse() {
    }

    public AccountUserResponse(Long accountId, Long userId, LocalDateTime associatedAt, String message) {
        this.accountId = accountId;
        this.userId = userId;
        this.associatedAt = associatedAt;
        this.message = message;
    }

    // Getters and Setters
    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getAssociatedAt() {
        return associatedAt;
    }

    public void setAssociatedAt(LocalDateTime associatedAt) {
        this.associatedAt = associatedAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

