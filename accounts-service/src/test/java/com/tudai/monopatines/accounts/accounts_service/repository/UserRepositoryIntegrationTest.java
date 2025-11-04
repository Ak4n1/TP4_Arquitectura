package com.tudai.monopatines.accounts.accounts_service.repository;

import com.tudai.monopatines.accounts.accounts_service.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de integracion para UserRepository.
 * Usa Spring Boot Test con H2 en memoria para probar el repositorio.
 * 
 */
@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("Tests de integracion - UserRepository")
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("Juan", "Perez", "juan.perez@example.com", "+5491112345678");
        testUser.setPassword("hashedPassword123");
    }

    @Test
    @DisplayName("Deberia guardar un usuario en la base de datos")
    void deberiaGuardarUsuarioEnBaseDeDatos() {
        User savedUser = userRepository.save(testUser);

        assertNotNull(savedUser.getId());
        assertEquals("Juan", savedUser.getFirstName());
        assertEquals("Perez", savedUser.getLastName());
        assertEquals("juan.perez@example.com", savedUser.getEmail());
        assertEquals("+5491112345678", savedUser.getPhoneNumber());
        assertNotNull(savedUser.getCreatedAt());
    }

    @Test
    @DisplayName("Deberia encontrar un usuario por ID")
    void deberiaEncontrarUsuarioPorId() {
        User savedUser = userRepository.save(testUser);

        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertTrue(foundUser.isPresent());
        assertEquals("juan.perez@example.com", foundUser.get().getEmail());
        assertEquals(savedUser.getId(), foundUser.get().getId());
    }

    @Test
    @DisplayName("Deberia encontrar un usuario por email")
    void deberiaEncontrarUsuarioPorEmail() {
        userRepository.save(testUser);

        Optional<User> foundUser = userRepository.findByEmail("juan.perez@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals("Juan", foundUser.get().getFirstName());
        assertEquals("Perez", foundUser.get().getLastName());
    }

    @Test
    @DisplayName("Deberia verificar si existe un usuario por email")
    void deberiaVerificarSiExisteUsuarioPorEmail() {
        userRepository.save(testUser);

        boolean exists = userRepository.existsByEmail("juan.perez@example.com");

        assertTrue(exists);
    }

    @Test
    @DisplayName("Deberia retornar false si no existe un usuario por email")
    void deberiaRetornarFalseSiNoExisteUsuarioPorEmail() {
        boolean exists = userRepository.existsByEmail("noexiste@example.com");

        assertFalse(exists);
    }

    @Test
    @DisplayName("Deberia encontrar todos los usuarios")
    void deberiaEncontrarTodosLosUsuarios() {
        User user1 = new User("Juan", "Perez", "juan.perez@example.com", "+5491112345678");
        User user2 = new User("Maria", "Gonzalez", "maria.gonzalez@example.com", "+5491198765432");
        User user3 = new User("Pedro", "Lopez", "pedro.lopez@example.com", "+5491122334455");

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        List<User> allUsers = userRepository.findAll();

        assertTrue(allUsers.size() >= 3);
        boolean found1 = false;
        boolean found2 = false;
        boolean found3 = false;
        for (User user : allUsers) {
            if (user.getEmail().equals("juan.perez@example.com")) {
                found1 = true;
            }
            if (user.getEmail().equals("maria.gonzalez@example.com")) {
                found2 = true;
            }
            if (user.getEmail().equals("pedro.lopez@example.com")) {
                found3 = true;
            }
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
    }

    @Test
    @DisplayName("Deberia actualizar un usuario existente")
    void deberiaActualizarUsuarioExistente() {
        User savedUser = userRepository.save(testUser);
        savedUser.setFirstName("Juan Updated");
        savedUser.setLastName("Perez Updated");

        User updatedUser = userRepository.save(savedUser);

        assertEquals(savedUser.getId(), updatedUser.getId());
        assertEquals("Juan Updated", updatedUser.getFirstName());
        assertEquals("Perez Updated", updatedUser.getLastName());
    }

    @Test
    @DisplayName("Deberia eliminar un usuario")
    void deberiaEliminarUsuario() {
        User savedUser = userRepository.save(testUser);
        Long userId = savedUser.getId();

        userRepository.deleteById(userId);

        Optional<User> deletedUser = userRepository.findById(userId);
        assertFalse(deletedUser.isPresent());
    }
}

