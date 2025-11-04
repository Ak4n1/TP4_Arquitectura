package com.tudai.monopatines.accounts.accounts_service.service;

import com.tudai.monopatines.accounts.accounts_service.dto.AccountRequest;
import com.tudai.monopatines.accounts.accounts_service.dto.AccountUserResponse;
import com.tudai.monopatines.accounts.accounts_service.dto.AccountsByUserResponse;
import com.tudai.monopatines.accounts.accounts_service.dto.CreateUserRequest;
import com.tudai.monopatines.accounts.accounts_service.dto.UsersByAccountResponse;
import com.tudai.monopatines.accounts.accounts_service.exception.AccountNotFoundException;
import com.tudai.monopatines.accounts.accounts_service.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de integracion para AccountUserService.
 * Usa Spring Boot Test con H2 en memoria para probar el servicio completo.
 * 
 */
@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("Tests de integracion - AccountUserService")
class AccountUserServiceIntegrationTest {

    @Autowired
    private AccountUserService accountUserService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    private Long testAccountId;
    private Long testUserId;

    @BeforeEach
    void setUp() {
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setIdentificationNumber("ACC001");
        accountRequest.setMercadoPagoAccountId("MP123456");
        accountRequest.setCurrentBalance(1000.0);
        testAccountId = accountService.createAccount(accountRequest).getId();

        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setFirstName("Juan");
        userRequest.setLastName("Perez");
        userRequest.setEmail("juan.perez@example.com");
        userRequest.setPhoneNumber("+5491112345678");
        userRequest.setPassword("hashedPassword123");
        testUserId = userService.createUser(userRequest).getId();
    }

    @Test
    @DisplayName("Deberia asociar un usuario a una cuenta exitosamente")
    void deberiaAsociarUsuarioACuentaExitosamente() {
        AccountUserResponse response = accountUserService.associateUserToAccount(testAccountId, testUserId);

        assertNotNull(response);
        assertEquals(testAccountId, response.getAccountId());
        assertEquals(testUserId, response.getUserId());
        assertNotNull(response.getAssociatedAt());
        assertEquals("Usuario asociado exitosamente a la cuenta", response.getMessage());
    }

    @Test
    @DisplayName("Deberia lanzar excepcion si la cuenta no existe al asociar")
    void deberiaLanzarExcepcionSiCuentaNoExisteAlAsociar() {
        assertThrows(AccountNotFoundException.class, () -> {
            accountUserService.associateUserToAccount(999L, testUserId);
        });
    }

    @Test
    @DisplayName("Deberia lanzar excepcion si el usuario no existe al asociar")
    void deberiaLanzarExcepcionSiUsuarioNoExisteAlAsociar() {
        assertThrows(UserNotFoundException.class, () -> {
            accountUserService.associateUserToAccount(testAccountId, 999L);
        });
    }

    @Test
    @DisplayName("Deberia desasociar un usuario de una cuenta exitosamente")
    void deberiaDesasociarUsuarioDeCuentaExitosamente() {
        accountUserService.associateUserToAccount(testAccountId, testUserId);

        AccountUserResponse response = accountUserService.disassociateUserFromAccount(testAccountId, testUserId);

        assertNotNull(response);
        assertEquals(testAccountId, response.getAccountId());
        assertEquals(testUserId, response.getUserId());
        assertEquals("Usuario desasociado exitosamente de la cuenta", response.getMessage());
    }

    @Test
    @DisplayName("Deberia obtener usuarios asociados a una cuenta")
    void deberiaObtenerUsuariosAsociadosACuenta() {
        accountUserService.associateUserToAccount(testAccountId, testUserId);

        UsersByAccountResponse response = accountUserService.getUsersByAccount(testAccountId);

        assertNotNull(response);
        assertEquals(testAccountId, response.getAccountId());
        assertNotNull(response.getUsers());
        assertTrue(response.getCount() >= 1);
        assertEquals("Usuarios asociados a la cuenta encontrados", response.getMessage());
    }

    @Test
    @DisplayName("Deberia retornar lista vacia si no hay usuarios asociados")
    void deberiaRetornarListaVaciaSiNoHayUsuariosAsociados() {
        UsersByAccountResponse response = accountUserService.getUsersByAccount(testAccountId);

        assertNotNull(response);
        assertEquals(testAccountId, response.getAccountId());
        assertNotNull(response.getUsers());
        assertEquals(0, response.getUsers().size());
        assertEquals(0, response.getCount());
        assertEquals("La cuenta no tiene usuarios asociados", response.getMessage());
    }

    @Test
    @DisplayName("Deberia obtener cuentas asociadas a un usuario")
    void deberiaObtenerCuentasAsociadasAUsuario() {
        accountUserService.associateUserToAccount(testAccountId, testUserId);

        AccountsByUserResponse response = accountUserService.getAccountsByUser(testUserId);

        assertNotNull(response);
        assertEquals(testUserId, response.getUserId());
        assertNotNull(response.getAccounts());
        assertTrue(response.getCount() >= 1);
        assertEquals("Cuentas asociadas al usuario encontradas", response.getMessage());
    }

    @Test
    @DisplayName("Deberia retornar lista vacia si no hay cuentas asociadas")
    void deberiaRetornarListaVaciaSiNoHayCuentasAsociadas() {
        AccountsByUserResponse response = accountUserService.getAccountsByUser(testUserId);

        assertNotNull(response);
        assertEquals(testUserId, response.getUserId());
        assertNotNull(response.getAccounts());
        assertEquals(0, response.getAccounts().size());
        assertEquals(0, response.getCount());
        assertEquals("El usuario no tiene cuentas asociadas", response.getMessage());
    }
}

