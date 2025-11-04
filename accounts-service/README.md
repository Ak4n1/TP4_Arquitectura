# Accounts Service

## Descripcion

Microservicio encargado de gestionar las cuentas y usuarios del sistema de monopatines electricos. Permite crear, editar, eliminar y consultar cuentas y usuarios, asi como gestionar la relacion many-to-many entre cuentas y usuarios. Tambien maneja la funcionalidad de anular cuentas cuando sea necesario. Este servicio es fundamental para el sistema ya que almacena toda la informacion de los usuarios y sus cuentas asociadas a Mercado Pago.

## Dependencias

### spring-boot-starter-web
**Para que sirve:** Framework web de Spring Boot. Permite crear endpoints REST, manejar requests HTTP, y toda la funcionalidad web del servicio.

### spring-boot-starter-data-jpa
**Para que sirve:** Integracion con JPA/Hibernate. Permite trabajar con entidades, repositorios, y mapear objetos Java a tablas de base de datos. Hibernate crea las tablas automaticamente con `ddl-auto=update`.

### mariadb-java-client
**Para que sirve:** Driver JDBC para conectarse a la base de datos MariaDB. Permite que Spring Boot se comunique con la base de datos.

### spring-boot-starter-validation
**Para que sirve:** Validacion de datos de entrada. Permite validar requests (email valido, campos requeridos, etc.) usando anotaciones como `@Email`, `@NotNull`, `@NotBlank`.

### spring-boot-starter-actuator
**Para que sirve:** Health checks y metricas del servicio. Expone endpoints como `/actuator/health` para verificar que el servicio esta funcionando correctamente.

### springdoc-openapi-starter-webmvc-ui
**Para que sirve:** Documentacion automatica de la API con Swagger/OpenAPI. Expone la documentacion interactiva de los endpoints en `/swagger-ui.html` y el esquema OpenAPI en `/v3/api-docs`.

### spring-boot-starter-test
**Para que sirve:** Testing. Permite escribir y ejecutar tests unitarios e integracion del servicio.

### h2
**Para que sirve:** Base de datos en memoria H2 para tests de integracion. Permite ejecutar tests de integracion sin necesidad de una base de datos MariaDB real, mejorando la velocidad y portabilidad de los tests.

### grpc-server-spring-boot-starter
**Para que sirve:** Servidor gRPC para comunicacion interna entre microservicios. Permite exponer servicios gRPC para que otros microservicios (como auth-service) puedan comunicarse internamente sin exponer endpoints REST publicamente.

### grpc-protobuf y grpc-stub
**Para que sirve:** Librerias de gRPC y Protocol Buffers. Permiten definir contratos de servicios mediante archivos `.proto` y generar codigo Java automaticamente para la comunicacion entre microservicios.

### javax.annotation-api
**Para que sirve:** API de anotaciones javax.annotation para compatibilidad con Java 17. Requerida por el codigo generado por gRPC para anotaciones como `@Generated`.

## Endpoints

### Cuentas (Accounts)

#### POST /api/accounts
**Descripcion:** Crea una nueva cuenta asociada a una cuenta de Mercado Pago. Este endpoint es publico (no requiere autenticacion) ya que es parte del proceso de registro de nuevos usuarios.
- **Roles permitidos:** Publico (sin autenticacion requerida)
- **Body:** `AccountRequest` (numero identificatorio, ID de Mercado Pago, saldo inicial)
- **Respuesta:** `AccountResponse` con la cuenta creada (HTTP 201)
- **Errores:** HTTP 409 si el numero identificatorio ya existe

#### GET /api/accounts
**Descripcion:** Obtiene todas las cuentas del sistema.
- **Roles permitidos:** `ROLE_ADMIN`
- **Respuesta:** Lista de `AccountResponse` (HTTP 200)

#### GET /api/accounts/{id}
**Descripcion:** Obtiene una cuenta por su ID.
- **Roles permitidos:** `ROLE_USER`, `ROLE_ADMIN`
- **Path Variable:** `id` - ID de la cuenta
- **Respuesta:** `AccountResponse` con los datos de la cuenta (HTTP 200)
- **Errores:** HTTP 404 si no se encuentra la cuenta

