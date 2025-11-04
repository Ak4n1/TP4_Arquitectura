# Auth Service

## Descripcion

Microservicio de autenticacion y autorizacion del sistema. Se encarga de validar credenciales de usuarios mediante login, generar tokens JWT para autenticacion, y manejar refresh tokens. Para validar usuarios, se comunica con el servicio de cuentas y usuarios mediante gRPC. Este servicio no tiene base de datos propia, sino que utiliza el servicio de cuentas para validar la existencia y credenciales de los usuarios.

## Roles del Sistema

En el proyecto vamos a tener 3 roles:
- **ROLE_EMPLOYEE**: Empleado (Encargado de Mantenimiento)
- **ROLE_USER**: Usuario (Usuario final del sistema)
- **ROLE_ADMIN**: Administrador (Administrador del sistema)

Los roles se guardan en la base de datos del `accounts-service` en las tablas `roles` y `user_roles` (relacion many-to-many). El `auth-service` consulta estos roles al hacer login y los incluye en el JWT.

## Autenticacion con Cookies

Los tokens JWT se envian y reciben mediante cookies HTTP-only para mayor seguridad. Esto evita que el JavaScript del frontend pueda acceder directamente a los tokens, reduciendo el riesgo de ataques XSS.

- **Access Token**: Se guarda en cookie `accessToken` con `HttpOnly=true`, `Secure=false` (en desarrollo), `Path=/`, expira en 24 horas
- **Refresh Token**: Se guarda en cookie `refreshToken` con las mismas caracteristicas de seguridad, expira en 7 dias

## Dependencias

### spring-boot-starter-web
**Para que sirve:** Framework web de Spring Boot. Permite crear endpoints REST para login, generar tokens, y manejar requests HTTP.

### spring-boot-starter-security
**Para que sirve:** Spring Security. Permite configurar autenticacion y autorizacion, incluyendo UserDetailsService y PasswordEncoder para validar credenciales.

### spring-boot-starter-webflux
**Para que sirve:** Cliente HTTP reactivo (WebClient). No se usa actualmente ya que la comunicacion con accounts-service se realiza mediante gRPC.

### jjwt-api, jjwt-impl, jjwt-jackson
**Para que sirve:** Libreria JWT (Java JSON Web Token). Permite generar tokens JWT para autenticacion, firmarlos con una clave secreta, y establecer expiracion. El servicio usa esto para crear tokens cuando un usuario hace login exitoso.

### spring-boot-starter-validation
**Para que sirve:** Validacion de datos de entrada. Permite validar requests de login (email valido, password no vacio, etc.) usando anotaciones como `@Email`, `@NotNull`, `@NotBlank`.

### springdoc-openapi-starter-webmvc-ui
**Para que sirve:** Documentacion automatica de la API con Swagger/OpenAPI. Expone la documentacion interactiva de los endpoints en `/swagger-ui/index.html` y el esquema OpenAPI en `/v3/api-docs`.

### grpc-client-spring-boot-starter
**Para que sirve:** Cliente gRPC para comunicacion interna entre microservicios. Permite llamar a accounts-service mediante gRPC para validar usuarios y crear nuevos usuarios sin exponer endpoints REST publicamente.

### grpc-core, grpc-protobuf, grpc-stub, grpc-netty-shaded
**Para que sirve:** Librerias de gRPC y Protocol Buffers. Permiten definir contratos de servicios mediante archivos `.proto` y generar codigo Java automaticamente para la comunicacion entre microservicios.

### javax.annotation-api
**Para que sirve:** API de anotaciones javax.annotation para compatibilidad con Java 17. Requerida por el codigo generado por gRPC para anotaciones como `@Generated`.

### spring-boot-starter-test
**Para que sirve:** Testing. Permite escribir y ejecutar tests unitarios e integracion del servicio.

## Endpoints

Todos los endpoints son PUBLICOS (no requieren autenticacion) ya que son para autenticacion.

### POST /auth/register
**Descripcion:** Registra un nuevo usuario en el sistema. Crea el usuario en accounts-service mediante gRPC y genera tokens JWT que se envian mediante cookies HTTP-only. El usuario se crea con el rol ROLE_USER por defecto.
- **Roles permitidos:** Publico (sin autenticacion requerida)
- **Body:** `RegisterRequest` (firstName, lastName, email, phoneNumber, password)
- **Respuesta:** `AuthResponse` con informacion del usuario y sus roles (HTTP 200)
  - Los tokens JWT se envian en las cookies: `accessToken` (expira en 24 horas) y `refreshToken` (expira en 7 dias)
