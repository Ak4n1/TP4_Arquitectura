package com.tudai.monopatines.gateway.filter;

import com.tudai.monopatines.gateway.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.*;

/**
 * Filtro global para validar tokens JWT y roles en el API Gateway.
 * Intercepta todas las requests y valida tokens JWT y roles antes de enrutar.
 * 
 */
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    // Rutas publicas que no requieren autenticacion (formato: "METHOD:path")
    private static final Set<String> PUBLIC_PATHS = Set.of(
            "POST:/api/auth/register",
            "POST:/api/auth/login",
            "POST:/api/auth/refresh",
            "POST:/api/auth/logout",
            "POST:/api/accounts", 
            "POST:/api/accounts/users", 
            "GET:/swagger-ui",
            "GET:/v3/api-docs",
            "GET:/actuator"
    );

    // Configuracion de roles requeridos por ruta y metodo HTTP
    // Key: "METHOD:pathPattern" (ej: "GET:/api/accounts/users/all")
    // Value: Lista de roles que pueden acceder (si el usuario tiene alguno, puede acceder)
    private static final Map<String, List<String>> ROLE_REQUIREMENTS = new HashMap<>();

    static {
        // Accounts endpoints
        ROLE_REQUIREMENTS.put("GET:/api/accounts", List.of("ROLE_ADMIN")); // GET /api/accounts (todos)
        ROLE_REQUIREMENTS.put("GET:/api/accounts/active", List.of("ROLE_ADMIN")); // GET /api/accounts/active
        ROLE_REQUIREMENTS.put("PUT:/api/accounts/{id}/cancel", List.of("ROLE_ADMIN")); // PUT /api/accounts/{id}/cancel
        ROLE_REQUIREMENTS.put("DELETE:/api/accounts/{id}", List.of("ROLE_ADMIN")); // DELETE /api/accounts/{id}
        
        // Accounts - GET/PUT /api/accounts/{id} (usuario o admin)
        ROLE_REQUIREMENTS.put("GET:/api/accounts/{id}", List.of("ROLE_USER", "ROLE_ADMIN"));
        ROLE_REQUIREMENTS.put("PUT:/api/accounts/{id}", List.of("ROLE_USER", "ROLE_ADMIN"));
        
        // Balance endpoints
        ROLE_REQUIREMENTS.put("GET:/api/accounts/{id}/balance", List.of("ROLE_USER", "ROLE_ADMIN"));
        ROLE_REQUIREMENTS.put("PUT:/api/accounts/{id}/balance", List.of("ROLE_USER", "ROLE_ADMIN"));
        ROLE_REQUIREMENTS.put("PUT:/api/accounts/{id}/balance/deduct", List.of("ROLE_EMPLOYEE", "ROLE_ADMIN"));
        ROLE_REQUIREMENTS.put("GET:/api/accounts/{id}/active", List.of("ROLE_EMPLOYEE", "ROLE_ADMIN"));
        
        // Users endpoints
        ROLE_REQUIREMENTS.put("GET:/api/accounts/users/all", List.of("ROLE_ADMIN")); // GET /api/accounts/users/all
        ROLE_REQUIREMENTS.put("GET:/api/accounts/users", List.of("ROLE_ADMIN")); // GET /api/accounts/users?email={email}
        ROLE_REQUIREMENTS.put("GET:/api/accounts/users/{id}", List.of("ROLE_USER", "ROLE_ADMIN"));
        ROLE_REQUIREMENTS.put("PUT:/api/accounts/users/{id}", List.of("ROLE_USER", "ROLE_ADMIN"));
        ROLE_REQUIREMENTS.put("DELETE:/api/accounts/users/{id}", List.of("ROLE_ADMIN"));
        
        // Account-User endpoints
        ROLE_REQUIREMENTS.put("GET:/api/accounts/{id}/users", List.of("ROLE_USER", "ROLE_ADMIN"));
        ROLE_REQUIREMENTS.put("GET:/api/accounts/users/{userId}/accounts", List.of("ROLE_USER", "ROLE_ADMIN"));
        ROLE_REQUIREMENTS.put("POST:/api/accounts/{id}/users/{userId}", List.of("ROLE_USER", "ROLE_ADMIN"));
        ROLE_REQUIREMENTS.put("DELETE:/api/accounts/{id}/users/{userId}", List.of("ROLE_USER", "ROLE_ADMIN"));
    }

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        HttpMethod method = request.getMethod();

        // Verificar si la ruta es publica
        if (isPublicPath(path, method)) {
            return chain.filter(exchange);
        }

        // Obtener el token del header Authorization o de la cookie
        String token = getTokenFromRequest(request);

        if (!StringUtils.hasText(token)) {
            return onError(exchange, "Token JWT no encontrado", HttpStatus.UNAUTHORIZED);
        }

        // Validar el token
        if (!jwtUtil.validateToken(token)) {
            return onError(exchange, "Token JWT invalido o expirado", HttpStatus.UNAUTHORIZED);
        }

        // Extraer roles del token
        List<String> userRoles;
        try {
            userRoles = jwtUtil.getRolesFromToken(token);
        } catch (Exception e) {
            return onError(exchange, "Error al extraer roles del token", HttpStatus.UNAUTHORIZED);
        }

        // Validar roles requeridos para la ruta
        if (!hasRequiredRole(method, path, userRoles)) {
            String normalizedPath = normalizePath(path);
            String exactKey = method.name() + ":" + normalizedPath;
            List<String> requiredRoles = ROLE_REQUIREMENTS.get(exactKey);
            
            String errorMessage = "No tiene permisos para acceder a este recurso";
            if (requiredRoles != null && !requiredRoles.isEmpty()) {
                errorMessage += String.format(". Roles requeridos: %s", String.join(", ", requiredRoles));
            }
            if (userRoles != null && !userRoles.isEmpty()) {
                errorMessage += String.format(". Sus roles: %s", String.join(", ", userRoles));
            }
            
            return onError(exchange, errorMessage, HttpStatus.FORBIDDEN);
        }

        // Agregar informacion del usuario al header para que los microservicios puedan usarla
        // Las cookies se preservan automaticamente en el enrutamiento
        // NOTA: El email no se incluye porque no esta en el token. Los microservicios pueden consultarlo por userId.
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);

            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", String.valueOf(userId))
                    .header("X-User-Roles", String.join(",", userRoles))
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        } catch (Exception e) {
            return onError(exchange, "Error al procesar token JWT", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Verifica si una ruta es publica segun el metodo HTTP.
     * 
     * @param path Ruta a verificar
     * @param method Metodo HTTP
     * @return true si la ruta es publica, false en caso contrario
     */
    private boolean isPublicPath(String path, HttpMethod method) {
        String key = method.name() + ":" + path;
        
        // Verificar coincidencia exacta
        if (PUBLIC_PATHS.contains(key)) {
            return true;
        }
        
        // Verificar prefijos para swagger y docs
        if (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs") || path.startsWith("/actuator")) {
            return method == HttpMethod.GET;
        }
        
        return false;
    }

    /**
     * Verifica si el usuario tiene alguno de los roles requeridos para la ruta.
     * 
     * @param method Metodo HTTP (GET, POST, PUT, DELETE)
     * @param path Ruta completa (ej: /api/accounts/123)
     * @param userRoles Roles del usuario autenticado
     * @return true si el usuario tiene algun rol requerido, false en caso contrario
     */
    private boolean hasRequiredRole(HttpMethod method, String path, List<String> userRoles) {
        if (userRoles == null || userRoles.isEmpty()) {
            return false;
        }

        // Normalizar path: convertir /api/accounts/123 a /api/accounts/{id}
        String normalizedPath = normalizePath(path);
        
        // Buscar coincidencia exacta primero: "METHOD:path"
        String exactKey = method.name() + ":" + normalizedPath;
        List<String> requiredRoles = ROLE_REQUIREMENTS.get(exactKey);
        
        if (requiredRoles != null) {
            return userRoles.stream().anyMatch(requiredRoles::contains);
        }

        // Si no hay coincidencia exacta, buscar por patrones
        // Por ejemplo, /api/accounts/{id} deberia coincidir con /api/accounts/123
        for (Map.Entry<String, List<String>> entry : ROLE_REQUIREMENTS.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(method.name() + ":")) {
                String pattern = key.substring(method.name().length() + 1);
                if (matchesPattern(normalizedPath, pattern)) {
                    return userRoles.stream().anyMatch(entry.getValue()::contains);
                }
            }
        }

        // Si no hay configuracion especifica, denegar acceso por defecto (seguridad)
        return false;
    }

    /**
     * Normaliza el path reemplazando IDs numericos por {id}.
     * Ej: /api/accounts/123 -> /api/accounts/{id}
     * 
     * @param path Path original
     * @return Path normalizado
     */
    private String normalizePath(String path) {
        // Reemplazar numeros al final de rutas por {id}
        String normalized = path.replaceAll("/\\d+$", "/{id}");
        // Reemplazar numeros en medio de rutas por {id}
        normalized = normalized.replaceAll("/\\d+/", "/{id}/");
        // Reemplazar numeros en userId
        normalized = normalized.replaceAll("users/\\d+", "users/{id}");
        normalized = normalized.replaceAll("users/\\d+/", "users/{userId}/");
        return normalized;
    }

    /**
     * Verifica si un path normalizado coincide con un patron.
     * Ej: /api/accounts/{id} coincide con /api/accounts/{id}
     * 
     * @param path Path normalizado
     * @param pattern Patron a verificar
     * @return true si coincide, false en caso contrario
     */
    private boolean matchesPattern(String path, String pattern) {
        // Comparacion simple: si el path normalizado coincide con el patron
        return path.equals(pattern) || path.startsWith(pattern + "/");
    }

    /**
     * Obtiene el token JWT del request.
     * Busca primero en la cookie accessToken (principalmente),
     * luego en el header Authorization como fallback.
     * 
     * @param request ServerHttpRequest
     * @return Token JWT o null si no se encuentra
     */
    private String getTokenFromRequest(ServerHttpRequest request) {
        // Buscar primero en la cookie accessToken (principal metodo)
        if (request.getCookies().containsKey("accessToken")) {
            return request.getCookies().getFirst("accessToken").getValue();
        }

        // Buscar en el header Authorization como fallback
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }

    /**
     * Maneja errores de autenticacion o autorizacion.
     * 
     * @param exchange ServerWebExchange
     * @param message Mensaje de error
     * @param status Codigo HTTP de error
     * @return Mono<Void>
     */
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", "application/json");
        return response.writeWith(Mono.just(response.bufferFactory().wrap(
                ("{\"error\":\"" + message + "\"}").getBytes()
        )));
    }

    // Ejecuta antes que otros filtros
    @Override
    public int getOrder() {
        return -100; 
    }
}
