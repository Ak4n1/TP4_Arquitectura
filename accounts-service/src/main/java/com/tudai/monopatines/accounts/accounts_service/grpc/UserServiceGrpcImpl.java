package com.tudai.monopatines.accounts.accounts_service.grpc;

import com.tudai.monopatines.accounts.accounts_service.dto.CreateUserRequest;
import com.tudai.monopatines.accounts.accounts_service.dto.UserResponse;
import com.tudai.monopatines.accounts.accounts_service.entity.User;
import com.tudai.monopatines.accounts.accounts_service.repository.UserRepository;
import com.tudai.monopatines.accounts.accounts_service.service.UserService;
import com.tudai.monopatines.accounts.grpc.GetUserByEmailRequest;
import com.tudai.monopatines.accounts.grpc.GetUserByIdRequest;
import com.tudai.monopatines.accounts.grpc.UserServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.format.DateTimeFormatter;
import java.util.Collections;

/**
 * Implementacion del servidor gRPC para operaciones de usuarios.
 * Expone los metodos de UserService mediante gRPC para comunicacion interna
 * entre microservicios (no expuestos publicamente).
 * 
 */
@GrpcService
public class UserServiceGrpcImpl extends UserServiceGrpc.UserServiceImplBase {

    private final UserService userService;
    private final UserRepository userRepository;
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public UserServiceGrpcImpl(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public void createUser(com.tudai.monopatines.accounts.grpc.CreateUserRequest request, 
                           StreamObserver<com.tudai.monopatines.accounts.grpc.UserResponse> responseObserver) {
        try {
            CreateUserRequest createRequest = new CreateUserRequest();
            createRequest.setFirstName(request.getFirstName());
            createRequest.setLastName(request.getLastName());
            createRequest.setEmail(request.getEmail());
            createRequest.setPhoneNumber(request.getPhoneNumber());
            createRequest.setPassword(request.getPassword());

            UserResponse userResponse = userService.createUser(createRequest);
            
            User user = userRepository.findByEmail(userResponse.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found after creation"));

            com.tudai.monopatines.accounts.grpc.UserResponse grpcResponse = com.tudai.monopatines.accounts.grpc.UserResponse.newBuilder()
                    .setId(userResponse.getId())
                    .setFirstName(userResponse.getFirstName())
                    .setLastName(userResponse.getLastName())
                    .setEmail(userResponse.getEmail())
                    .setPhoneNumber(userResponse.getPhoneNumber())
                    .setCreatedAt(userResponse.getCreatedAt().format(ISO_FORMATTER))
                    .addAllRoles(userResponse.getRoles() != null ? userResponse.getRoles() : Collections.emptyList())
                    .setPassword(user.getPassword() != null ? user.getPassword() : "")
                    .build();

            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error creating user: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getUserByEmail(GetUserByEmailRequest request, 
                              StreamObserver<com.tudai.monopatines.accounts.grpc.UserResponse> responseObserver) {
        try {
            UserResponse userResponse = userService.getUserByEmail(request.getEmail());
            
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + request.getEmail()));

            com.tudai.monopatines.accounts.grpc.UserResponse grpcResponse = com.tudai.monopatines.accounts.grpc.UserResponse.newBuilder()
                    .setId(userResponse.getId())
                    .setFirstName(userResponse.getFirstName())
                    .setLastName(userResponse.getLastName())
                    .setEmail(userResponse.getEmail())
                    .setPhoneNumber(userResponse.getPhoneNumber())
                    .setCreatedAt(userResponse.getCreatedAt().format(ISO_FORMATTER))
                    .addAllRoles(userResponse.getRoles() != null ? userResponse.getRoles() : Collections.emptyList())
                    .setPassword(user.getPassword() != null ? user.getPassword() : "")
                    .build();

            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("User not found with email: " + request.getEmail())
                    .asRuntimeException());
        }
    }

    @Override
    public void getUserById(GetUserByIdRequest request, 
                           StreamObserver<com.tudai.monopatines.accounts.grpc.UserResponse> responseObserver) {
        try {
            UserResponse userResponse = userService.getUserById(request.getUserId());
            
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

            com.tudai.monopatines.accounts.grpc.UserResponse grpcResponse = com.tudai.monopatines.accounts.grpc.UserResponse.newBuilder()
                    .setId(userResponse.getId())
                    .setFirstName(userResponse.getFirstName())
                    .setLastName(userResponse.getLastName())
                    .setEmail(userResponse.getEmail())
                    .setPhoneNumber(userResponse.getPhoneNumber())
                    .setCreatedAt(userResponse.getCreatedAt().format(ISO_FORMATTER))
                    .addAllRoles(userResponse.getRoles() != null ? userResponse.getRoles() : Collections.emptyList())
                    .setPassword(user.getPassword() != null ? user.getPassword() : "")
                    .build();

            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("User not found with id: " + request.getUserId())
                    .asRuntimeException());
        }
    }
}