#### GET /api/accounts/active
**Descripcion:** Obtiene todas las cuentas activas (no anuladas).
- **Roles permitidos:** `ROLE_ADMIN`
- **Respuesta:** Lista de `AccountResponse` con solo las cuentas activas (HTTP 200)

#### PUT /api/accounts/{id}
**Descripcion:** Actualiza los datos de una cuenta existente.
- **Roles permitidos:** `ROLE_USER`, `ROLE_ADMIN`
- **Path Variable:** `id` - ID de la cuenta a actualizar
- **Body:** `AccountRequest` con los datos actualizados
- **Respuesta:** `AccountResponse` con la cuenta actualizada (HTTP 200)
- **Errores:** HTTP 404 si no se encuentra la cuenta, HTTP 409 si el nuevo numero identificatorio ya existe

#### PUT /api/accounts/{id}/cancel
**Descripcion:** Anula una cuenta (requerimiento del TP). Marca la cuenta como inactiva y establece la fecha de anulacion. Una cuenta anulada no puede ser utilizada para nuevos viajes.
- **Roles permitidos:** `ROLE_ADMIN`
- **Path Variable:** `id` - ID de la cuenta a anular
- **Respuesta:** `AccountResponse` con la cuenta anulada (HTTP 200)
- **Errores:** HTTP 404 si no se encuentra la cuenta

#### DELETE /api/accounts/{id}
**Descripcion:** Elimina una cuenta del sistema.
- **Roles permitidos:** `ROLE_ADMIN`
- **Path Variable:** `id` - ID de la cuenta a eliminar
- **Respuesta:** Sin contenido (HTTP 204)
- **Errores:** HTTP 404 si no se encuentra la cuenta

### Operaciones de Saldo (Balance)

#### GET /api/accounts/{id}/balance
**Descripcion:** Obtiene el saldo actual de una cuenta.
- **Roles permitidos:** `ROLE_USER`, `ROLE_ADMIN`
- **Path Variable:** `id` - ID de la cuenta
- **Respuesta:** `BalanceResponse` con el saldo actual (HTTP 200)
- **Errores:** HTTP 404 si no se encuentra la cuenta

#### PUT /api/accounts/{id}/balance
**Descripcion:** Carga saldo a una cuenta. Incrementa el saldo actual con el monto especificado.
- **Roles permitidos:** `ROLE_USER`, `ROLE_ADMIN`
- **Path Variable:** `id` - ID de la cuenta
- **Body:** `BalanceRequest` con el monto a cargar (debe ser positivo)
- **Respuesta:** `BalanceResponse` con el saldo actualizado (HTTP 200)
- **Errores:** HTTP 404 si no se encuentra la cuenta, HTTP 400 si la cuenta esta anulada

#### PUT /api/accounts/{id}/balance/deduct?amount={amount}
**Descripcion:** Descuenta saldo de una cuenta (usado por otros microservicios). Se utiliza cuando se activa un monopatin o se finaliza un viaje.
- **Roles permitidos:** `ROLE_EMPLOYEE`, `ROLE_ADMIN`
- **Path Variable:** `id` - ID de la cuenta
- **Query Parameter:** `amount` - Monto a descontar
- **Respuesta:** `BalanceResponse` con el saldo actualizado (HTTP 200)
- **Errores:** HTTP 404 si no se encuentra la cuenta, HTTP 400 si la cuenta esta anulada o no hay saldo suficiente

#### GET /api/accounts/{id}/active
**Descripcion:** Verifica si una cuenta esta activa (para otros microservicios).
- **Roles permitidos:** `ROLE_EMPLOYEE`, `ROLE_ADMIN`
- **Path Variable:** `id` - ID de la cuenta
- **Respuesta:** `Boolean` (true si esta activa, false si esta anulada) (HTTP 200)
- **Errores:** HTTP 404 si no se encuentra la cuenta

### Usuarios (Users)

