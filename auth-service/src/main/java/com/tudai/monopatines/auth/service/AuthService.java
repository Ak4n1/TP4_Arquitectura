package com.tudai.monopatines.auth.service;

import com.tudai.monopatines.auth.dto.AuthResponse;
import com.tudai.monopatines.auth.dto.LoginRequest;
import com.tudai.monopatines.auth.dto.RegisterRequest;
import com.tudai.monopatines.auth.model.UserResponseGrpc;
import com.tudai.monopatines.auth.security.config.JwtUtil;
import com.tudai.monopatines.auth.security.model.UserDetailsImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de autenticacion.
 * Maneja la logica de registro, login y refresh token.
 * 
 */
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AccountsServiceClient accountsServiceClient;

    public AuthService(AuthenticationManager authenticationManager,
                      PasswordEncoder passwordEncoder,
                      JwtUtil jwtUtil,
                      AccountsServiceClient accountsServiceClient) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.accountsServiceClient = accountsServiceClient;
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * 
     * @param request Datos del usuario a registrar
     * @return AuthResponse con los datos del usuario y tokens
     */
    public AuthResponse register(RegisterRequest request) {
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        UserResponseGrpc user = accountsServiceClient.createUser(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPhoneNumber(),
                hashedPassword
        );

        List<String> roles = user.getRoles() != null ? user.getRoles() : List.of();

        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                roles
        );
    }

    /**
     * Autentica un usuario y genera tokens JWT.
     * 
     * @param request Credenciales del usuario
     * @return AuthResponse con los datos del usuario y tokens
     * @throws org.springframework.security.authentication.BadCredentialsException si las credenciales son invalidas
     */
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .toList();

        UserResponseGrpc userGrpc = accountsServiceClient.getUserByEmail(userDetails.getEmail());

        return new AuthResponse(
                userDetails.getId(),
                userDetails.getEmail(),
                userGrpc.getFirstName(),
                userGrpc.getLastName(),
                roles
        );
    }

    /**
     * Genera tokens JWT para un usuario autenticado.
     * 
     * @param userId ID del usuario
     * @param email Email del usuario
     * @param roles Lista de roles del usuario
     * @return Array con [accessToken, refreshToken]
     */
    public String[] generateTokens(Long userId, String email, List<String> roles) {
        String accessToken = jwtUtil.generateAccessToken(email, userId, roles);
        String refreshToken = jwtUtil.generateRefreshToken(email, userId, roles);
        return new String[]{accessToken, refreshToken};
    }

    /**
     * Valida un refresh token y genera nuevos tokens.
     * 
     * @param refreshToken Refresh token a validar
     * @return Array con [accessToken, refreshToken]
     * @throws RuntimeException si el token es invalido o ha expirado
     */
    public String[] refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        List<String> roles = jwtUtil.getRolesFromToken(refreshToken);

        // Obtener email desde accounts-service para generar el nuevo token
        UserResponseGrpc user = accountsServiceClient.getUserById(userId);
        String email = user.getEmail();

        return generateTokens(userId, email, roles);
    }
}
