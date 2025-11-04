package com.tudai.monopatines.accounts.accounts_service.service;

import com.tudai.monopatines.accounts.accounts_service.dto.AccountResponse;
import com.tudai.monopatines.accounts.accounts_service.dto.AccountUserResponse;
import com.tudai.monopatines.accounts.accounts_service.dto.AccountsByUserResponse;
import com.tudai.monopatines.accounts.accounts_service.dto.UserResponse;
import com.tudai.monopatines.accounts.accounts_service.dto.UsersByAccountResponse;
import com.tudai.monopatines.accounts.accounts_service.entity.Account;
import com.tudai.monopatines.accounts.accounts_service.entity.AccountUser;
import com.tudai.monopatines.accounts.accounts_service.entity.User;
import com.tudai.monopatines.accounts.accounts_service.exception.AccountNotFoundException;
import com.tudai.monopatines.accounts.accounts_service.exception.UserNotFoundException;
import com.tudai.monopatines.accounts.accounts_service.repository.AccountRepository;
import com.tudai.monopatines.accounts.accounts_service.repository.AccountUserRepository;
import com.tudai.monopatines.accounts.accounts_service.repository.UserRepository;
import com.tudai.monopatines.accounts.accounts_service.util.MapperUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar la relación many-to-many entre cuentas y usuarios.
 * Permite asociar y desasociar usuarios de cuentas, y consultar las relaciones existentes.
 * 
 */
@Service
@Transactional
public class AccountUserService {

    private final AccountUserRepository accountUserRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final RoleService roleService;

    /**
     * Constructor del servicio.
     * 
     * @param accountUserRepository Repositorio para acceder a la tabla de relaciones cuenta-usuario
     * @param accountRepository Repositorio para acceder a la base de datos de cuentas
     * @param userRepository Repositorio para acceder a la base de datos de usuarios
     * @param roleService Servicio para gestionar roles
     */
    public AccountUserService(AccountUserRepository accountUserRepository,
                              AccountRepository accountRepository,
                              UserRepository userRepository,
                              RoleService roleService) {
        this.accountUserRepository = accountUserRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.roleService = roleService;
    }

    /**
     * Asocia un usuario a una cuenta.
     * 
     * Crea una relación many-to-many entre una cuenta y un usuario, permitiendo que
     * el usuario utilice los créditos cargados en esa cuenta. Verifica que no exista
     * ya una asociación entre la cuenta y el usuario.
     * 
     * @param accountId ID de la cuenta a asociar
     * @param userId ID del usuario a asociar
     * @return AccountUserResponse con información sobre la asociación creada
     * @throws AccountNotFoundException si no se encuentra la cuenta
     * @throws UserNotFoundException si no se encuentra el usuario
     * @throws RuntimeException si ya existe una asociación entre la cuenta y el usuario
     */
    public AccountUserResponse associateUserToAccount(Long accountId, Long userId) {
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (accountOptional.isEmpty()) {
            throw new AccountNotFoundException(accountId);
        }
        Account account = accountOptional.get();

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(userId);
        }
        User user = userOptional.get();

        // Verificar si ya existe la asociación
        List<AccountUser> allAccountUsers = accountUserRepository.findAll();
        boolean exists = false;
        for (AccountUser au : allAccountUsers) {
            if (au.getAccount().getId().equals(accountId) && 
                au.getUser().getId().equals(userId)) {
                exists = true;
                break;
            }
        }

        if (exists) {
            throw new RuntimeException("User is already associated with this account");
        }

        AccountUser accountUser = new AccountUser(account, user);
        AccountUser savedAccountUser = accountUserRepository.save(accountUser);

