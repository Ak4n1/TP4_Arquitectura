package com.tudai.monopatines.accounts.accounts_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Entidad intermedia que representa la relación many-to-many entre
 * Cuentas y Usuarios.
 * 
 * Una cuenta puede tener varios usuarios asociados que utilizarán
 * los créditos cargados en la cuenta, y un usuario puede estar
 * asociado a más de una cuenta.
 * 
 */
@Entity
@Table(name = "account_user", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "user_id"}))
public class AccountUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Cuenta asociada.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    @NotNull(message = "La cuenta es requerida")
    private Account account;

    /**
     * Usuario asociado.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "El usuario es requerido")
    private User user;

    /**
     * Fecha y hora en que se asoció el usuario a la cuenta.
     */
    @Column(name = "associated_at", nullable = false)
    @NotNull(message = "La fecha de asociación es requerida")
    private LocalDateTime associatedAt;

    // Constructors
    public AccountUser() {
        this.associatedAt = LocalDateTime.now();
    }

    public AccountUser(Account account, User user) {
        this();
        this.account = account;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getAssociatedAt() {
        return associatedAt;
    }

    public void setAssociatedAt(LocalDateTime associatedAt) {
        this.associatedAt = associatedAt;
    }

    @Override
    public String toString() {
        return "AccountUser{" +
                "id=" + id +
                ", accountId=" + (account != null ? account.getId() : null) +
                ", userId=" + (user != null ? user.getId() : null) +
                ", associatedAt=" + associatedAt +
                '}';
    }
}

