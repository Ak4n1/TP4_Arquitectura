package com.tudai.monopatines.auth.controller;

import com.tudai.monopatines.auth.dto.AuthResponse;
import com.tudai.monopatines.auth.dto.LoginRequest;
import com.tudai.monopatines.auth.dto.RefreshTokenRequest;
import com.tudai.monopatines.auth.dto.RegisterRequest;
import com.tudai.monopatines.auth.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller para endpoints de autenticacion.
 * Expone endpoints REST para registro, login y refresh token.
 * Los tokens JWT se envian mediante cookies HTTP-only para mayor seguridad.
 * 
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint para registrar un nuevo usuario.
     * 
     * @param request Datos del usuario a registrar
     * @param response HttpServletResponse para agregar cookies
     * @return ResponseEntity con AuthResponse y cookies con tokens JWT
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response) {
        
        AuthResponse authResponse = authService.register(request);
        
        String[] tokens = authService.generateTokens(
                authResponse.getUserId(),
                authResponse.getEmail(),
                authResponse.getRoles()
        );
        
        addTokenCookies(response, tokens[0], tokens[1]);
        
        return ResponseEntity.ok(authResponse);
    }

    /**
     * Endpoint para autenticar un usuario existente.
     * 
     * @param request Credenciales del usuario
     * @param response HttpServletResponse para agregar cookies
     * @return ResponseEntity con AuthResponse y cookies con tokens JWT
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        
        AuthResponse authResponse = authService.login(request);
        
        String[] tokens = authService.generateTokens(
                authResponse.getUserId(),
                authResponse.getEmail(),
                authResponse.getRoles()
        );
        
        addTokenCookies(response, tokens[0], tokens[1]);
        
        return ResponseEntity.ok(authResponse);
    }

    /**
     * Endpoint para refrescar tokens JWT.
     * El refresh token se envia mediante cookie HTTP-only.
     * 
     * @param request RefreshTokenRequest (puede estar vacio, el token viene en cookie)
     * @param response HttpServletResponse para agregar cookies
     * @return ResponseEntity con nuevos tokens en cookies
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @RequestBody(required = false) RefreshTokenRequest request,
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {
        
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().body("Refresh token is required");
        }
        
        try {
            String[] tokens = authService.refreshToken(refreshToken);
            addTokenCookies(response, tokens[0], tokens[1]);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid or expired refresh token");
        }
    }

    /**
     * Endpoint para cerrar sesion del usuario.
     * Elimina las cookies de tokens JWT estableciendo MaxAge=0.
     * 
     * @param response HttpServletResponse para eliminar cookies
     * @return ResponseEntity vacio
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        removeTokenCookies(response);
        return ResponseEntity.ok().build();
    }

    /**
     * Agrega cookies HTTP-only con los tokens JWT.
     * 
     * @param response HttpServletResponse
     * @param accessToken Access token JWT
     * @param refreshToken Refresh token JWT
     */
    private void addTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(false);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(24 * 60 * 60);
        response.addCookie(accessTokenCookie);

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(refreshTokenCookie);
    }

    /**
     * Elimina las cookies de tokens JWT estableciendo MaxAge=0.
     * 
     * @param response HttpServletResponse
     */
    private void removeTokenCookies(HttpServletResponse response) {
        Cookie accessTokenCookie = new Cookie("accessToken", "");
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(false);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0);
        response.addCookie(accessTokenCookie);

        Cookie refreshTokenCookie = new Cookie("refreshToken", "");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0);
        response.addCookie(refreshTokenCookie);
    }
}