        AccountUserResponse response = new AccountUserResponse();
        response.setAccountId(accountId);
        response.setUserId(userId);
        response.setAssociatedAt(savedAccountUser.getAssociatedAt());
        response.setMessage("Usuario asociado exitosamente a la cuenta");
        return response;
    }

    /**
     * Desasocia un usuario de una cuenta.
     * 
     * Elimina la relación many-to-many entre una cuenta y un usuario,
     * impidiendo que el usuario utilice los créditos de esa cuenta.
     * 
     * @param accountId ID de la cuenta
     * @param userId ID del usuario
     * @return AccountUserResponse con información sobre la desasociación realizada
     * @throws AccountNotFoundException si no se encuentra la cuenta
     * @throws UserNotFoundException si no se encuentra el usuario
     * @throws RuntimeException si no existe una asociación entre la cuenta y el usuario
     */
    public AccountUserResponse disassociateUserFromAccount(Long accountId, Long userId) {
        // Verificar que la cuenta existe
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (accountOptional.isEmpty()) {
            throw new AccountNotFoundException(accountId);
        }

        // Verificar que el usuario existe
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(userId);
        }

        List<AccountUser> allAccountUsers = accountUserRepository.findAll();
        AccountUser accountUserToDelete = null;
        for (AccountUser au : allAccountUsers) {
            if (au.getAccount().getId().equals(accountId) && 
                au.getUser().getId().equals(userId)) {
                accountUserToDelete = au;
                break;
            }
        }

        if (accountUserToDelete == null) {
            throw new RuntimeException("Association not found");
        }

        LocalDateTime associatedAt = accountUserToDelete.getAssociatedAt();
        accountUserRepository.delete(accountUserToDelete);

        AccountUserResponse response = new AccountUserResponse();
        response.setAccountId(accountId);
        response.setUserId(userId);
        response.setAssociatedAt(associatedAt);
        response.setMessage("Usuario desasociado exitosamente de la cuenta");
        return response;
    }

    /**
     * Obtiene todos los usuarios asociados a una cuenta.
     * 
     * Retorna la lista de usuarios que pueden utilizar los créditos
     * cargados en la cuenta especificada.
     * 
     * @param accountId ID de la cuenta
     * @return UsersByAccountResponse con la lista de usuarios y mensaje informativo
     * @throws AccountNotFoundException si no se encuentra la cuenta
     */
    @Transactional(readOnly = true)
    public UsersByAccountResponse getUsersByAccount(Long accountId) {
        // Verificar que la cuenta existe
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (accountOptional.isEmpty()) {
            throw new AccountNotFoundException(accountId);
        }

        List<AccountUser> allAccountUsers = accountUserRepository.findAll();
        List<UserResponse> responses = new ArrayList<>();
        for (AccountUser au : allAccountUsers) {
            if (au.getAccount().getId().equals(accountId)) {
                UserResponse userResponse = MapperUtil.mapUserToResponse(au.getUser());
                // Obtener roles para incluir en la respuesta
                List<String> roles = roleService.getRolesByUserId(au.getUser().getId());
                userResponse.setRoles(roles);
                responses.add(userResponse);
            }
        }

        String message;
        if (responses.isEmpty()) {
            message = "La cuenta no tiene usuarios asociados";
        } else {
            message = "Usuarios asociados a la cuenta encontrados";
        }

        UsersByAccountResponse response = new UsersByAccountResponse();
        response.setAccountId(accountId);
        response.setUsers(responses);
        response.setMessage(message);
        return response;
    }

    /**
     * Obtiene todas las cuentas asociadas a un usuario.
     * 
     * Retorna la lista de cuentas cuyos créditos puede utilizar el usuario especificado.
     * Un usuario puede estar asociado a múltiples cuentas.
     * 
     * @param userId ID del usuario
     * @return AccountsByUserResponse con la lista de cuentas y mensaje informativo
     * @throws UserNotFoundException si no se encuentra el usuario
     */
    @Transactional(readOnly = true)
    public AccountsByUserResponse getAccountsByUser(Long userId) {
        // Verificar que el usuario existe
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(userId);
        }

        List<AccountUser> allAccountUsers = accountUserRepository.findAll();
        List<AccountResponse> responses = new ArrayList<>();
        for (AccountUser au : allAccountUsers) {
            if (au.getUser().getId().equals(userId)) {
                responses.add(MapperUtil.mapAccountToResponse(au.getAccount()));
            }
        }

        String message;
        if (responses.isEmpty()) {
            message = "El usuario no tiene cuentas asociadas";
        } else {
            message = "Cuentas asociadas al usuario encontradas";
        }

        AccountsByUserResponse response = new AccountsByUserResponse();
        response.setUserId(userId);
        response.setAccounts(responses);
        response.setMessage(message);
        return response;
    }

}

