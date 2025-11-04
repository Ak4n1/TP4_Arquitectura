package com.tudai.monopatines.accounts.accounts_service.dto;

import java.util.List;

/**
 * DTO (Data Transfer Object) para la respuesta de cuentas asociadas a un usuario.
 * 
 * Se utiliza en el endpoint GET /api/accounts/users/{userId}/accounts para retornar
 * la lista de cuentas asociadas a un usuario, junto con un mensaje informativo.
 * 
 */
public class AccountsByUserResponse {

    /**
     * ID del usuario.
     */
    private Long userId;

    /**
     * Lista de cuentas asociadas al usuario.
     */
    private List<AccountResponse> accounts;

    /**
     * Mensaje informativo sobre el resultado.
     */
    private String message;

    /**
     * Cantidad de cuentas asociadas.
     */
    private Integer count;

    // Constructors
    public AccountsByUserResponse() {
    }

    public AccountsByUserResponse(Long userId, List<AccountResponse> accounts, String message) {
        this.userId = userId;
        this.accounts = accounts;
        this.message = message;
        this.count = accounts != null ? accounts.size() : 0;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<AccountResponse> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountResponse> accounts) {
        this.accounts = accounts;
        this.count = accounts != null ? accounts.size() : 0;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}

