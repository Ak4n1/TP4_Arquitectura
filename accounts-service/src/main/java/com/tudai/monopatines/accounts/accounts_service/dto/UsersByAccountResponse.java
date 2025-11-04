package com.tudai.monopatines.accounts.accounts_service.dto;

import java.util.List;

/**
 * DTO (Data Transfer Object) para la respuesta de usuarios asociados a una cuenta.
 * 
 * Se utiliza en el endpoint GET /api/accounts/{accountId}/users para retornar
 * la lista de usuarios asociados a una cuenta, junto con un mensaje informativo.
 * 
 */
public class UsersByAccountResponse {

    /**
     * ID de la cuenta.
     */
    private Long accountId;

    /**
     * Lista de usuarios asociados a la cuenta.
     */
    private List<UserResponse> users;

    /**
     * Mensaje informativo sobre el resultado.
     */
    private String message;

    /**
     * Cantidad de usuarios asociados.
     */
    private Integer count;

    // Constructors
    public UsersByAccountResponse() {
    }

    public UsersByAccountResponse(Long accountId, List<UserResponse> users, String message) {
        this.accountId = accountId;
        this.users = users;
        this.message = message;
        this.count = users != null ? users.size() : 0;
    }

    // Getters and Setters
    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public List<UserResponse> getUsers() {
        return users;
    }

    public void setUsers(List<UserResponse> users) {
        this.users = users;
        this.count = users != null ? users.size() : 0;
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

