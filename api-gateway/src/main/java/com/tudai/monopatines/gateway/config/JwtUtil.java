package com.tudai.monopatines.gateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

/**
 * Utilidad para validar tokens JWT en el API Gateway.
 * Valida tokens generados por auth-service antes de enrutar requests.
 * 
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    /**
     * Obtiene la clave secreta para validar los tokens.
     * 
     * @return SecretKey para validar tokens JWT
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
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
     * Verifica que el token no haya expirado.
     * 
     * @param token Token JWT
     * @return true si el token es valido, false en caso contrario
     */
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}