- **Errores:** HTTP 409 si el email ya existe, HTTP 400 si los datos son invalidos

### POST /auth/login
**Descripcion:** Autentica un usuario existente mediante email y password. Valida las credenciales consultando accounts-service mediante gRPC. Si las credenciales son validas, genera tokens JWT que se envian mediante cookies HTTP-only.
- **Roles permitidos:** Publico (sin autenticacion requerida)
- **Body:** `LoginRequest` (email, password)
- **Respuesta:** `AuthResponse` con informacion del usuario y sus roles asignados (HTTP 200)
  - Los tokens JWT se envian en las cookies: `accessToken` (expira en 24 horas) y `refreshToken` (expira en 7 dias)
- **Errores:** HTTP 401 si las credenciales son invalidas, HTTP 404 si el usuario no existe, HTTP 400 si los datos son invalidos

### POST /auth/refresh
**Descripcion:** Refresca los tokens JWT usando un refresh token valido. El refresh token se envia mediante cookie HTTP-only o en el body del request. Si el refresh token es valido, genera nuevos tokens JWT (accessToken y refreshToken) que se envian mediante cookies HTTP-only.
- **Roles permitidos:** Publico (sin autenticacion requerida)
- **Body:** `RefreshTokenRequest` (opcional, puede venir en cookie) o cookie `refreshToken`
- **Respuesta:** Sin contenido (HTTP 200)
  - Los nuevos tokens JWT se envian en las cookies: `accessToken` (expira en 24 horas) y `refreshToken` (expira en 7 dias)
- **Errores:** HTTP 400 si el refresh token es invalido, expirado o no proporcionado

### POST /auth/logout
**Descripcion:** Cierra la sesion del usuario eliminando las cookies de tokens JWT. Con JWT stateless, el logout se maneja principalmente del lado del cliente eliminando las cookies, pero este endpoint proporciona una forma estandar de cerrar sesion.
- **Roles permitidos:** Publico (sin autenticacion requerida)
- **Respuesta:** Sin contenido (HTTP 200)
  - Elimina las cookies `accessToken` y `refreshToken` estableciendo `MaxAge=0`

## Comunicacion gRPC (Interna)

Este servicio se comunica con `accounts-service` mediante gRPC para operaciones internas. Estos llamados **no estan expuestos publicamente** y solo son accesibles desde otros microservicios dentro de la red Docker.

### Servicio gRPC: UserService

El servicio utiliza el cliente gRPC para llamar a `accounts-service` y realizar las siguientes operaciones:

#### CreateUser
**Descripcion:** Crea un nuevo usuario mediante gRPC. Normalmente es llamado durante el registro.
- **Request:** `CreateUserRequest` con firstName, lastName, email, phoneNumber, password (hasheado)
- **Response:** `UserResponse` con todos los datos del usuario incluyendo password hasheado (para autenticacion interna)

#### GetUserByEmail
**Descripcion:** Obtiene un usuario por su email mediante gRPC. Normalmente es llamado durante el login.
- **Request:** `GetUserByEmailRequest` con email
- **Response:** `UserResponse` con todos los datos del usuario incluyendo password hasheado (para verificacion de credenciales)

#### GetUserById
**Descripcion:** Obtiene un usuario por su ID mediante gRPC. Normalmente es llamado para obtener datos del usuario durante el refresh token.
- **Request:** `GetUserByIdRequest` con user_id
- **Response:** `UserResponse` con todos los datos del usuario incluyendo password hasheado (para autenticacion interna)

**Nota:** El servicio gRPC expone el password hasheado en las respuestas para uso interno entre microservicios. Esto es necesario para que auth-service pueda validar credenciales durante el login. Los endpoints REST publicos nunca exponen el password.

**Configuracion:**
- Direccion gRPC: `accounts-service:9090` (configurado en `application.properties`)
- El cliente gRPC se conecta automaticamente al iniciar el servicio
- El contrato esta definido en `src/main/proto/user_service.proto`

