package com.tudai.monopatines.accounts.accounts_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Entidad que representa un usuario del servicio de monopatines.
 * Un usuario puede estar asociado a varias cuentas y utilizar los
 * créditos cargados en cualquiera de las cuentas a las que pertenece.
 * 
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del usuario.
     */
    @Column(name = "first_name", nullable = false)
    @NotBlank(message = "El nombre es requerido")
    private String firstName;

    /**
     * Apellido del usuario.
     */
    @Column(name = "last_name", nullable = false)
    @NotBlank(message = "El apellido es requerido")
    private String lastName;

    /**
     * Email válido del usuario.
     * Debe ser único en el sistema.
     */
    @Column(name = "email", unique = true, nullable = false)
    @NotBlank(message = "El email es requerido")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    /**
     * Número de celular del usuario.
     */
    @Column(name = "phone_number", nullable = false)
    @NotBlank(message = "El número de celular es requerido")
    private String phoneNumber;

    /**
     * Contraseña del usuario (hasheada con BCrypt).
     * Se utiliza para autenticación en el auth-service.
     * Se guarda en la base de datos pero el hasheo se maneja en auth-service.
     */
    @Column(name = "password")
    private String password;

    /**
     * Fecha y hora de alta del usuario en el sistema.
     */
    @Column(name = "created_at", nullable = false)
    @NotNull(message = "La fecha de alta es requerida")
    private LocalDateTime createdAt;

    // Constructors
    public User() {
        this.createdAt = LocalDateTime.now();
    }

    public User(String firstName, String lastName, String email, String phoneNumber) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public User(String firstName, String lastName, String email, String phoneNumber, String password) {
        this(firstName, lastName, email, phoneNumber);
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}

