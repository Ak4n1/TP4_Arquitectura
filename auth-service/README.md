# Auth Service

## Descripción

Microservicio de autenticación y autorización del sistema. Se encarga de validar credenciales de usuarios mediante login, generar tokens JWT para autenticación, y manejar refresh tokens. Para validar usuarios, se comunica con el servicio de cuentas y usuarios. Este servicio no tiene base de datos propia, sino que utiliza el servicio de cuentas para validar la existencia y credenciales de los usuarios.

## Roles del Sistema

En el proyecto vamos a tener 3 roles:
- **ROLE_EMPLOYEE**: Empleado (Encargado de Mantenimiento)
- **ROLE_USER**: Usuario (Usuario final del sistema)
- **ROLE_ADMIN**: Administrador (Administrador del sistema)

Los roles se guardan en la base de datos del `accounts-service` en las tablas `roles` y `user_roles` (relación many-to-many). El `auth-service` consulta estos roles al hacer login y los incluye en el JWT.

## Autenticación con Cookies

Los tokens JWT se envían y reciben mediante cookies HTTP-only para mayor seguridad. Esto evita que el JavaScript del frontend pueda acceder directamente a los tokens, reduciendo el riesgo de ataques XSS.

- **Access Token**: Se guarda en cookie `accessToken` con `HttpOnly=true`, `Secure=true` (en producción), `SameSite=Strict`
- **Refresh Token**: Se guarda en cookie `refreshToken` con las mismas características de seguridad

## Dependencias

### spring-boot-starter-web
**Para qué sirve:** Framework web de Spring Boot. Permite crear endpoints REST para login, generar tokens, y manejar requests HTTP.

### spring-boot-starter-webflux
**Para qué sirve:** Cliente HTTP reactivo (WebClient). Permite hacer llamadas HTTP asíncronas a otros microservicios, en este caso para comunicarse con accounts-service y validar usuarios.

### jjwt-api, jjwt-impl, jjwt-jackson
**Para qué sirve:** Librería JWT (Java JSON Web Token). Permite generar tokens JWT para autenticación, firmarlos con una clave secreta, y establecer expiración. El servicio usa esto para crear tokens cuando un usuario hace login exitoso.

### spring-boot-starter-validation
**Para qué sirve:** Validación de datos de entrada. Permite validar requests de login (email válido, password no vacío, etc.) usando anotaciones como `@Email`, `@NotNull`, `@NotBlank`.

### spring-boot-starter-test
**Para qué sirve:** Testing. Permite escribir y ejecutar tests unitarios e integración del servicio.

