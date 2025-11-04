package com.tudai.monopatines.accounts.accounts_service.service;

import com.tudai.monopatines.accounts.accounts_service.dto.AccountRequest;
import com.tudai.monopatines.accounts.accounts_service.dto.AccountResponse;
import com.tudai.monopatines.accounts.accounts_service.dto.BalanceRequest;
import com.tudai.monopatines.accounts.accounts_service.dto.BalanceResponse;
import com.tudai.monopatines.accounts.accounts_service.exception.AccountAlreadyExistsException;
import com.tudai.monopatines.accounts.accounts_service.exception.AccountInactiveException;
import com.tudai.monopatines.accounts.accounts_service.exception.AccountNotFoundException;
import com.tudai.monopatines.accounts.accounts_service.exception.InsufficientBalanceException;
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
 * Tests de integracion para AccountService.
 * Usa Spring Boot Test con H2 en memoria para probar el servicio completo.
 * 
 */
@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("Tests de integracion - AccountService")
class AccountServiceIntegrationTest {

    @Autowired
    private AccountService accountService;

    private AccountRequest testAccountRequest;

    @BeforeEach
    void setUp() {
        testAccountRequest = new AccountRequest();
        testAccountRequest.setIdentificationNumber("ACC001");
        testAccountRequest.setMercadoPagoAccountId("MP123456");
        testAccountRequest.setCurrentBalance(1000.0);
    }

    @Test
    @DisplayName("Deberia crear una cuenta exitosamente")
    void deberiaCrearCuentaExitosamente() {
        AccountResponse response = accountService.createAccount(testAccountRequest);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("ACC001", response.getIdentificationNumber());
        assertEquals("MP123456", response.getMercadoPagoAccountId());
        assertEquals(1000.0, response.getCurrentBalance());
        assertTrue(response.getActive());
        assertNotNull(response.getCreatedAt());
    }

    @Test
    @DisplayName("Deberia lanzar excepcion si el numero identificatorio ya existe")
    void deberiaLanzarExcepcionSiNumeroIdentificatorioYaExiste() {
        accountService.createAccount(testAccountRequest);

        assertThrows(AccountAlreadyExistsException.class, () -> {
            accountService.createAccount(testAccountRequest);
        });
    }

    @Test
    @DisplayName("Deberia obtener una cuenta por ID")
    void deberiaObtenerCuentaPorId() {
        AccountResponse createdAccount = accountService.createAccount(testAccountRequest);

        AccountResponse foundAccount = accountService.getAccountById(createdAccount.getId());

        assertNotNull(foundAccount);
        assertEquals(createdAccount.getId(), foundAccount.getId());
        assertEquals("ACC001", foundAccount.getIdentificationNumber());
    }

    @Test
    @DisplayName("Deberia lanzar excepcion si la cuenta no existe")
    void deberiaLanzarExcepcionSiCuentaNoExiste() {
        assertThrows(AccountNotFoundException.class, () -> {
            accountService.getAccountById(999L);
        });
    }

    @Test
    @DisplayName("Deberia obtener todas las cuentas")
    void deberiaObtenerTodasLasCuentas() {
        AccountRequest account1 = new AccountRequest();
        account1.setIdentificationNumber("ACC001");
        account1.setMercadoPagoAccountId("MP123456");
        AccountRequest account2 = new AccountRequest();
        account2.setIdentificationNumber("ACC002");
        account2.setMercadoPagoAccountId("MP654321");

        accountService.createAccount(account1);
        accountService.createAccount(account2);

        List<AccountResponse> allAccounts = accountService.getAllAccounts();

        assertTrue(allAccounts.size() >= 2);
    }

    @Test
    @DisplayName("Deberia obtener solo las cuentas activas")
    void deberiaObtenerSoloCuentasActivas() {
        AccountRequest request1 = new AccountRequest();
        request1.setIdentificationNumber("ACC001");
        request1.setMercadoPagoAccountId("MP123456");
        AccountRequest request2 = new AccountRequest();
        request2.setIdentificationNumber("ACC002");
        request2.setMercadoPagoAccountId("MP654321");
        
        AccountResponse account1 = accountService.createAccount(request1);
        AccountResponse account2 = accountService.createAccount(request2);
        accountService.cancelAccount(account2.getId());

        List<AccountResponse> activeAccounts = accountService.getActiveAccounts();

        assertTrue(activeAccounts.size() >= 1);
        for (AccountResponse account : activeAccounts) {
            assertTrue(account.getActive());
        }
    }

