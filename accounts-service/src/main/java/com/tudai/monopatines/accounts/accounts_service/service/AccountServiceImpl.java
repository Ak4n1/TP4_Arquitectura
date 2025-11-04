package com.tudai.monopatines.accounts.accounts_service.service;

import com.tudai.monopatines.accounts.accounts_service.dto.AccountRequest;
import com.tudai.monopatines.accounts.accounts_service.dto.AccountResponse;
import com.tudai.monopatines.accounts.accounts_service.dto.BalanceRequest;
import com.tudai.monopatines.accounts.accounts_service.dto.BalanceResponse;
import com.tudai.monopatines.accounts.accounts_service.entity.Account;
import com.tudai.monopatines.accounts.accounts_service.exception.AccountAlreadyExistsException;
import com.tudai.monopatines.accounts.accounts_service.exception.AccountInactiveException;
import com.tudai.monopatines.accounts.accounts_service.exception.AccountNotFoundException;
import com.tudai.monopatines.accounts.accounts_service.exception.InsufficientBalanceException;
import com.tudai.monopatines.accounts.accounts_service.repository.AccountRepository;
import com.tudai.monopatines.accounts.accounts_service.util.MapperUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio para gestionar cuentas.
 * Contiene la lógica de negocio para todas las operaciones relacionadas con cuentas.
 * 
 */
