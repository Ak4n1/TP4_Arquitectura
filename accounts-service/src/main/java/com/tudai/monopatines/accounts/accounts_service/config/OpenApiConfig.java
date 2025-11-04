package com.tudai.monopatines.accounts.accounts_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI (Swagger) para documentar los endpoints REST.
 * 
 * Esta configuración define la información general de la API que se mostrará
 * en la interfaz de Swagger UI.
 * 
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configura la información de la API para Swagger/OpenAPI.
     * 
     * Define el título, descripción, versión y otra información de contacto
     * que aparecerá en la documentación interactiva de Swagger UI.
     * 
     * @return OpenAPI con la configuración de la documentación
     */
    @Bean
    public OpenAPI accountsServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Accounts Service API")
                        .description("API REST para gestionar cuentas y usuarios del sistema de monopatines eléctricos. " +
                                "Permite realizar operaciones CRUD sobre cuentas y usuarios, gestionar saldos, " +
                                "asociar usuarios a cuentas, y administrar roles de usuarios.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("TUDAI - Arquitecturas Web")
                                .email("tudai@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}

