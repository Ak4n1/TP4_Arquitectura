package com.tudai.monopatines.accounts.accounts_service.controller;

import com.tudai.monopatines.accounts.accounts_service.dto.AccountRequest;
import com.tudai.monopatines.accounts.accounts_service.dto.AccountResponse;
import com.tudai.monopatines.accounts.accounts_service.dto.BalanceRequest;
import com.tudai.monopatines.accounts.accounts_service.dto.BalanceResponse;
import com.tudai.monopatines.accounts.accounts_service.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
                "Este endpoint es PUBLICO (no requiere autenticacion) ya que es parte del proceso de registro de nuevos usuarios. " +
                "NOTA: La validacion de roles se realiza en el API Gateway."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cuenta creada exitosamente",
            content = @Content(schema = @Schema(implementation = AccountResponse.class))),
        @ApiResponse(responseCode = "409", description = "El numero identificatorio ya existe"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos")
    })
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
        @Parameter(description = "Datos de la cuenta a crear", required = true)
        @Valid @RequestBody AccountRequest request) {
        AccountResponse response = accountService.createAccount(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
        summary = "Obtener cuenta por ID",
        description = "Obtiene los datos de una cuenta por su identificador unico. " +
                "Roles requeridos: ROLE_USER, ROLE_ADMIN. " +
                "NOTA: La validacion de roles se realiza en el API Gateway."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cuenta encontrada",
            content = @Content(schema = @Schema(implementation = AccountResponse.class))),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(
        @Parameter(description = "ID de la cuenta", required = true)
        @PathVariable Long id) {
        AccountResponse response = accountService.getAccountById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Obtener todas las cuentas",
        description = "Retorna la lista completa de cuentas del sistema. " +
                "Roles requeridos: ROLE_ADMIN. " +
                "NOTA: La validacion de roles se realiza en el API Gateway."
    )
    @ApiResponse(responseCode = "200", description = "Lista de cuentas obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        List<AccountResponse> response = accountService.getAllAccounts();
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Obtener cuentas activas",
        description = "Retorna unicamente las cuentas que estan activas (no anuladas). " +
                "Roles requeridos: ROLE_ADMIN. " +
                "NOTA: La validacion de roles se realiza en el API Gateway."
    )
    @ApiResponse(responseCode = "200", description = "Lista de cuentas activas obtenida exitosamente")
    @GetMapping("/active")
    public ResponseEntity<List<AccountResponse>> getActiveAccounts() {
        List<AccountResponse> response = accountService.getActiveAccounts();
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Actualizar cuenta",
        description = "Actualiza los datos de una cuenta existente. " +
                "Roles requeridos: ROLE_USER, ROLE_ADMIN. " +
                "NOTA: La validacion de roles se realiza en el API Gateway."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cuenta actualizada exitosamente",
            content = @Content(schema = @Schema(implementation = AccountResponse.class))),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada"),
        @ApiResponse(responseCode = "409", description = "El nuevo numero identificatorio ya existe"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AccountResponse> updateAccount(
        @Parameter(description = "ID de la cuenta a actualizar", required = true)
        @PathVariable Long id,
        @Parameter(description = "Datos actualizados de la cuenta", required = true)
        @Valid @RequestBody AccountRequest request) {
        AccountResponse response = accountService.updateAccount(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Anular cuenta",
        description = "Marca una cuenta como inactiva y establece la fecha de anulacion. Una cuenta anulada no puede ser utilizada para nuevos viajes. " +
                "Roles requeridos: ROLE_ADMIN. " +
                "NOTA: La validacion de roles se realiza en el API Gateway. Esta operacion es solo para administradores."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cuenta anulada exitosamente",
            content = @Content(schema = @Schema(implementation = AccountResponse.class))),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    @PutMapping("/{id}/cancel")
    public ResponseEntity<AccountResponse> cancelAccount(
        @Parameter(description = "ID de la cuenta a anular", required = true)
        @PathVariable Long id) {
        AccountResponse response = accountService.cancelAccount(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Cargar saldo a cuenta",
        description = "Incrementa el saldo actual de la cuenta con el monto especificado. " +
                "Roles requeridos: ROLE_USER, ROLE_ADMIN. " +
                "NOTA: La validacion de roles se realiza en el API Gateway."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Saldo cargado exitosamente",
            content = @Content(schema = @Schema(implementation = BalanceResponse.class))),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada"),
        @ApiResponse(responseCode = "400", description = "La cuenta esta anulada o datos invalidos")
    })
    @PutMapping("/{id}/balance")
    public ResponseEntity<BalanceResponse> loadBalance(
        @Parameter(description = "ID de la cuenta", required = true)
        @PathVariable Long id,
        @Parameter(description = "Monto a cargar (debe ser positivo)", required = true)
        @Valid @RequestBody BalanceRequest request) {
        BalanceResponse response = accountService.loadBalance(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Obtener saldo de cuenta",
        description = "Retorna el saldo actual de una cuenta. " +
                "Roles requeridos: ROLE_USER, ROLE_ADMIN. " +
                "NOTA: La validacion de roles se realiza en el API Gateway."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Saldo obtenido exitosamente",
            content = @Content(schema = @Schema(implementation = BalanceResponse.class))),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    @GetMapping("/{id}/balance")
    public ResponseEntity<BalanceResponse> getBalance(
        @Parameter(description = "ID de la cuenta", required = true)
        @PathVariable Long id) {
        BalanceResponse response = accountService.getBalance(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Descontar saldo de cuenta",
        description = "Descuenta un monto del saldo de una cuenta. Se utiliza cuando se activa un monopatin o se finaliza un viaje. " +
                "Roles requeridos: ROLE_EMPLOYEE, ROLE_ADMIN. " +
                "NOTA: La validacion de roles se realiza en el API Gateway. Este endpoint es usado principalmente por otros microservicios."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Saldo descontado exitosamente",
            content = @Content(schema = @Schema(implementation = BalanceResponse.class))),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada"),
        @ApiResponse(responseCode = "400", description = "La cuenta esta anulada, no hay saldo suficiente o datos invalidos")
    })
    @PutMapping("/{id}/balance/deduct")
    public ResponseEntity<BalanceResponse> deductBalance(
        @Parameter(description = "ID de la cuenta", required = true)
        @PathVariable Long id,
        @Parameter(description = "Monto a descontar", required = true)
        @RequestParam Double amount) {
        BalanceResponse response = accountService.deductBalance(id, amount);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Verificar si cuenta esta activa",
        description = "Retorna true si la cuenta esta activa, false si esta anulada. Usado por otros microservicios. " +
                "Roles requeridos: ROLE_EMPLOYEE, ROLE_ADMIN. " +
                "NOTA: La validacion de roles se realiza en el API Gateway. Este endpoint es usado principalmente por otros microservicios."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado de la cuenta obtenido exitosamente"),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    @GetMapping("/{id}/active")
    public ResponseEntity<Boolean> isAccountActive(
        @Parameter(description = "ID de la cuenta", required = true)
        @PathVariable Long id) {
        boolean active = accountService.isAccountActive(id);
        return ResponseEntity.ok(active);
    }

    @Operation(
        summary = "Eliminar cuenta",
        description = "Elimina una cuenta del sistema permanentemente. " +
                "Roles requeridos: ROLE_ADMIN. " +
                "NOTA: La validacion de roles se realiza en el API Gateway. Esta operacion es solo para administradores."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cuenta eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(
        @Parameter(description = "ID de la cuenta a eliminar", required = true)
        @PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}

