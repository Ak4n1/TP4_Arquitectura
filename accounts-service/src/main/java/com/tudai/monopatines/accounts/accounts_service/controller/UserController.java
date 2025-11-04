package com.tudai.monopatines.accounts.accounts_service.controller;

import com.tudai.monopatines.accounts.accounts_service.dto.CreateUserRequest;
import com.tudai.monopatines.accounts.accounts_service.dto.UpdateUserRequest;
import com.tudai.monopatines.accounts.accounts_service.dto.UserResponse;
import com.tudai.monopatines.accounts.accounts_service.service.UserService;
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
@RequestMapping("/api/accounts/users")
@Tag(name = "Users", description = "API para gestionar usuarios del sistema")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
        summary = "Crear un nuevo usuario",
        description = "Crea un nuevo usuario en el sistema. El password debe venir hasheado desde auth-service. Se asigna ROLE_USER por defecto. " +
                "Este endpoint es PUBLICO (no requiere autenticacion) ya que es parte del proceso de registro. " +
                "Normalmente es llamado internamente por auth-service durante el registro mediante gRPC. " +
                "NOTA: La validacion de roles se realiza en el API Gateway."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "409", description = "El email ya existe"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos")
    })
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
        @Parameter(description = "Datos del usuario a crear", required = true)
        @Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
        summary = "Obtener usuario por ID",
        description = "Obtiene los datos de un usuario incluyendo sus roles asignados. " +
                "Roles requeridos: ROLE_USER, ROLE_ADMIN. " +
                "NOTA: La validacion de roles se realiza en el API Gateway."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
        @Parameter(description = "ID del usuario", required = true)
        @PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Obtener usuario por email",
        description = "Obtiene los datos de un usuario por su email (unico en el sistema) incluyendo sus roles. " +
                "Roles requeridos: ROLE_ADMIN. " +
                "NOTA: La validacion de roles se realiza en el API Gateway."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping
    public ResponseEntity<UserResponse> getUserByEmail(
        @Parameter(description = "Email del usuario", required = true)
        @RequestParam String email) {
        UserResponse response = userService.getUserByEmail(email);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Obtener todos los usuarios",
        description = "Retorna la lista completa de usuarios del sistema, incluyendo sus roles asignados. " +
                "Roles requeridos: ROLE_ADMIN. " +
                "NOTA: La validacion de roles se realiza en el API Gateway."
    )
    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente")
    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> response = userService.getAllUsers();
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Actualizar usuario",
        description = "Actualiza los datos de un usuario existente. No incluye password (se cambia desde auth-service). " +
                "Roles requeridos: ROLE_USER, ROLE_ADMIN. " +
                "NOTA: La validacion de roles se realiza en el API Gateway."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "409", description = "El nuevo email ya existe"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
        @Parameter(description = "ID del usuario a actualizar", required = true)
        @PathVariable Long id,
        @Parameter(description = "Datos actualizados del usuario (sin password)", required = true)
        @Valid @RequestBody UpdateUserRequest request) {
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Eliminar usuario",
        description = "Elimina un usuario del sistema permanentemente. " +
                "Roles requeridos: ROLE_ADMIN. " +
                "NOTA: La validacion de roles se realiza en el API Gateway. Esta operacion es solo para administradores."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
        @Parameter(description = "ID del usuario a eliminar", required = true)
        @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

