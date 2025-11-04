package com.tudai.monopatines.accounts.accounts_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Entidad que representa un rol del sistema.
 * Los roles disponibles son: ROLE_USER, ROLE_EMPLOYEE, ROLE_ADMIN.
 * 
 * Un usuario puede tener múltiples roles, y un rol puede estar
 * asignado a múltiples usuarios (relación many-to-many).
 * 
 */
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del rol. Debe ser único en el sistema.
     * Ejemplos: "ROLE_USER", "ROLE_EMPLOYEE", "ROLE_ADMIN"
     */
    @Column(name = "name", unique = true, nullable = false)
    @NotBlank(message = "El nombre del rol es requerido")
    private String name;

    /**
     * Descripción del rol.
     */
    @Column(name = "description")
    private String description;

    // Constructors
    public Role() {
    }

    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Role(String name) {
        this.name = name;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

