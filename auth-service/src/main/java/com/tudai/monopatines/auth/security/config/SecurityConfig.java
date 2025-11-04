package com.tudai.monopatines.auth.security.config;

import com.tudai.monopatines.auth.security.services.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Configuracion de seguridad de Spring Security.
 * Define la configuracion de autenticacion, autorizacion y CORS.
 * 
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Configura el SecurityFilterChain para definir las reglas de seguridad.
     * 
     * @param http 
     * @return 
     * @throws Exception 
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler())
            )
            .httpBasic(httpBasic -> httpBasic.disable())
            .formLogin(formLogin -> formLogin.disable())
            .logout(logout -> logout.disable())
            .authenticationProvider(authenticationProvider());

        return http.build();
    }

    /**
     * Configura el DaoAuthenticationProvider para autenticacion.
     * 
     * @return DaoAuthenticationProvider configurado
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Configura el AuthenticationManager.
     * 
     * @param authConfig AuthenticationConfiguration
     * @return AuthenticationManager
     * @throws Exception si hay error en la configuracion
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Configura el PasswordEncoder para BCrypt.
     * 
     * @return BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura CORS para permitir requests desde el frontend.
     * 
     * @return CorsConfigurationSource configurado
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permitir todos los origenes con patrones (no usar allowedOrigins con "*" cuando allowCredentials es true)
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        // IMPORTANTE: allowCredentials debe ser true para cookies, pero no se puede usar con allowedOrigins("*")
        // Por eso usamos allowedOriginPatterns("*") que si permite esta combinacion
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Set-Cookie"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Manejador de excepciones de autenticacion.
     * Maneja excepciones cuando el usuario no esta autenticado.
     * 
     * @return AuthenticationEntryPoint configurado
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (HttpServletRequest request, HttpServletResponse response, 
                org.springframework.security.core.AuthenticationException authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            String errorMessage = String.format(
                "{\"error\":\"Unauthorized\",\"message\":\"%s\",\"path\":\"%s\"}",
                authException.getMessage(),
                request.getRequestURI()
            );
            try {
                response.getWriter().write(errorMessage);
            } catch (IOException e) {
                // Log error si no se puede escribir la respuesta
            }
        };
    }

    /**
     * Manejador de excepciones de acceso denegado.
     * Proporciona informacion detallada sobre por que se denego el acceso.
     * 
     * @return AccessDeniedHandler configurado
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (HttpServletRequest request, HttpServletResponse response, 
                AccessDeniedException accessDeniedException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            String errorMessage = String.format(
                "{\"error\":\"Access Denied\",\"message\":\"%s\",\"path\":\"%s\"}",
                accessDeniedException.getMessage(),
                request.getRequestURI()
            );
            try {
                response.getWriter().write(errorMessage);
            } catch (IOException e) {
                // Log error si no se puede escribir la respuesta
            }
        };
    }
}

