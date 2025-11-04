package com.tudai.monopatines.accounts.accounts_service.repository;

import com.tudai.monopatines.accounts.accounts_service.entity.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad AccountUser.
 * Proporciona métodos para realizar operaciones CRUD sobre la relación entre cuentas y usuarios.
 * 
 */
@Repository
public interface AccountUserRepository extends JpaRepository<AccountUser, Long> {
}

