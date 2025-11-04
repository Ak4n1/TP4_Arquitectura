package com.tudai.monopatines.accounts.accounts_service.repository;

import com.tudai.monopatines.accounts.accounts_service.entity.Account;
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
 * Tests de integracion para AccountRepository.
 * Usa Spring Boot Test con H2 en memoria para probar el repositorio.
 * 
 */
@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("Tests de integracion - AccountRepository")
class AccountRepositoryIntegrationTest {

    @Autowired
    private AccountRepository accountRepository;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = new Account("ACC001", "MP123456");
        testAccount.setCurrentBalance(1000.0);
    }

    @Test
    @DisplayName("Deberia guardar una cuenta en la base de datos")
    void deberiaGuardarCuentaEnBaseDeDatos() {
        Account savedAccount = accountRepository.save(testAccount);

        assertNotNull(savedAccount.getId());
        assertEquals("ACC001", savedAccount.getIdentificationNumber());
        assertEquals("MP123456", savedAccount.getMercadoPagoAccountId());
        assertEquals(1000.0, savedAccount.getCurrentBalance());
        assertTrue(savedAccount.getActive());
        assertNotNull(savedAccount.getCreatedAt());
    }

    @Test
    @DisplayName("Deberia encontrar una cuenta por ID")
    void deberiaEncontrarCuentaPorId() {
        Account savedAccount = accountRepository.save(testAccount);

        Optional<Account> foundAccount = accountRepository.findById(savedAccount.getId());

        assertTrue(foundAccount.isPresent());
        assertEquals("ACC001", foundAccount.get().getIdentificationNumber());
        assertEquals(savedAccount.getId(), foundAccount.get().getId());
    }

    @Test
    @DisplayName("Deberia verificar si existe una cuenta por numero identificatorio")
    void deberiaVerificarSiExisteCuentaPorNumeroIdentificatorio() {
        accountRepository.save(testAccount);

        boolean exists = accountRepository.existsByIdentificationNumber("ACC001");

        assertTrue(exists);
    }

    @Test
    @DisplayName("Deberia retornar false si no existe una cuenta por numero identificatorio")
    void deberiaRetornarFalseSiNoExisteCuentaPorNumeroIdentificatorio() {
        boolean exists = accountRepository.existsByIdentificationNumber("ACC999");

        assertFalse(exists);
    }

    @Test
    @DisplayName("Deberia encontrar todas las cuentas activas")
    void deberiaEncontrarTodasLasCuentasActivas() {
        Account activeAccount1 = new Account("ACC001", "MP123456");
        Account activeAccount2 = new Account("ACC002", "MP654321");
        Account inactiveAccount = new Account("ACC003", "MP111111");
        inactiveAccount.cancel();

        accountRepository.save(activeAccount1);
        accountRepository.save(activeAccount2);
        accountRepository.save(inactiveAccount);

        List<Account> activeAccounts = accountRepository.findByActiveTrue();

        assertEquals(2, activeAccounts.size());
        for (Account account : activeAccounts) {
            assertTrue(account.getActive());
        }
    }

    @Test
    @DisplayName("Deberia encontrar todas las cuentas inactivas")
    void deberiaEncontrarTodasLasCuentasInactivas() {
        Account activeAccount = new Account("ACC001", "MP123456");
        Account inactiveAccount1 = new Account("ACC002", "MP654321");
        inactiveAccount1.cancel();
        Account inactiveAccount2 = new Account("ACC003", "MP111111");
        inactiveAccount2.cancel();

        accountRepository.save(activeAccount);
        accountRepository.save(inactiveAccount1);
        accountRepository.save(inactiveAccount2);

        List<Account> inactiveAccounts = accountRepository.findByActiveFalse();

        assertEquals(2, inactiveAccounts.size());
        for (Account account : inactiveAccounts) {
            assertFalse(account.getActive());
        }
    }

    @Test
    @DisplayName("Deberia actualizar una cuenta existente")
    void deberiaActualizarCuentaExistente() {
        Account savedAccount = accountRepository.save(testAccount);
        savedAccount.setCurrentBalance(2000.0);
        savedAccount.setIdentificationNumber("ACC001-UPDATED");

        Account updatedAccount = accountRepository.save(savedAccount);

        assertEquals(savedAccount.getId(), updatedAccount.getId());
        assertEquals(2000.0, updatedAccount.getCurrentBalance());
        assertEquals("ACC001-UPDATED", updatedAccount.getIdentificationNumber());
    }

    @Test
    @DisplayName("Deberia eliminar una cuenta")
    void deberiaEliminarCuenta() {
        Account savedAccount = accountRepository.save(testAccount);
        Long accountId = savedAccount.getId();

        accountRepository.deleteById(accountId);

        Optional<Account> deletedAccount = accountRepository.findById(accountId);
        assertFalse(deletedAccount.isPresent());
    }

    @Test
    @DisplayName("Deberia encontrar todas las cuentas")
    void deberiaEncontrarTodasLasCuentas() {
        Account account1 = new Account("ACC001", "MP123456");
        Account account2 = new Account("ACC002", "MP654321");
        Account account3 = new Account("ACC003", "MP111111");

        accountRepository.save(account1);
        accountRepository.save(account2);
        accountRepository.save(account3);

        List<Account> allAccounts = accountRepository.findAll();

        assertTrue(allAccounts.size() >= 3);
        boolean found1 = false;
        boolean found2 = false;
        boolean found3 = false;
        for (Account account : allAccounts) {
            if (account.getIdentificationNumber().equals("ACC001")) {
                found1 = true;
            }
            if (account.getIdentificationNumber().equals("ACC002")) {
                found2 = true;
            }
            if (account.getIdentificationNumber().equals("ACC003")) {
                found3 = true;
            }
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
    }
}

