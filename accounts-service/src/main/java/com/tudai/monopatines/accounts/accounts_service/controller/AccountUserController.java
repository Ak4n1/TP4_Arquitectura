package com.tudai.monopatines.accounts.accounts_service.controller;

import com.tudai.monopatines.accounts.accounts_service.dto.AccountUserResponse;
import com.tudai.monopatines.accounts.accounts_service.dto.AccountsByUserResponse;
import com.tudai.monopatines.accounts.accounts_service.dto.UsersByAccountResponse;
import com.tudai.monopatines.accounts.accounts_service.service.AccountUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
                "Roles requeridos: ROLE_USER, ROLE_ADMIN. " +
                "NOTA: La validacion de roles se realiza en el API Gateway."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario asociado exitosamente",
            content = @Content(schema = @Schema(implementation = AccountUserResponse.class))),
        @ApiResponse(responseCode = "404", description = "Cuenta o usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "El usuario ya esta asociado a esta cuenta")
    })
    @PostMapping("/{accountId}/users/{userId}")
    public ResponseEntity<AccountUserResponse> associateUserToAccount(
        @Parameter(description = "ID de la cuenta", required = true)
        @PathVariable Long accountId,
        @Parameter(description = "ID del usuario", required = true)
        @PathVariable Long userId) {
        AccountUserResponse response = accountUserService.associateUserToAccount(accountId, userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
        summary = "Desasociar usuario de cuenta",
        description = "Elimina la relacion entre una cuenta y un usuario, impidiendo que el usuario utilice los creditos de esa cuenta. " +
                "Roles requeridos: ROLE_USER, ROLE_ADMIN. " +
                "NOTA: La validacion de roles se realiza en el API Gateway."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario desasociado exitosamente",
            content = @Content(schema = @Schema(implementation = AccountUserResponse.class))),
        @ApiResponse(responseCode = "404", description = "Cuenta o usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "No existe una asociacion entre la cuenta y el usuario")
    })
    @DeleteMapping("/{accountId}/users/{userId}")
    public ResponseEntity<AccountUserResponse> disassociateUserFromAccount(
        @Parameter(description = "ID de la cuenta", required = true)
        @PathVariable Long accountId,
        @Parameter(description = "ID del usuario", required = true)
        @PathVariable Long userId) {
        AccountUserResponse response = accountUserService.disassociateUserFromAccount(accountId, userId);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Obtener usuarios de una cuenta",
        description = "Retorna la lista de usuarios asociados a una cuenta, incluyendo sus roles asignados. " +
                "Roles requeridos: ROLE_USER, ROLE_ADMIN. " +
                "NOTA: La validacion de roles se realiza en el API Gateway."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuarios obtenidos exitosamente",
            content = @Content(schema = @Schema(implementation = UsersByAccountResponse.class))),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    @GetMapping("/{accountId}/users")
    public ResponseEntity<UsersByAccountResponse> getUsersByAccount(
        @Parameter(description = "ID de la cuenta", required = true)
        @PathVariable Long accountId) {
        UsersByAccountResponse response = accountUserService.getUsersByAccount(accountId);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Obtener cuentas de un usuario",
        description = "Retorna la lista de cuentas asociadas a un usuario. Un usuario puede estar asociado a multiples cuentas. " +
                "Roles requeridos: ROLE_USER, ROLE_ADMIN. " +
                "NOTA: La validacion de roles se realiza en el API Gateway."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cuentas obtenidas exitosamente",
            content = @Content(schema = @Schema(implementation = AccountsByUserResponse.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/users/{userId}/accounts")
    public ResponseEntity<AccountsByUserResponse> getAccountsByUser(
        @Parameter(description = "ID del usuario", required = true)
        @PathVariable Long userId) {
        AccountsByUserResponse response = accountUserService.getAccountsByUser(userId);
        return ResponseEntity.ok(response);
    }
}

