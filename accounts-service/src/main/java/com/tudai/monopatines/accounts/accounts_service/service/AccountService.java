package com.tudai.monopatines.accounts.accounts_service.service;

import com.tudai.monopatines.accounts.accounts_service.dto.AccountRequest;
import com.tudai.monopatines.accounts.accounts_service.dto.AccountResponse;
import com.tudai.monopatines.accounts.accounts_service.dto.BalanceRequest;
import com.tudai.monopatines.accounts.accounts_service.dto.BalanceResponse;

import java.util.List;

/**
 * Interfaz del servicio para gestionar cuentas.
 * Define los métodos para realizar operaciones CRUD sobre cuentas,
 * gestionar saldos y anular cuentas.
 * 
 */
public interface AccountService {

    /**
     * Crea una nueva cuenta asociada a una cuenta de Mercado Pago.
     * 
     * @param request Datos de la cuenta a crear (número identificatorio, ID de Mercado Pago, saldo inicial)
     * @return AccountResponse con la cuenta creada
     * @throws AccountAlreadyExistsException si el número identificatorio ya existe
     */
    AccountResponse createAccount(AccountRequest request);

    /**
     * Obtiene una cuenta por su ID.
     * 
     * @param id ID de la cuenta a buscar
     * @return AccountResponse con los datos de la cuenta
     * @throws AccountNotFoundException si no se encuentra la cuenta
     */
    AccountResponse getAccountById(Long id);

    /**
     * Obtiene todas las cuentas.
     * 
     * @return Lista de AccountResponse con todas las cuentas
     */
    List<AccountResponse> getAllAccounts();

    /**
     * Obtiene todas las cuentas activas (no anuladas).
     * 
     * @return Lista de AccountResponse con todas las cuentas activas
     */
    List<AccountResponse> getActiveAccounts();

    /**
     * Actualiza los datos de una cuenta existente.
     * 
     * @param id ID de la cuenta a actualizar
     * @param request Datos actualizados de la cuenta
     * @return AccountResponse con la cuenta actualizada
     * @throws AccountNotFoundException si no se encuentra la cuenta
     * @throws AccountAlreadyExistsException si el nuevo número identificatorio ya existe
     */
    AccountResponse updateAccount(Long id, AccountRequest request);

    /**
     * Anula una cuenta (requerimiento del TP).
     * Marca la cuenta como inactiva y establece la fecha de anulación.
     * Una cuenta anulada no puede ser utilizada para nuevos viajes.
     * 
     * @param id ID de la cuenta a anular
     * @return AccountResponse con la cuenta anulada
     * @throws AccountNotFoundException si no se encuentra la cuenta
     */
    AccountResponse cancelAccount(Long id);

    /**
     * Carga saldo a una cuenta.
     * Incrementa el saldo actual de la cuenta con el monto especificado.
     * 
     * @param id ID de la cuenta
     * @param request Monto a cargar (debe ser positivo)
     * @return BalanceResponse con el saldo actualizado
     * @throws AccountNotFoundException si no se encuentra la cuenta
     * @throws AccountInactiveException si la cuenta está anulada
     */
    BalanceResponse loadBalance(Long id, BalanceRequest request);

    /**
     * Descuenta saldo de una cuenta (usado por otros servicios).
     * Se utiliza cuando se activa un monopatín o se finaliza un viaje.
     * 
     * @param id ID de la cuenta
     * @param amount Monto a descontar
     * @return BalanceResponse con el saldo actualizado
     * @throws AccountNotFoundException si no se encuentra la cuenta
     * @throws AccountInactiveException si la cuenta está anulada
     * @throws InsufficientBalanceException si no hay saldo suficiente
     */
    BalanceResponse deductBalance(Long id, Double amount);

    /**
     * Obtiene el saldo actual de una cuenta.
     * 
     * @param id ID de la cuenta
     * @return BalanceResponse con el saldo actual
     * @throws AccountNotFoundException si no se encuentra la cuenta
     */
    BalanceResponse getBalance(Long id);

    /**
     * Verifica si una cuenta está activa (para otros servicios).
     * 
     * @param id ID de la cuenta
     * @return true si la cuenta está activa, false si está anulada
     * @throws AccountNotFoundException si no se encuentra la cuenta
     */
    boolean isAccountActive(Long id);

    /**
     * Elimina una cuenta del sistema.
     * 
     * @param id ID de la cuenta a eliminar
     * @throws AccountNotFoundException si no se encuentra la cuenta
     */
    void deleteAccount(Long id);
}
