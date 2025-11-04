package com.tudai.monopatines.accounts.accounts_service.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la entidad Account.
 * 
 */
@DisplayName("Tests de la entidad Account")
class AccountTest {

    @Test
    @DisplayName("Deberia crear una cuenta valida")
    void deberiaCrearCuentaValida() {
        // Given & When
        Account account = new Account("CUENTA-001", "MP-12345");

        // Then
        assertNotNull(account);
        assertEquals("CUENTA-001", account.getIdentificationNumber());
        assertEquals("MP-12345", account.getMercadoPagoAccountId());
        assertEquals(0.0, account.getCurrentBalance());
        assertTrue(account.getActive());
        assertNotNull(account.getCreatedAt());
        assertNull(account.getCancelledAt());
    }

    @Test
    @DisplayName("Deberia inicializar con valores por defecto")
    void deberiaInicializarConValoresPorDefecto() {
        // Given & When
        Account account = new Account();

        // Then
        assertTrue(account.getActive());
        assertEquals(0.0, account.getCurrentBalance());
        assertNotNull(account.getCreatedAt());
    }


    @Test
    @DisplayName("Deberia anular una cuenta correctamente")
    void deberiaAnularCuentaCorrectamente() {
        // Given
        Account account = new Account("CUENTA-001", "MP-12345");
        assertTrue(account.getActive());
        assertNull(account.getCancelledAt());

        // When
        account.cancel();

        // Then
        assertFalse(account.getActive());
        assertNotNull(account.getCancelledAt());
        assertTrue(account.getCancelledAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @DisplayName("Deberia permitir actualizar saldo")
    void deberiaPermitirActualizarSaldo() {
        // Given
        Account account = new Account("CUENTA-001", "MP-12345");
        assertEquals(0.0, account.getCurrentBalance());

        // When
        account.setCurrentBalance(1000.0);

        // Then
        assertEquals(1000.0, account.getCurrentBalance());
    }

    @Test
    @DisplayName("Deberia tener un toString con informacion relevante")
    void deberiaTenerToStringConInformacionRelevante() {
        // Given
        Account account = new Account("CUENTA-001", "MP-12345");

        // When
        String toString = account.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("CUENTA-001"));
        assertTrue(toString.contains("MP-12345"));
    }
}

