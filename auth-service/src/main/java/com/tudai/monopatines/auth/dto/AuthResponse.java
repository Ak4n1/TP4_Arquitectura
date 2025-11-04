package com.tudai.monopatines.auth.dto;

import java.util.List;

/**
 * DTO para respuesta de autenticacion.
 * Contiene informacion del usuario autenticado y sus roles.
 * Los tokens JWT se envian mediante cookies HTTP-only.
 * 
 */
public class AuthResponse {

    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private List<String> roles;

    public AuthResponse() {
    }

    public AuthResponse(Long userId, String email, String firstName, String lastName, List<String> roles) {
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = roles;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}

