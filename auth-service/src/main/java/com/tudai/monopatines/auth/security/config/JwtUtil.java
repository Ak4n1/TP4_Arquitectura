package com.tudai.monopatines.auth.security.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

/**
 * Utilidad para generar y validar tokens JWT.
 * Maneja la creacion, validacion y extraccion de informacion de tokens JWT.
 * 
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    /**
     * Obtiene la clave secreta para firmar los tokens.
     * 
     * @return SecretKey para firmar tokens JWT
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Genera un access token JWT para un usuario.
     * 
     * @param email Email del usuario
     * @param userId ID del usuario
     * @param roles Lista de roles del usuario
     * @return Token JWT como String
     */
    public String generateAccessToken(String email, Long userId, List<String> roles) {
        return generateToken(email, userId, roles, expiration);
    }

    /**
     * Genera un refresh token JWT para un usuario.
     * 
     * @param email Email del usuario
     * @param userId ID del usuario
     * @param roles Lista de roles del usuario
     * @return Refresh token JWT como String
     */
    public String generateRefreshToken(String email, Long userId, List<String> roles) {
        return generateToken(email, userId, roles, refreshExpiration);
    }

    /**
     * Genera un token JWT con los datos proporcionados.
     * Solo incluye informacion esencial: userId (subject) y roles.
     * El email no se incluye porque puede cambiar y los microservicios pueden consultarlo por userId.
     * 
     * @param email Email del usuario (no se guarda en el token, solo para compatibilidad)
     * @param userId ID del usuario (se usa como subject)
     * @param roles Lista de roles del usuario
     * @param expirationMillis Tiempo de expiracion en milisegundos
     * @return Token JWT como String
     */
    private String generateToken(String email, Long userId, List<String> roles, Long expirationMillis) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .setSubject(String.valueOf(userId)) // Usar ID del usuario como subject
                .claim("userId", userId)
                .claim("roles", roles)
                // No incluir email: puede cambiar y los microservicios pueden consultarlo por userId
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Extrae el ID del usuario del token JWT (subject).
     * 
     * @param token Token JWT
     * @return ID del usuario
     */
    public Long getUserIdFromToken(String token) {
        String subject = getClaimFromToken(token, Claims::getSubject);
        return Long.parseLong(subject);
    }

    /**
     * Extrae los roles del token JWT.
     * 
     * @param token Token JWT
     * @return Lista de roles
     */
    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("roles", List.class);
    }

    /**
     * Extrae la fecha de expiracion del token JWT.
     * 
     * @param token Token JWT
     * @return Fecha de expiracion
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Extrae un claim especifico del token usando una funcion.
     * 
     * @param token Token JWT
     * @param claimsResolver Funcion para extraer el claim
     * @param <T> Tipo del claim
     * @return Valor del claim
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrae todos los claims del token JWT.
     * 
     * @param token Token JWT
     * @return Claims del token
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Verifica si el token ha expirado.
     * 
     * @param token Token JWT
     * @return true si el token ha expirado, false en caso contrario
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Valida un token JWT.
     * Verifica que el token no haya expirado y que el ID del usuario coincida.
     * 
     * @param token Token JWT
     * @param userId ID del usuario
     * @return true si el token es valido, false en caso contrario
     */
    public Boolean validateToken(String token, Long userId) {
        final Long tokenUserId = getUserIdFromToken(token);
        return (tokenUserId.equals(userId) && !isTokenExpired(token));
    }

    /**
     * Valida un token JWT sin verificar el email.
     * Solo verifica que el token no haya expirado.
     * 
     * @param token Token JWT
     * @return true si el token es valido, false en caso contrario
     */
    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }
}