@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    /**
     * Constructor del servicio.
     * 
     * @param accountRepository Repositorio para acceder a la base de datos de cuentas
     */
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * {@inheritDoc}
     * 
     * Implementación: Verifica que no exista una cuenta con el mismo número identificatorio,
     * crea una nueva cuenta con saldo inicial (por defecto 0.0) y la guarda en la base de datos.
     */
    @Override
    public AccountResponse createAccount(AccountRequest request) {
        // Verificar si ya existe una cuenta con el mismo número identificatorio
        if (accountRepository.existsByIdentificationNumber(request.getIdentificationNumber())) {
            throw AccountAlreadyExistsException.forIdentificationNumber(request.getIdentificationNumber());
        }

        Account account = new Account();
        account.setIdentificationNumber(request.getIdentificationNumber());
        account.setMercadoPagoAccountId(request.getMercadoPagoAccountId());
        account.setCurrentBalance(request.getCurrentBalance() != null ? request.getCurrentBalance() : 0.0);

        Account savedAccount = accountRepository.save(account);
        return MapperUtil.mapAccountToResponse(savedAccount);
    }

    /**
     * {@inheritDoc}
     * 
     * Implementación: Busca la cuenta en la base de datos y la convierte a DTO de respuesta.
     */
    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccountById(Long id) {
        Optional<Account> accountOptional = accountRepository.findById(id);
        if (accountOptional.isEmpty()) {
            throw new AccountNotFoundException(id);
        }
        Account account = accountOptional.get();
        return MapperUtil.mapAccountToResponse(account);
    }

    /**
     * {@inheritDoc}
     * 
     * Implementación: Obtiene todas las cuentas desde el repositorio,
     * las mapea a DTOs de respuesta y retorna una lista con todos los resultados.
     */
    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        List<AccountResponse> responses = new ArrayList<>();
        for (Account account : accounts) {
            responses.add(MapperUtil.mapAccountToResponse(account));
        }
        return responses;
    }

    /**
     * {@inheritDoc}
     * 
     * Implementación: Obtiene todas las cuentas activas (no anuladas) desde el repositorio,
     * las convierte a DTOs de respuesta y retorna una lista con todos los resultados.
     */
    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> getActiveAccounts() {
        List<Account> accounts = accountRepository.findByActiveTrue();
        List<AccountResponse> responses = new ArrayList<>();
        for (Account account : accounts) {
            responses.add(MapperUtil.mapAccountToResponse(account));
        }
        return responses;
    }

    /**
     * {@inheritDoc}
     * 
     * Implementación: Busca la cuenta, verifica que el nuevo número identificatorio no exista
     * (si cambió), actualiza los campos y guarda los cambios en la base de datos.
     */
    @Override
    public AccountResponse updateAccount(Long id, AccountRequest request) {
        Optional<Account> accountOptional = accountRepository.findById(id);
        if (accountOptional.isEmpty()) {
            throw new AccountNotFoundException(id);
        }
        Account account = accountOptional.get();

        // Verificar si el número identificatorio cambió y ya existe
        if (!account.getIdentificationNumber().equals(request.getIdentificationNumber())) {
            if (accountRepository.existsByIdentificationNumber(request.getIdentificationNumber())) {
                throw AccountAlreadyExistsException.forIdentificationNumber(request.getIdentificationNumber());
            }
        }

        account.setIdentificationNumber(request.getIdentificationNumber());
        account.setMercadoPagoAccountId(request.getMercadoPagoAccountId());
        if (request.getCurrentBalance() != null) {
            account.setCurrentBalance(request.getCurrentBalance());
        }

        Account updatedAccount = accountRepository.save(account);
        return MapperUtil.mapAccountToResponse(updatedAccount);
    }

    /**
     * {@inheritDoc}
     * 
     * Implementación: Busca la cuenta, llama al método cancel() de la entidad
     * que marca la cuenta como inactiva y establece la fecha de anulación.
     */
    @Override
    public AccountResponse cancelAccount(Long id) {
        Optional<Account> accountOptional = accountRepository.findById(id);
        if (accountOptional.isEmpty()) {
            throw new AccountNotFoundException(id);
        }
        Account account = accountOptional.get();

        account.cancel();
        Account cancelledAccount = accountRepository.save(account);
        return MapperUtil.mapAccountToResponse(cancelledAccount);
    }

    /**
     * {@inheritDoc}
     * 
     * Implementación: Verifica que la cuenta esté activa, incrementa el saldo
     * con el monto especificado y guarda los cambios.
     */
    @Override
    public BalanceResponse loadBalance(Long id, BalanceRequest request) {
        Optional<Account> accountOptional = accountRepository.findById(id);
        if (accountOptional.isEmpty()) {
            throw new AccountNotFoundException(id);
        }
        Account account = accountOptional.get();

        if (!account.getActive()) {
            throw new AccountInactiveException(id);
        }

        account.setCurrentBalance(account.getCurrentBalance() + request.getAmount());
        Account updatedAccount = accountRepository.save(account);

        return new BalanceResponse(updatedAccount.getId(), updatedAccount.getCurrentBalance());
    }

    /**
     * {@inheritDoc}
     * 
     * Implementación: Verifica que la cuenta esté activa y tenga saldo suficiente,
     * descuenta el monto del saldo actual y guarda los cambios.
     */
    @Override
    public BalanceResponse deductBalance(Long id, Double amount) {
        Optional<Account> accountOptional = accountRepository.findById(id);
        if (accountOptional.isEmpty()) {
            throw new AccountNotFoundException(id);
        }
        Account account = accountOptional.get();

        if (!account.getActive()) {
            throw new AccountInactiveException(id);
        }

        if (account.getCurrentBalance() < amount) {
            throw new InsufficientBalanceException(account.getCurrentBalance(), amount);
        }

        account.setCurrentBalance(account.getCurrentBalance() - amount);
        Account updatedAccount = accountRepository.save(account);

        return new BalanceResponse(updatedAccount.getId(), updatedAccount.getCurrentBalance());
    }

    /**
     * {@inheritDoc}
     * 
     * Implementación: Busca la cuenta y retorna solo el saldo actual en un DTO BalanceResponse.
     */
    @Override
    @Transactional(readOnly = true)
    public BalanceResponse getBalance(Long id) {
        Optional<Account> accountOptional = accountRepository.findById(id);
        if (accountOptional.isEmpty()) {
            throw new AccountNotFoundException(id);
        }
        Account account = accountOptional.get();
        return new BalanceResponse(account.getId(), account.getCurrentBalance());
    }

    /**
     * {@inheritDoc}
     * 
     * Implementación: Busca la cuenta y retorna el estado activo (true/false).
     * Usado por otros microservicios para validar si una cuenta puede ser utilizada.
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isAccountActive(Long id) {
        Optional<Account> accountOptional = accountRepository.findById(id);
        if (accountOptional.isEmpty()) {
            throw new AccountNotFoundException(id);
        }
        Account account = accountOptional.get();
        return account.getActive();
    }

    /**
     * {@inheritDoc}
     * 
     * Implementación: Verifica que la cuenta exista y la elimina de la base de datos.
     */
    @Override
    public void deleteAccount(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new AccountNotFoundException(id);
        }
        accountRepository.deleteById(id);
    }

}
