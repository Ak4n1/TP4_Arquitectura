package com.tudai.monopatines.accounts.accounts_service.controller;

import com.tudai.monopatines.accounts.accounts_service.dto.AccountRequest;
import com.tudai.monopatines.accounts.accounts_service.dto.AccountResponse;
import com.tudai.monopatines.accounts.accounts_service.dto.BalanceRequest;
import com.tudai.monopatines.accounts.accounts_service.dto.BalanceResponse;
import com.tudai.monopatines.accounts.accounts_service.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Accounts", description = "API para gestionar cuentas del sistema")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(
        summary = "Crear cuenta",
        description = "Crea una nueva cuenta asociada a una cuenta de Mercado Pago. " +
                "Roles requeridos: PUBLICO (no requiere autenticacion)."
    )
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
        @Valid @RequestBody AccountRequest request) {
        AccountResponse response = accountService.createAccount(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
        summary = "Obtener cuenta por ID",
        description = "Obtiene los datos de una cuenta por su identificador unico. " +
                "Roles requeridos: ROLE_USER, ROLE_ADMIN."
    )
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable Long id) {
        AccountResponse response = accountService.getAccountById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Obtener todas las cuentas",
        description = "Retorna la lista completa de cuentas del sistema. " +
                "Roles requeridos: ROLE_ADMIN."
    )
    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        List<AccountResponse> response = accountService.getAllAccounts();
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Obtener cuentas activas",
        description = "Retorna unicamente las cuentas que estan activas (no anuladas). " +
                "Roles requeridos: ROLE_ADMIN."
    )
    @GetMapping("/active")
    public ResponseEntity<List<AccountResponse>> getActiveAccounts() {
        List<AccountResponse> response = accountService.getActiveAccounts();
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Actualizar cuenta",
        description = "Actualiza los datos de una cuenta existente. " +
                "Roles requeridos: ROLE_USER, ROLE_ADMIN."
    )
    @PutMapping("/{id}")
    public ResponseEntity<AccountResponse> updateAccount(
        @PathVariable Long id,
        @Valid @RequestBody AccountRequest request) {
        AccountResponse response = accountService.updateAccount(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Anular cuenta",
        description = "Marca una cuenta como inactiva y establece la fecha de anulacion. Una cuenta anulada no puede ser utilizada para nuevos viajes. " +
                "Roles requeridos: ROLE_ADMIN."
    )
    @PutMapping("/{id}/cancel")
    public ResponseEntity<AccountResponse> cancelAccount(@PathVariable Long id) {
        AccountResponse response = accountService.cancelAccount(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Cargar saldo a cuenta",
        description = "Incrementa el saldo actual de la cuenta con el monto especificado. " +
                "Roles requeridos: ROLE_USER, ROLE_ADMIN."
    )
    @PutMapping("/{id}/balance")
    public ResponseEntity<BalanceResponse> loadBalance(
        @PathVariable Long id,
        @Valid @RequestBody BalanceRequest request) {
        BalanceResponse response = accountService.loadBalance(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Obtener saldo de cuenta",
        description = "Retorna el saldo actual de una cuenta. " +
                "Roles requeridos: ROLE_USER, ROLE_ADMIN."
    )
    @GetMapping("/{id}/balance")
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable Long id) {
        BalanceResponse response = accountService.getBalance(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Descontar saldo de cuenta",
        description = "Descuenta un monto del saldo de una cuenta. Se utiliza cuando se activa un monopatin o se finaliza un viaje. " +
                "Roles requeridos: ROLE_EMPLOYEE, ROLE_ADMIN."
    )
    @PutMapping("/{id}/balance/deduct")
    public ResponseEntity<BalanceResponse> deductBalance(
        @PathVariable Long id,
        @RequestParam Double amount) {
        BalanceResponse response = accountService.deductBalance(id, amount);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Verificar si cuenta esta activa",
        description = "Retorna true si la cuenta esta activa, false si esta anulada. Usado por otros microservicios. " +
                "Roles requeridos: ROLE_EMPLOYEE, ROLE_ADMIN."
    )
    @GetMapping("/{id}/active")
    public ResponseEntity<Boolean> isAccountActive(@PathVariable Long id) {
        boolean active = accountService.isAccountActive(id);
        return ResponseEntity.ok(active);
    }

    @Operation(
        summary = "Eliminar cuenta",
        description = "Elimina una cuenta del sistema permanentemente. " +
                "Roles requeridos: ROLE_ADMIN."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}

