package com.tudai.monopatines.auth.service;

import com.tudai.monopatines.accounts.grpc.CreateUserRequest;
import com.tudai.monopatines.accounts.grpc.GetUserByEmailRequest;
import com.tudai.monopatines.accounts.grpc.GetUserByIdRequest;
import com.tudai.monopatines.accounts.grpc.UserResponse;
import com.tudai.monopatines.accounts.grpc.UserServiceGrpc;
import com.tudai.monopatines.auth.model.UserResponseGrpc;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Cliente gRPC para comunicarse con accounts-service.
 * Utiliza gRPC para comunicacion interna entre microservicios (no expuesto publicamente).
 * 
 */
@Service
public class AccountsServiceClient {

    @GrpcClient("accounts-service")
    private UserServiceGrpc.UserServiceBlockingStub userServiceStub;

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Crea un usuario en accounts-service mediante gRPC.
     * 
     * @param firstName Nombre del usuario
     * @param lastName Apellido del usuario
     * @param email Email del usuario
     * @param phoneNumber Numero de telefono
     * @param password Password ya hasheado
     * @return UserResponse con los datos del usuario creado
     * @throws RuntimeException si hay error en la comunicacion gRPC
     */
    public UserResponseGrpc createUser(String firstName, String lastName, String email, 
                                       String phoneNumber, String password) {
        try {
            CreateUserRequest request = CreateUserRequest.newBuilder()
                    .setFirstName(firstName)
                    .setLastName(lastName)
                    .setEmail(email)
                    .setPhoneNumber(phoneNumber)
                    .setPassword(password)
                    .build();

            UserResponse response = userServiceStub.createUser(request);
            return mapToUserResponseGrpc(response);
        } catch (StatusRuntimeException e) {
            throw new RuntimeException("Error creating user via gRPC: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene un usuario por email desde accounts-service mediante gRPC.
     * 
     * @param email Email del usuario
     * @return UserResponseGrpc con los datos del usuario
     * @throws RuntimeException si el usuario no existe o hay error en la comunicacion
     */
    public UserResponseGrpc getUserByEmail(String email) {
        try {
            GetUserByEmailRequest request = GetUserByEmailRequest.newBuilder()
                    .setEmail(email)
                    .build();

            UserResponse response = userServiceStub.getUserByEmail(request);
            return mapToUserResponseGrpc(response);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == io.grpc.Status.Code.NOT_FOUND) {
                throw new RuntimeException("User not found with email: " + email);
            }
            throw new RuntimeException("Error getting user via gRPC: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene un usuario por ID desde accounts-service mediante gRPC.
     * 
     * @param userId ID del usuario
     * @return UserResponseGrpc con los datos del usuario
     * @throws RuntimeException si el usuario no existe o hay error en la comunicacion
     */
    public UserResponseGrpc getUserById(Long userId) {
        try {
            GetUserByIdRequest request = GetUserByIdRequest.newBuilder()
                    .setUserId(userId)
                    .build();

            UserResponse response = userServiceStub.getUserById(request);
            return mapToUserResponseGrpc(response);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == io.grpc.Status.Code.NOT_FOUND) {
                throw new RuntimeException("User not found with id: " + userId);
            }
            throw new RuntimeException("Error getting user via gRPC: " + e.getMessage(), e);
        }
    }

    /**
     * Mapea UserResponse de gRPC a UserResponseGrpc (DTO interno).
     */
    private UserResponseGrpc mapToUserResponseGrpc(UserResponse response) {
        UserResponseGrpc userResponse = new UserResponseGrpc();
        userResponse.setId(response.getId());
        userResponse.setFirstName(response.getFirstName());
        userResponse.setLastName(response.getLastName());
        userResponse.setEmail(response.getEmail());
        userResponse.setPhoneNumber(response.getPhoneNumber());
        userResponse.setCreatedAt(LocalDateTime.parse(response.getCreatedAt(), ISO_FORMATTER));
        userResponse.setPassword(response.getPassword());
        
        List<String> roles = new ArrayList<>();
        for (int i = 0; i < response.getRolesCount(); i++) {
            roles.add(response.getRoles(i));
        }
        userResponse.setRoles(roles);
        
        return userResponse;
    }
}
