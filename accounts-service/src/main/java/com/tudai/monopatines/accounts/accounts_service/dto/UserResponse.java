package com.tudai.monopatines.accounts.accounts_service.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO (Data Transfer Object) para la respuesta de un usuario.
 * 
 * Se utiliza en todos los endpoints GET, POST y PUT para retornar los datos
 * del usuario al cliente. Separa la capa de persistencia (entidad JPA) de
 * la capa de presentación (API REST), evitando exponer anotaciones JPA y
 * relaciones directamente en la respuesta HTTP.
 * 
 */
public class UserResponse {

    /**
     * ID único del usuario en la base de datos.
     */
    private Long id;

    /**
     * Nombre del usuario.
     */
    private String firstName;

    /**
     * Apellido del usuario.
     */
    private String lastName;

    /**
     * Email único del usuario.
     */
    private String email;

    /**
     * Número de teléfono del usuario.
     */
    private String phoneNumber;

    /**
     * Fecha y hora de creación del usuario.
     */
    private LocalDateTime createdAt;

    /**
     * Lista de roles asignados al usuario (opcional).
     * Solo se incluye cuando se solicita explícitamente (ej: para auth-service).
     * Ejemplos: ["ROLE_USER", "ROLE_ADMIN"]
     */
    private List<String> roles;

    // Constructors
    public UserResponse() {
    }

    public UserResponse(Long id, String firstName, String lastName, String email, 
                       String phoneNumber, LocalDateTime createdAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
    }

    public UserResponse(Long id, String firstName, String lastName, String email, 
                       String phoneNumber, LocalDateTime createdAt, List<String> roles) {
        this(id, firstName, lastName, email, phoneNumber, createdAt);
        this.roles = roles;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}