#### POST /api/accounts/users
**Descripcion:** Crea un nuevo usuario del sistema. El password debe venir ya hasheado desde auth-service. Este endpoint es publico (no requiere autenticacion) ya que es parte del proceso de registro de nuevos usuarios. Normalmente es llamado internamente por auth-service durante el registro.
- **Roles permitidos:** Publico (sin autenticacion requerida)
- **Body:** `CreateUserRequest` (nombre, apellido, email, telefono, password - hasheado)
- **Respuesta:** `UserResponse` con el usuario creado, incluyendo roles asignados (HTTP 201)
- **Nota:** El password debe estar hasheado con BCrypt. El hasheo se realiza en auth-service antes de llamar a este endpoint. Al crear un usuario, se le asigna automaticamente el rol `ROLE_USER`.
- **Errores:** HTTP 409 si el email ya existe (el email debe ser unico)

#### GET /api/accounts/users/all
**Descripcion:** Obtiene todos los usuarios del sistema.
- **Roles permitidos:** `ROLE_ADMIN`
- **Respuesta:** Lista de `UserResponse`, cada uno incluyendo roles asignados (HTTP 200)

#### GET /api/accounts/users/{id}
**Descripcion:** Obtiene un usuario por su ID.
- **Roles permitidos:** `ROLE_USER`, `ROLE_ADMIN`
- **Path Variable:** `id` - ID del usuario
- **Respuesta:** `UserResponse` con los datos del usuario, incluyendo roles asignados (HTTP 200)
- **Errores:** HTTP 404 si no se encuentra el usuario

#### GET /api/accounts/users?email={email}
**Descripcion:** Obtiene un usuario por su email. El email es unico en el sistema.
- **Roles permitidos:** `ROLE_ADMIN`
- **Query Parameter:** `email` - Email del usuario
- **Respuesta:** `UserResponse` con los datos del usuario, incluyendo roles asignados (HTTP 200)
- **Errores:** HTTP 404 si no se encuentra el usuario con ese email

#### PUT /api/accounts/users/{id}
**Descripcion:** Actualiza los datos de un usuario existente. No permite cambiar el password (el password se cambia desde auth-service).
- **Roles permitidos:** `ROLE_USER`, `ROLE_ADMIN`
- **Path Variable:** `id` - ID del usuario a actualizar
- **Body:** `UpdateUserRequest` con los datos actualizados (nombre, apellido, email, telefono - sin password)
- **Respuesta:** `UserResponse` con el usuario actualizado, incluyendo roles asignados (HTTP 200)
- **Nota:** Este endpoint no actualiza el password. El cambio de password se realiza desde auth-service.
- **Errores:** HTTP 404 si no se encuentra el usuario, HTTP 409 si el nuevo email ya existe

#### DELETE /api/accounts/users/{id}
**Descripcion:** Elimina un usuario del sistema.
- **Roles permitidos:** `ROLE_ADMIN`
- **Path Variable:** `id` - ID del usuario a eliminar
- **Respuesta:** Sin contenido (HTTP 204)
- **Errores:** HTTP 404 si no se encuentra el usuario

### Relaciones Cuenta-Usuario (Account-User)

#### POST /api/accounts/{accountId}/users/{userId}
**Descripcion:** Asocia un usuario a una cuenta. Crea una relacion many-to-many que permite que el usuario utilice los creditos cargados en esa cuenta.
- **Roles permitidos:** `ROLE_USER`, `ROLE_ADMIN`
- **Path Variables:** `accountId` - ID de la cuenta, `userId` - ID del usuario
- **Respuesta:** `AccountUserResponse` con informacion sobre la asociacion creada (HTTP 201)
  - Incluye: `accountId`, `userId`, `associatedAt` (fecha de asociacion), `message`
- **Errores:** HTTP 404 si no se encuentra la cuenta o el usuario, HTTP 500 si ya existe la asociacion