    @Test
    @DisplayName("Deberia actualizar una cuenta exitosamente")
    void deberiaActualizarCuentaExitosamente() {
        AccountResponse createdAccount = accountService.createAccount(testAccountRequest);
        AccountRequest updateRequest = new AccountRequest();
        updateRequest.setIdentificationNumber("ACC001-UPDATED");
        updateRequest.setMercadoPagoAccountId("MP123456");
        updateRequest.setCurrentBalance(2000.0);

        AccountResponse updatedAccount = accountService.updateAccount(createdAccount.getId(), updateRequest);

        assertNotNull(updatedAccount);
        assertEquals(createdAccount.getId(), updatedAccount.getId());
        assertEquals("ACC001-UPDATED", updatedAccount.getIdentificationNumber());
        assertEquals(2000.0, updatedAccount.getCurrentBalance());
    }

    @Test
    @DisplayName("Deberia anular una cuenta exitosamente")
    void deberiaAnularCuentaExitosamente() {
        AccountResponse createdAccount = accountService.createAccount(testAccountRequest);
        assertTrue(createdAccount.getActive());

        AccountResponse cancelledAccount = accountService.cancelAccount(createdAccount.getId());

        assertFalse(cancelledAccount.getActive());
        assertNotNull(cancelledAccount.getCancelledAt());
    }

    @Test
    @DisplayName("Deberia cargar saldo a una cuenta")
    void deberiaCargarSaldoACuenta() {
        AccountResponse createdAccount = accountService.createAccount(testAccountRequest);
        BalanceRequest balanceRequest = new BalanceRequest();
        balanceRequest.setAmount(500.0);

        BalanceResponse response = accountService.loadBalance(createdAccount.getId(), balanceRequest);

        assertNotNull(response);
        assertEquals(createdAccount.getId(), response.getAccountId());
        assertEquals(1500.0, response.getCurrentBalance());
    }

    @Test
    @DisplayName("Deberia lanzar excepcion si intenta cargar saldo a cuenta inactiva")
    void deberiaLanzarExcepcionSiIntentaCargarSaldoACuentaInactiva() {
        AccountResponse createdAccount = accountService.createAccount(testAccountRequest);
        accountService.cancelAccount(createdAccount.getId());
        BalanceRequest balanceRequest = new BalanceRequest();
        balanceRequest.setAmount(500.0);

        assertThrows(AccountInactiveException.class, () -> {
            accountService.loadBalance(createdAccount.getId(), balanceRequest);
        });
    }

    @Test
    @DisplayName("Deberia descontar saldo de una cuenta")
    void deberiaDescontarSaldoDeCuenta() {
        AccountResponse createdAccount = accountService.createAccount(testAccountRequest);

        BalanceResponse response = accountService.deductBalance(createdAccount.getId(), 500.0);

        assertNotNull(response);
        assertEquals(createdAccount.getId(), response.getAccountId());
        assertEquals(500.0, response.getCurrentBalance());
    }

    @Test
    @DisplayName("Deberia lanzar excepcion si no hay saldo suficiente")
    void deberiaLanzarExcepcionSiNoHaySaldoSuficiente() {
        AccountResponse createdAccount = accountService.createAccount(testAccountRequest);

        assertThrows(InsufficientBalanceException.class, () -> {
            accountService.deductBalance(createdAccount.getId(), 2000.0);
        });
    }

    @Test
    @DisplayName("Deberia obtener el saldo de una cuenta")
    void deberiaObtenerSaldoDeCuenta() {
        AccountResponse createdAccount = accountService.createAccount(testAccountRequest);

        BalanceResponse response = accountService.getBalance(createdAccount.getId());

        assertNotNull(response);
        assertEquals(createdAccount.getId(), response.getAccountId());
        assertEquals(1000.0, response.getCurrentBalance());
    }

    @Test
    @DisplayName("Deberia verificar si una cuenta esta activa")
    void deberiaVerificarSiCuentaEstaActiva() {
        AccountResponse createdAccount = accountService.createAccount(testAccountRequest);

        boolean isActive = accountService.isAccountActive(createdAccount.getId());

        assertTrue(isActive);
    }

    @Test
    @DisplayName("Deberia eliminar una cuenta")
    void deberiaEliminarCuenta() {
        AccountResponse createdAccount = accountService.createAccount(testAccountRequest);

        accountService.deleteAccount(createdAccount.getId());

        assertThrows(AccountNotFoundException.class, () -> {
            accountService.getAccountById(createdAccount.getId());
        });
    }
}

