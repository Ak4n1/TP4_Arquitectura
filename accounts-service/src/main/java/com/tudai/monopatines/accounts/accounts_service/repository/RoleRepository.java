package com.tudai.monopatines.accounts.accounts_service.repository;

import com.tudai.monopatines.accounts.accounts_service.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad Role.
 * 
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Busca un rol por su nombre.
     * 
     * @param name Nombre del rol (ej: "ROLE_USER", "ROLE_ADMIN")
     * @return Optional con el rol encontrado o vacío si no existe
     */
    Optional<Role> findByName(String name);

    /**
     * Verifica si existe un rol con el nombre especificado.
     * 
     * @param name Nombre del rol
     * @return true si existe, false si no existe
     */
    boolean existsByName(String name);
}

