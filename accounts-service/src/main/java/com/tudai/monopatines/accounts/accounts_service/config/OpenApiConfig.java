package com.tudai.monopatines.accounts.accounts_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion de OpenAPI (Swagger) para documentar los endpoints REST.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configura la informacion de la API para Swagger/OpenAPI.
     * 
     * Define el titulo, descripcion, version y otra informacion de contacto
     * que aparecera en la documentacion interactiva de Swagger UI.
     * Tambien incluye informacion sobre seguridad JWT.
     * 
     * @return OpenAPI con la configuracion de la documentacion
     */
    @Bean
    public OpenAPI accountsServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Accounts Service API")
                        .description("API REST para gestionar cuentas y usuarios del sistema de monopatines electricos. " +
                                "Permite realizar operaciones CRUD sobre cuentas y usuarios, gestionar saldos, " +
                                "asociar usuarios a cuentas, y administrar roles de usuarios. " +
                                "Los endpoints estan protegidos por JWT y requieren roles especificos segun el endpoint. " +
                                "La autenticacion se realiza mediante cookies HTTP-only con tokens JWT. " +
                                "NOTA: Este servicio expone endpoints REST publicos y tambien servicios gRPC internos " +
                                "para comunicacion entre microservicios (no expuestos publicamente).")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("TUDAI - Arquitecturas Web")
                                .email("encabojuan@gmail.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token obtenido desde auth-service. El token debe incluirse en el header Authorization como 'Bearer {token}' o en la cookie 'accessToken'.")));
    }
}

