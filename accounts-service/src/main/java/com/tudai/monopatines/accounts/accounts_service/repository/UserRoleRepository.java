package com.tudai.monopatines.accounts.accounts_service.repository;

import com.tudai.monopatines.accounts.accounts_service.entity.Role;
import com.tudai.monopatines.accounts.accounts_service.entity.User;
import com.tudai.monopatines.accounts.accounts_service.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad UserRole (relación many-to-many entre User y Role).
 * 
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    /**
     * Busca todos los roles asignados a un usuario.
     * 
     * @param user Usuario
     * @return Lista de UserRole con todos los roles del usuario
     */
    List<UserRole> findByUser(User user);

    /**
     * Busca todos los roles asignados a un usuario por su ID.
     * 
     * @param userId ID del usuario
     * @return Lista de UserRole con todos los roles del usuario
     */
    List<UserRole> findByUserId(Long userId);

    /**
     * Busca la relación entre un usuario y un rol específico.
     * 
     * @param user Usuario
     * @param role Rol
     * @return Optional con la relación encontrada o vacío si no existe
     */
    Optional<UserRole> findByUserAndRole(User user, Role role);

    /**
     * Verifica si existe una relación entre un usuario y un rol.
     * 
     * @param user Usuario
     * @param role Rol
     * @return true si existe la relación, false si no existe
     */
    boolean existsByUserAndRole(User user, Role role);

    /**
     * Elimina todas las relaciones de roles de un usuario.
     * 
     * @param user Usuario
     */
    void deleteByUser(User user);
}

