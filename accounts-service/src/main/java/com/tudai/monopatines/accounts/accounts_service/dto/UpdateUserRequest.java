package com.tudai.monopatines.accounts.accounts_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO (Data Transfer Object) para actualizar un usuario existente.
 * 
 * Se utiliza en el endpoint PUT para recibir los datos actualizados del usuario.
 * No incluye password porque la password se cambia desde auth-service.
 * Las validaciones se realizan automáticamente con @Valid en el controller.
 * 
 */
public class UpdateUserRequest {

    /**
     * Nombre del usuario.
     * No puede estar vacío.
     */
    @NotBlank(message = "First name is required")
    private String firstName;

    /**
     * Apellido del usuario.
     * No puede estar vacío.
     */
    @NotBlank(message = "Last name is required")
    private String lastName;

    /**
     * Email del usuario.
     * Debe ser único en el sistema y tener un formato de email válido.
     * No puede estar vacío.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    /**
     * Número de teléfono del usuario.
     * No puede estar vacío.
     */
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    // Constructors
    public UpdateUserRequest() {
    }

    public UpdateUserRequest(String firstName, String lastName, String email, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters
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
}

