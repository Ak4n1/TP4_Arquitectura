package com.tudai.monopatines.accounts.accounts_service.repository;

import com.tudai.monopatines.accounts.accounts_service.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Account.
 * Proporciona métodos para realizar operaciones CRUD sobre cuentas.
 * 
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Verifica si existe una cuenta con el número identificatorio especificado.
     * 
     * @param identificationNumber Número identificatorio de la cuenta
     * @return true si existe una cuenta con ese número identificatorio, false en caso contrario
     */
    boolean existsByIdentificationNumber(String identificationNumber);

    /**
     * Obtiene todas las cuentas activas (no anuladas).
     * 
     * @return Lista de cuentas activas
     */
    List<Account> findByActiveTrue();

    /**
     * Obtiene todas las cuentas inactivas (anuladas).
     * 
     * @return Lista de cuentas inactivas
     */
    List<Account> findByActiveFalse();
}

