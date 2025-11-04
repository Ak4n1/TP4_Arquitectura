package com.tudai.monopatines.accounts.accounts_service.service;

import com.tudai.monopatines.accounts.accounts_service.dto.CreateUserRequest;
import com.tudai.monopatines.accounts.accounts_service.dto.UpdateUserRequest;
import com.tudai.monopatines.accounts.accounts_service.dto.UserResponse;
import com.tudai.monopatines.accounts.accounts_service.exception.UserAlreadyExistsException;
import com.tudai.monopatines.accounts.accounts_service.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de integracion para UserService.
 * Usa Spring Boot Test con H2 en memoria para probar el servicio completo.
 * 
 */
@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("Tests de integracion - UserService")
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    private CreateUserRequest testCreateUserRequest;

    @BeforeEach
    void setUp() {
        testCreateUserRequest = new CreateUserRequest();
        testCreateUserRequest.setFirstName("Juan");
        testCreateUserRequest.setLastName("Perez");
        testCreateUserRequest.setEmail("juan.perez@example.com");
        testCreateUserRequest.setPhoneNumber("+5491112345678");
        testCreateUserRequest.setPassword("hashedPassword123");
    }

    @Test
    @DisplayName("Deberia crear un usuario exitosamente")
    void deberiaCrearUsuarioExitosamente() {
        UserResponse response = userService.createUser(testCreateUserRequest);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("Juan", response.getFirstName());
        assertEquals("Perez", response.getLastName());
        assertEquals("juan.perez@example.com", response.getEmail());
        assertEquals("+5491112345678", response.getPhoneNumber());
        assertNotNull(response.getRoles());
        assertTrue(response.getRoles().contains("ROLE_USER"));
    }

    @Test
    @DisplayName("Deberia lanzar excepcion si el email ya existe")
    void deberiaLanzarExcepcionSiEmailYaExiste() {
        userService.createUser(testCreateUserRequest);

        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.createUser(testCreateUserRequest);
        });
    }

    @Test
    @DisplayName("Deberia obtener un usuario por ID")
    void deberiaObtenerUsuarioPorId() {
        UserResponse createdUser = userService.createUser(testCreateUserRequest);

        UserResponse foundUser = userService.getUserById(createdUser.getId());

        assertNotNull(foundUser);
        assertEquals(createdUser.getId(), foundUser.getId());
        assertEquals("juan.perez@example.com", foundUser.getEmail());
        assertNotNull(foundUser.getRoles());
    }

    @Test
    @DisplayName("Deberia lanzar excepcion si el usuario no existe")
    void deberiaLanzarExcepcionSiUsuarioNoExiste() {
        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserById(999L);
        });
    }

    @Test
    @DisplayName("Deberia obtener un usuario por email")
    void deberiaObtenerUsuarioPorEmail() {
        userService.createUser(testCreateUserRequest);

        UserResponse foundUser = userService.getUserByEmail("juan.perez@example.com");

        assertNotNull(foundUser);
        assertEquals("Juan", foundUser.getFirstName());
        assertEquals("Perez", foundUser.getLastName());
        assertNotNull(foundUser.getRoles());
    }

    @Test
    @DisplayName("Deberia obtener todos los usuarios")
    void deberiaObtenerTodosLosUsuarios() {
        CreateUserRequest user1 = new CreateUserRequest();
        user1.setFirstName("Juan");
        user1.setLastName("Perez");
        user1.setEmail("juan.perez@example.com");
        user1.setPhoneNumber("+5491112345678");
        user1.setPassword("pass1");

        CreateUserRequest user2 = new CreateUserRequest();
        user2.setFirstName("Maria");
        user2.setLastName("Gonzalez");
        user2.setEmail("maria.gonzalez@example.com");
        user2.setPhoneNumber("+5491198765432");
        user2.setPassword("pass2");

        userService.createUser(user1);
        userService.createUser(user2);

        List<UserResponse> allUsers = userService.getAllUsers();

        assertTrue(allUsers.size() >= 2);
    }

    @Test
    @DisplayName("Deberia actualizar un usuario exitosamente")
    void deberiaActualizarUsuarioExitosamente() {
        UserResponse createdUser = userService.createUser(testCreateUserRequest);
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setFirstName("Juan Updated");
        updateRequest.setLastName("Perez Updated");
        updateRequest.setEmail("juan.perez@example.com");
        updateRequest.setPhoneNumber("+5491112345678");

        UserResponse updatedUser = userService.updateUser(createdUser.getId(), updateRequest);

        assertNotNull(updatedUser);
        assertEquals(createdUser.getId(), updatedUser.getId());
        assertEquals("Juan Updated", updatedUser.getFirstName());
        assertEquals("Perez Updated", updatedUser.getLastName());
    }

    @Test
    @DisplayName("Deberia lanzar excepcion si intenta actualizar email que ya existe")
    void deberiaLanzarExcepcionSiIntentaActualizarEmailQueYaExiste() {
        userService.createUser(testCreateUserRequest);

        CreateUserRequest user2 = new CreateUserRequest();
        user2.setFirstName("Maria");
        user2.setLastName("Gonzalez");
        user2.setEmail("maria.gonzalez@example.com");
        user2.setPhoneNumber("+5491198765432");
        user2.setPassword("pass2");
        UserResponse createdUser2 = userService.createUser(user2);

        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setFirstName("Maria");
        updateRequest.setLastName("Gonzalez");
        updateRequest.setEmail("juan.perez@example.com");
        updateRequest.setPhoneNumber("+5491198765432");

        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.updateUser(createdUser2.getId(), updateRequest);
        });
    }

    @Test
    @DisplayName("Deberia eliminar un usuario")
    void deberiaEliminarUsuario() {
        UserResponse createdUser = userService.createUser(testCreateUserRequest);

        userService.deleteUser(createdUser.getId());

        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserById(createdUser.getId());
        });
    }
}

