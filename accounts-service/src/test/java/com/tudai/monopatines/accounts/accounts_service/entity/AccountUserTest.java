package com.tudai.monopatines.accounts.accounts_service.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la entidad AccountUser (relacion many-to-many).
 * 
 */
@DisplayName("Tests de la entidad AccountUser")
class AccountUserTest {

    private Account account;
    private User user;

    @BeforeEach
    void setUp() {
        account = new Account("CUENTA-001", "MP-12345");
        user = new User("Juan", "Perez", "juan.perez@example.com", "+5491112345678");
    }

    @Test
    @DisplayName("Deberia crear una relacion AccountUser valida")
    void deberiaCrearRelacionAccountUserValida() {
        // Given & When
        AccountUser accountUser = new AccountUser(account, user);

        // Then
        assertNotNull(accountUser);
        assertEquals(account, accountUser.getAccount());
        assertEquals(user, accountUser.getUser());
        assertNotNull(accountUser.getAssociatedAt());
    }

    @Test
    @DisplayName("Deberia inicializar con fecha de asociacion automatica")
    void deberiaInicializarConFechaAsociacionAutomatica() {
        // Given & When
        AccountUser accountUser = new AccountUser();

        // Then
        assertNotNull(accountUser.getAssociatedAt());
    }


    @Test
    @DisplayName("Deberia permitir asociar un usuario a una cuenta")
    void deberiaPermitirAsociarUsuarioACuenta() {
        // Given
        AccountUser accountUser = new AccountUser();

        // When
        accountUser.setAccount(account);
        accountUser.setUser(user);

        // Then
        assertEquals(account, accountUser.getAccount());
        assertEquals(user, accountUser.getUser());
    }

    @Test
    @DisplayName("Deberia permitir cambiar la fecha de asociacion")
    void deberiaPermitirCambiarFechaAsociacion() {
        // Given
        AccountUser accountUser = new AccountUser(account, user);
        java.time.LocalDateTime nuevaFecha = java.time.LocalDateTime.now().minusDays(1);

        // When
        accountUser.setAssociatedAt(nuevaFecha);

        // Then
        assertEquals(nuevaFecha, accountUser.getAssociatedAt());
    }

    @Test
    @DisplayName("Deberia tener un toString con informacion relevante")
    void deberiaTenerToStringConInformacionRelevante() {
        // Given
        AccountUser accountUser = new AccountUser(account, user);

        // When
        String toString = accountUser.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("AccountUser"));
    }
}

