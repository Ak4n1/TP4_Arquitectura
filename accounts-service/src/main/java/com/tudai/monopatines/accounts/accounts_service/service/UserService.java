package com.tudai.monopatines.accounts.accounts_service.service;

import com.tudai.monopatines.accounts.accounts_service.dto.CreateUserRequest;
import com.tudai.monopatines.accounts.accounts_service.dto.UpdateUserRequest;
import com.tudai.monopatines.accounts.accounts_service.dto.UserResponse;

import java.util.List;

/**
 * Interfaz del servicio para gestionar usuarios.
 * Define los métodos para realizar operaciones CRUD sobre usuarios.
 * 
 */
public interface UserService {

    /**
     * Crea un nuevo usuario del sistema.
     * 
     * @param request Datos del usuario a crear (nombre, apellido, email, teléfono, password)
     * @return UserResponse con el usuario creado
     * @throws UserAlreadyExistsException si el email ya existe (el email debe ser único)
     */
    UserResponse createUser(CreateUserRequest request);

    /**
     * Obtiene un usuario por su ID, incluyendo sus roles.
     * 
     * @param id ID del usuario a buscar
     * @return UserResponse con los datos del usuario y sus roles
     * @throws UserNotFoundException si no se encuentra el usuario
     */
    UserResponse getUserById(Long id);

    /**
     * Obtiene un usuario por su email, incluyendo sus roles.
     * El email es único en el sistema, por lo que este método devuelve un único usuario.
     * 
     * @param email Email del usuario a buscar
     * @return UserResponse con los datos del usuario y sus roles
     * @throws UserNotFoundException si no se encuentra el usuario con ese email
     */
    UserResponse getUserByEmail(String email);

    /**
     * Obtiene todos los usuarios, incluyendo sus roles.
     * 
     * @return Lista de UserResponse con todos los usuarios y sus roles
     */
    List<UserResponse> getAllUsers();

    /**
     * Actualiza los datos de un usuario existente.
     * 
     * @param id ID del usuario a actualizar
     * @param request Datos actualizados del usuario (sin password)
     * @return UserResponse con el usuario actualizado
     * @throws UserNotFoundException si no se encuentra el usuario
     * @throws UserAlreadyExistsException si el nuevo email ya existe
     */
    UserResponse updateUser(Long id, UpdateUserRequest request);

    /**
     * Elimina un usuario del sistema.
     * 
     * @param id ID del usuario a eliminar
     * @throws UserNotFoundException si no se encuentra el usuario
     */
    void deleteUser(Long id);
}
