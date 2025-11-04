package com.tudai.monopatines.accounts.accounts_service.controller;

import com.tudai.monopatines.accounts.accounts_service.dto.CreateUserRequest;
import com.tudai.monopatines.accounts.accounts_service.dto.UpdateUserRequest;
import com.tudai.monopatines.accounts.accounts_service.dto.UserResponse;
import com.tudai.monopatines.accounts.accounts_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
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
                "Roles requeridos: PUBLICO (no requiere autenticacion)."
    )
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
        @Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
        summary = "Obtener usuario por ID",
        description = "Obtiene los datos de un usuario incluyendo sus roles asignados. " +
                "Roles requeridos: ROLE_USER, ROLE_ADMIN."
    )
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Obtener usuario por email",
        description = "Obtiene los datos de un usuario por su email (unico en el sistema) incluyendo sus roles. " +
                "Roles requeridos: ROLE_ADMIN."
    )
    @GetMapping
    public ResponseEntity<UserResponse> getUserByEmail(@RequestParam String email) {
        UserResponse response = userService.getUserByEmail(email);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Obtener todos los usuarios",
        description = "Retorna la lista completa de usuarios del sistema, incluyendo sus roles asignados. " +
                "Roles requeridos: ROLE_ADMIN."
    )
    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> response = userService.getAllUsers();
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Actualizar usuario",
        description = "Actualiza los datos de un usuario existente. No incluye password (se cambia desde auth-service). " +
                "Roles requeridos: ROLE_USER, ROLE_ADMIN."
    )
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
        @PathVariable Long id,
        @Valid @RequestBody UpdateUserRequest request) {
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Eliminar usuario",
        description = "Elimina un usuario del sistema permanentemente. " +
                "Roles requeridos: ROLE_ADMIN."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

