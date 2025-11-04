package com.tudai.monopatines.accounts.accounts_service.service;

import com.tudai.monopatines.accounts.accounts_service.entity.Role;
import com.tudai.monopatines.accounts.accounts_service.entity.User;
import com.tudai.monopatines.accounts.accounts_service.entity.UserRole;
import com.tudai.monopatines.accounts.accounts_service.repository.RoleRepository;
import com.tudai.monopatines.accounts.accounts_service.repository.UserRepository;
import com.tudai.monopatines.accounts.accounts_service.repository.UserRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar roles y asignaciones de roles a usuarios.
 * 
 */
@Service
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    /**
     * Constructor del servicio.
     * 
     * @param roleRepository Repositorio para acceder a la base de datos de roles
     * @param userRepository Repositorio para acceder a la base de datos de usuarios
     * @param userRoleRepository Repositorio para acceder a la tabla de relaciones usuario-rol
     */
    public RoleService(RoleRepository roleRepository,
                       UserRepository userRepository,
                       UserRoleRepository userRoleRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
    }

    /**
     * Obtiene todos los roles asignados a un usuario.
     * 
     * Retorna la lista de nombres de roles (ej: "ROLE_USER", "ROLE_ADMIN")
     * asignados al usuario especificado.
     * 
     * @param userId ID del usuario
     * @return Lista de nombres de roles asignados al usuario
     */
    @Transactional(readOnly = true)
    public List<String> getRolesByUserId(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return new ArrayList<>();
        }

        List<UserRole> userRoles = userRoleRepository.findByUserId(userId);
        List<String> roles = new ArrayList<>();
        for (UserRole userRole : userRoles) {
            if (userRole.getRole() != null) {
                roles.add(userRole.getRole().getName());
            }
        }
        return roles;
    }

    /**
     * Asigna un rol a un usuario.
     * 
     * Crea una relación entre el usuario y el rol especificado.
     * Si el rol no existe, lo crea primero.
     * 
     * @param userId ID del usuario
     * @param roleName Nombre del rol (ej: "ROLE_USER", "ROLE_ADMIN")
     */
    public void assignRoleToUser(Long userId, String roleName) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        User user = userOptional.get();

        Optional<Role> roleOptional = roleRepository.findByName(roleName);
        Role role;
        if (roleOptional.isEmpty()) {
            role = new Role(roleName);
            role = roleRepository.save(role);
        } else {
            role = roleOptional.get();
        }

        // Verificar si ya existe la asignación
        if (!userRoleRepository.existsByUserAndRole(user, role)) {
            UserRole userRole = new UserRole(user, role);
            userRoleRepository.save(userRole);
        }
    }

    /**
     * Elimina un rol de un usuario.
     * 
     * Elimina la relación entre el usuario y el rol especificado.
     * 
     * @param userId ID del usuario
     * @param roleName Nombre del rol a eliminar
     */
    public void removeRoleFromUser(Long userId, String roleName) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        User user = userOptional.get();

        Optional<Role> roleOptional = roleRepository.findByName(roleName);
        if (roleOptional.isEmpty()) {
            return;
        }
        Role role = roleOptional.get();

        Optional<UserRole> userRoleOptional = userRoleRepository.findByUserAndRole(user, role);
        if (userRoleOptional.isPresent()) {
            userRoleRepository.delete(userRoleOptional.get());
        }
    }

    /**
     * Crea un rol si no existe.
     * 
     * @param roleName Nombre del rol a crear
     * @return Role creado o existente
     */
    public Role createRoleIfNotExists(String roleName) {
        Optional<Role> roleOptional = roleRepository.findByName(roleName);
        if (roleOptional.isPresent()) {
            return roleOptional.get();
        }
        Role role = new Role(roleName);
        return roleRepository.save(role);
    }
}

