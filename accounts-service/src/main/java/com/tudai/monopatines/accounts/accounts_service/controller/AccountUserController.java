package com.tudai.monopatines.accounts.accounts_service.controller;

import com.tudai.monopatines.accounts.accounts_service.dto.AccountUserResponse;
import com.tudai.monopatines.accounts.accounts_service.dto.AccountsByUserResponse;
import com.tudai.monopatines.accounts.accounts_service.dto.UsersByAccountResponse;
import com.tudai.monopatines.accounts.accounts_service.service.AccountUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Account-User Relationships", description = "API para gestionar relaciones entre cuentas y usuarios")
public class AccountUserController {

    private final AccountUserService accountUserService;

    public AccountUserController(AccountUserService accountUserService) {
        this.accountUserService = accountUserService;
    }

    @Operation(
        summary = "Asociar usuario a cuenta",
        description = "Crea una relacion entre una cuenta y un usuario, permitiendo que el usuario utilice los creditos cargados en esa cuenta. " +
                "Roles requeridos: ROLE_USER, ROLE_ADMIN."
    )
    @PostMapping("/{accountId}/users/{userId}")
    public ResponseEntity<AccountUserResponse> associateUserToAccount(
        @PathVariable Long accountId,
        @PathVariable Long userId) {
        AccountUserResponse response = accountUserService.associateUserToAccount(accountId, userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
        summary = "Desasociar usuario de cuenta",
        description = "Elimina la relacion entre una cuenta y un usuario, impidiendo que el usuario utilice los creditos de esa cuenta. " +
                "Roles requeridos: ROLE_USER, ROLE_ADMIN."
    )
    @DeleteMapping("/{accountId}/users/{userId}")
    public ResponseEntity<AccountUserResponse> disassociateUserFromAccount(
        @PathVariable Long accountId,
        @PathVariable Long userId) {
        AccountUserResponse response = accountUserService.disassociateUserFromAccount(accountId, userId);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Obtener usuarios de una cuenta",
        description = "Retorna la lista de usuarios asociados a una cuenta, incluyendo sus roles asignados. " +
                "Roles requeridos: ROLE_USER, ROLE_ADMIN."
    )
    @GetMapping("/{accountId}/users")
    public ResponseEntity<UsersByAccountResponse> getUsersByAccount(@PathVariable Long accountId) {
        UsersByAccountResponse response = accountUserService.getUsersByAccount(accountId);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Obtener cuentas de un usuario",
        description = "Retorna la lista de cuentas asociadas a un usuario. Un usuario puede estar asociado a multiples cuentas. " +
                "Roles requeridos: ROLE_USER, ROLE_ADMIN."
    )
    @GetMapping("/users/{userId}/accounts")
    public ResponseEntity<AccountsByUserResponse> getAccountsByUser(@PathVariable Long userId) {
        AccountsByUserResponse response = accountUserService.getAccountsByUser(userId);
        return ResponseEntity.ok(response);
    }
}