#### DELETE /api/accounts/{accountId}/users/{userId}
**Descripcion:** Desasocia un usuario de una cuenta. Elimina la relacion many-to-many, impidiendo que el usuario utilice los creditos de esa cuenta.
- **Roles permitidos:** `ROLE_USER`, `ROLE_ADMIN`
- **Path Variables:** `accountId` - ID de la cuenta, `userId` - ID del usuario
- **Respuesta:** `AccountUserResponse` con informacion sobre la desasociacion realizada (HTTP 200)
  - Incluye: `accountId`, `userId`, `associatedAt` (fecha original de asociacion), `message`
- **Errores:** HTTP 404 si no se encuentra la cuenta o el usuario, HTTP 500 si no existe la asociacion

#### GET /api/accounts/{accountId}/users
**Descripcion:** Obtiene todos los usuarios asociados a una cuenta. Retorna la lista de usuarios que pueden utilizar los creditos cargados en esa cuenta, junto con un mensaje informativo.
- **Roles permitidos:** `ROLE_USER`, `ROLE_ADMIN`
- **Path Variable:** `accountId` - ID de la cuenta
- **Respuesta:** `UsersByAccountResponse` con informacion sobre los usuarios asociados (HTTP 200)
  - Incluye: `accountId`, `users` (lista de usuarios con sus roles), `count` (cantidad de usuarios), `message` (mensaje informativo)
  - Si no hay usuarios: `users` sera una lista vacia y `message` indicara "La cuenta no tiene usuarios asociados"
- **Errores:** HTTP 404 si no se encuentra la cuenta

#### GET /api/accounts/users/{userId}/accounts
**Descripcion:** Obtiene todas las cuentas asociadas a un usuario. Retorna la lista de cuentas cuyos creditos puede utilizar el usuario, junto con un mensaje informativo. Un usuario puede estar asociado a multiples cuentas.
- **Roles permitidos:** `ROLE_USER`, `ROLE_ADMIN`
- **Path Variable:** `userId` - ID del usuario
- **Respuesta:** `AccountsByUserResponse` con informacion sobre las cuentas asociadas (HTTP 200)
  - Incluye: `userId`, `accounts` (lista de cuentas), `count` (cantidad de cuentas), `message` (mensaje informativo)
  - Si no hay cuentas: `accounts` sera una lista vacia y `message` indicara "El usuario no tiene cuentas asociadas"
- **Errores:** HTTP 404 si no se encuentra el usuario

## Comunicacion gRPC (Interna)

Este servicio tambien expone endpoints gRPC para comunicacion interna entre microservicios. Estos endpoints **no estan expuestos publicamente** y solo son accesibles desde otros microservicios dentro de la red Docker.

### Servicio gRPC: UserService

El servicio gRPC expone los siguientes metodos para operaciones de usuarios:

#### CreateUser
**Descripcion:** Crea un nuevo usuario mediante gRPC. Normalmente es llamado por auth-service durante el registro.
- **Request:** `CreateUserRequest` con firstName, lastName, email, phoneNumber, password (hasheado)
- **Response:** `UserResponse` con todos los datos del usuario incluyendo password hasheado (para autenticacion interna)

#### GetUserByEmail
**Descripcion:** Obtiene un usuario por su email mediante gRPC. Normalmente es llamado por auth-service durante el login.
- **Request:** `GetUserByEmailRequest` con email
- **Response:** `UserResponse` con todos los datos del usuario incluyendo password hasheado (para verificacion de credenciales)

#### GetUserById
**Descripcion:** Obtiene un usuario por su ID mediante gRPC. Normalmente es llamado por auth-service para obtener datos del usuario.
- **Request:** `GetUserByIdRequest` con user_id
- **Response:** `UserResponse` con todos los datos del usuario incluyendo password hasheado (para autenticacion interna)

**Nota:** El servicio gRPC expone el password hasheado en las respuestas para uso interno entre microservicios. Esto es necesario para que auth-service pueda validar credenciales durante el login. Los endpoints REST publicos nunca exponen el password.

**Configuracion:**
- Puerto gRPC: `9090` (configurado en `application.properties`)
- El servicio gRPC se expone automaticamente al iniciar el servicio
- El contrato esta definido en `src/main/proto/user_service.proto`
