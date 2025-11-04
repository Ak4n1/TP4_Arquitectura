package com.tudai.monopatines.accounts.accounts_service.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la entidad User.
 * 
 */
@DisplayName("Tests de la entidad User")
class UserTest {

    @Test
    @DisplayName("Deberia crear un usuario valido")
    void deberiaCrearUsuarioValido() {
        // Given & When
        User user = new User("Juan", "Perez", "juan.perez@example.com", "+5491112345678");

        // Then
        assertNotNull(user);
        assertEquals("Juan", user.getFirstName());
        assertEquals("Perez", user.getLastName());
        assertEquals("juan.perez@example.com", user.getEmail());
        assertEquals("+5491112345678", user.getPhoneNumber());
        assertNotNull(user.getCreatedAt());
    }

    @Test
    @DisplayName("Deberia inicializar con fecha de alta automatica")
    void deberiaInicializarConFechaAltaAutomatica() {
        // Given & When
        User user = new User();

        // Then
        assertNotNull(user.getCreatedAt());
    }


    @Test
    @DisplayName("Deberia tener un toString con informacion relevante")
    void deberiaTenerToStringConInformacionRelevante() {
        // Given
        User user = new User("Juan", "Perez", "juan.perez@example.com", "+5491112345678");

        // When
        String toString = user.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("Juan"));
        assertTrue(toString.contains("Perez"));
        assertTrue(toString.contains("juan.perez@example.com"));
    }
}

