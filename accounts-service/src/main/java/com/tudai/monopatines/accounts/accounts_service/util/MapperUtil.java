package com.tudai.monopatines.accounts.accounts_service.util;

import com.tudai.monopatines.accounts.accounts_service.dto.AccountResponse;
import com.tudai.monopatines.accounts.accounts_service.dto.UserResponse;
import com.tudai.monopatines.accounts.accounts_service.entity.Account;
import com.tudai.monopatines.accounts.accounts_service.entity.User;

/**
 * Clase utilitaria para mapear entidades JPA a DTOs de respuesta.
 * 
 * Esta clase centraliza la lógica de mapeo para evitar duplicación de código
 * y mantener la consistencia en la conversión de entidades a DTOs.
 * 
 */
public class MapperUtil {

    /**
     * Mapea una entidad Account a un DTO AccountResponse.
     * 
     * Este método convierte una entidad JPA (Account) a un DTO de respuesta (AccountResponse).
     * Se utiliza para separar la capa de persistencia de la capa de presentación,
     * evitando exponer anotaciones JPA y relaciones directamente en la API REST.
     * 
     * @param account Entidad Account de la base de datos
     * @return AccountResponse DTO con los datos de la cuenta listos para enviar al cliente
     */
    public static AccountResponse mapAccountToResponse(Account account) {
        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setIdentificationNumber(account.getIdentificationNumber());
        response.setMercadoPagoAccountId(account.getMercadoPagoAccountId());
        response.setCurrentBalance(account.getCurrentBalance());
        response.setActive(account.getActive());
        response.setCreatedAt(account.getCreatedAt());
        response.setCancelledAt(account.getCancelledAt());
        return response;
    }

    /**
     * Mapea una entidad User a un DTO UserResponse.
     * 
     * Este método convierte una entidad JPA (User) a un DTO de respuesta (UserResponse).
     * Se utiliza para separar la capa de persistencia de la capa de presentación,
     * evitando exponer anotaciones JPA y relaciones directamente en la API REST.
     * 
     * Nota: Este método no incluye los roles. Los roles deben agregarse después
     * llamando a roleService.getRolesByUserId() y usando setRoles() en el UserResponse.
     * 
     * @param user Entidad User de la base de datos
     * @return UserResponse DTO con los datos del usuario (sin roles)
     */
    public static UserResponse mapUserToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}

