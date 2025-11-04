package com.tudai.monopatines.accounts.accounts_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO (Data Transfer Object) para crear un usuario nuevo.
 * 
 * Se utiliza en el endpoint POST para recibir los datos del usuario a crear.
 * Incluye password (hasheado) que es obligatorio al crear un usuario.
 * Las validaciones se realizan automáticamente con @Valid en el controller.
 * 
 */
public class CreateUserRequest {

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

    /**
     * Contraseña del usuario (ya hasheada con BCrypt).
     * Obligatoria: siempre debe venir hasheada desde auth-service.
     * El hasheo se realiza en auth-service, este servicio solo guarda el hash.
     * No puede estar vacía.
     */
    @NotBlank(message = "Password is required")
    private String password;

    // Constructors
    public CreateUserRequest() {
    }

    public CreateUserRequest(String firstName, String lastName, String email, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public CreateUserRequest(String firstName, String lastName, String email, String phoneNumber, String password) {
        this(firstName, lastName, email, phoneNumber);
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

