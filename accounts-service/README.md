# Accounts Service

## Descripción

Microservicio encargado de gestionar las cuentas y usuarios del sistema de monopatines eléctricos. Permite crear, editar, eliminar y consultar cuentas y usuarios, así como gestionar la relación many-to-many entre cuentas y usuarios. También maneja la funcionalidad de anular cuentas cuando sea necesario. Este servicio es fundamental para el sistema ya que almacena toda la información de los usuarios y sus cuentas asociadas a Mercado Pago.

## Dependencias

### spring-boot-starter-web
**Para qué sirve:** Framework web de Spring Boot. Permite crear endpoints REST, manejar requests HTTP, y toda la funcionalidad web del servicio.

### spring-boot-starter-data-jpa
**Para qué sirve:** Integración con JPA/Hibernate. Permite trabajar con entidades, repositorios, y mapear objetos Java a tablas de base de datos. Hibernate crea las tablas automáticamente con `ddl-auto=update`.

### mariadb-java-client
**Para qué sirve:** Driver JDBC para conectarse a la base de datos MariaDB. Permite que Spring Boot se comunique con la base de datos.

### spring-boot-starter-validation
**Para qué sirve:** Validación de datos de entrada. Permite validar requests (email válido, campos requeridos, etc.) usando anotaciones como `@Email`, `@NotNull`, `@NotBlank`.

### spring-boot-starter-actuator
**Para qué sirve:** Health checks y métricas del servicio. Expone endpoints como `/actuator/health` para verificar que el servicio está funcionando correctamente.

### spring-boot-starter-test
**Para qué sirve:** Testing. Permite escribir y ejecutar tests unitarios e integración del servicio.

## Endpoints

### Cuentas (Accounts)

#### POST /api/accounts
**Descripción:** Crea una nueva cuenta asociada a una cuenta de Mercado Pago. Este endpoint es público (no requiere autenticación) ya que es parte del proceso de registro de nuevos usuarios.
- **Roles permitidos:** Público (sin autenticación requerida)
- **Body:** `AccountRequest` (número identificatorio, ID de Mercado Pago, saldo inicial)
- **Respuesta:** `AccountResponse` con la cuenta creada (HTTP 201)
- **Errores:** HTTP 409 si el número identificatorio ya existe

#### GET /api/accounts
**Descripción:** Obtiene todas las cuentas del sistema.
- **Roles permitidos:** `ROLE_ADMIN`
- **Respuesta:** Lista de `AccountResponse` (HTTP 200)

#### GET /api/accounts/{id}
**Descripción:** Obtiene una cuenta por su ID.
- **Roles permitidos:** `ROLE_USER`, `ROLE_ADMIN`
- **Path Variable:** `id` - ID de la cuenta
- **Respuesta:** `AccountResponse` con los datos de la cuenta (HTTP 200)
- **Errores:** HTTP 404 si no se encuentra la cuenta

#### GET /api/accounts/active
**Descripción:** Obtiene todas las cuentas activas (no anuladas).
- **Roles permitidos:** `ROLE_ADMIN`
- **Respuesta:** Lista de `AccountResponse` con solo las cuentas activas (HTTP 200)

#### PUT /api/accounts/{id}
**Descripción:** Actualiza los datos de una cuenta existente.
- **Roles permitidos:** `ROLE_USER`, `ROLE_ADMIN`
- **Path Variable:** `id` - ID de la cuenta a actualizar
- **Body:** `AccountRequest` con los datos actualizados
- **Respuesta:** `AccountResponse` con la cuenta actualizada (HTTP 200)
- **Errores:** HTTP 404 si no se encuentra la cuenta, HTTP 409 si el nuevo número identificatorio ya existe

#### PUT /api/accounts/{id}/cancel
**Descripción:** Anula una cuenta (requerimiento del TP). Marca la cuenta como inactiva y establece la fecha de anulación. Una cuenta anulada no puede ser utilizada para nuevos viajes.
- **Roles permitidos:** `ROLE_ADMIN`
- **Path Variable:** `id` - ID de la cuenta a anular
- **Respuesta:** `AccountResponse` con la cuenta anulada (HTTP 200)
- **Errores:** HTTP 404 si no se encuentra la cuenta

#### DELETE /api/accounts/{id}
**Descripción:** Elimina una cuenta del sistema.
- **Roles permitidos:** `ROLE_ADMIN`
- **Path Variable:** `id` - ID de la cuenta a eliminar
- **Respuesta:** Sin contenido (HTTP 204)
- **Errores:** HTTP 404 si no se encuentra la cuenta

### Operaciones de Saldo (Balance)

#### GET /api/accounts/{id}/balance
**Descripción:** Obtiene el saldo actual de una cuenta.
- **Roles permitidos:** `ROLE_USER`, `ROLE_ADMIN`
- **Path Variable:** `id` - ID de la cuenta
- **Respuesta:** `BalanceResponse` con el saldo actual (HTTP 200)
- **Errores:** HTTP 404 si no se encuentra la cuenta

#### PUT /api/accounts/{id}/balance
**Descripción:** Carga saldo a una cuenta. Incrementa el saldo actual con el monto especificado.
- **Roles permitidos:** `ROLE_USER`, `ROLE_ADMIN`
- **Path Variable:** `id` - ID de la cuenta
- **Body:** `BalanceRequest` con el monto a cargar (debe ser positivo)
- **Respuesta:** `BalanceResponse` con el saldo actualizado (HTTP 200)
- **Errores:** HTTP 404 si no se encuentra la cuenta, HTTP 400 si la cuenta está anulada

#### PUT /api/accounts/{id}/balance/deduct?amount={amount}
**Descripción:** Descuenta saldo de una cuenta (usado por otros microservicios). Se utiliza cuando se activa un monopatín o se finaliza un viaje.
- **Roles permitidos:** `ROLE_EMPLOYEE`, `ROLE_ADMIN`
- **Path Variable:** `id` - ID de la cuenta
- **Query Parameter:** `amount` - Monto a descontar
- **Respuesta:** `BalanceResponse` con el saldo actualizado (HTTP 200)
- **Errores:** HTTP 404 si no se encuentra la cuenta, HTTP 400 si la cuenta está anulada o no hay saldo suficiente

#### GET /api/accounts/{id}/active
**Descripción:** Verifica si una cuenta está activa (para otros microservicios).
- **Roles permitidos:** `ROLE_EMPLOYEE`, `ROLE_ADMIN`
- **Path Variable:** `id` - ID de la cuenta
- **Respuesta:** `Boolean` (true si está activa, false si está anulada) (HTTP 200)
- **Errores:** HTTP 404 si no se encuentra la cuenta

### Usuarios (Users)

#### POST /api/accounts/users
**Descripción:** Crea un nuevo usuario del sistema. El password debe venir ya hasheado desde auth-service. Este endpoint es público (no requiere autenticación) ya que es parte del proceso de registro de nuevos usuarios. Normalmente es llamado internamente por auth-service durante el registro.
- **Roles permitidos:** Público (sin autenticación requerida)
- **Body:** `CreateUserRequest` (nombre, apellido, email, teléfono, password - hasheado)
- **Respuesta:** `UserResponse` con el usuario creado, incluyendo roles asignados (HTTP 201)
- **Nota:** El password debe estar hasheado con BCrypt. El hasheo se realiza en auth-service antes de llamar a este endpoint. Al crear un usuario, se le asigna automáticamente el rol `ROLE_USER`.
- **Errores:** HTTP 409 si el email ya existe (el email debe ser único)

#### GET /api/accounts/users/all
**Descripción:** Obtiene todos los usuarios del sistema.
- **Roles permitidos:** `ROLE_ADMIN`
- **Respuesta:** Lista de `UserResponse`, cada uno incluyendo roles asignados (HTTP 200)

#### GET /api/accounts/users/{id}
**Descripción:** Obtiene un usuario por su ID.
- **Roles permitidos:** `ROLE_USER`, `ROLE_ADMIN`
- **Path Variable:** `id` - ID del usuario
- **Respuesta:** `UserResponse` con los datos del usuario, incluyendo roles asignados (HTTP 200)
- **Errores:** HTTP 404 si no se encuentra el usuario

#### GET /api/accounts/users?email={email}
**Descripción:** Obtiene un usuario por su email. El email es único en el sistema.
- **Roles permitidos:** `ROLE_ADMIN`
- **Query Parameter:** `email` - Email del usuario
- **Respuesta:** `UserResponse` con los datos del usuario, incluyendo roles asignados (HTTP 200)
- **Errores:** HTTP 404 si no se encuentra el usuario con ese email

#### PUT /api/accounts/users/{id}
**Descripción:** Actualiza los datos de un usuario existente. No permite cambiar el password (el password se cambia desde auth-service).
- **Roles permitidos:** `ROLE_USER`, `ROLE_ADMIN`
- **Path Variable:** `id` - ID del usuario a actualizar
- **Body:** `UpdateUserRequest` con los datos actualizados (nombre, apellido, email, teléfono - sin password)
- **Respuesta:** `UserResponse` con el usuario actualizado, incluyendo roles asignados (HTTP 200)
- **Nota:** Este endpoint no actualiza el password. El cambio de password se realiza desde auth-service.
- **Errores:** HTTP 404 si no se encuentra el usuario, HTTP 409 si el nuevo email ya existe

#### DELETE /api/accounts/users/{id}
**Descripción:** Elimina un usuario del sistema.
- **Roles permitidos:** `ROLE_ADMIN`
- **Path Variable:** `id` - ID del usuario a eliminar
- **Respuesta:** Sin contenido (HTTP 204)
- **Errores:** HTTP 404 si no se encuentra el usuario

### Relaciones Cuenta-Usuario (Account-User)

#### POST /api/accounts/{accountId}/users/{userId}
**Descripción:** Asocia un usuario a una cuenta. Crea una relación many-to-many que permite que el usuario utilice los créditos cargados en esa cuenta.
- **Roles permitidos:** `ROLE_USER`, `ROLE_ADMIN`
- **Path Variables:** `accountId` - ID de la cuenta, `userId` - ID del usuario
- **Respuesta:** `AccountUserResponse` con información sobre la asociación creada (HTTP 201)
  - Incluye: `accountId`, `userId`, `associatedAt` (fecha de asociación), `message`
- **Errores:** HTTP 404 si no se encuentra la cuenta o el usuario, HTTP 500 si ya existe la asociación

#### DELETE /api/accounts/{accountId}/users/{userId}
**Descripción:** Desasocia un usuario de una cuenta. Elimina la relación many-to-many, impidiendo que el usuario utilice los créditos de esa cuenta.
- **Roles permitidos:** `ROLE_USER`, `ROLE_ADMIN`
- **Path Variables:** `accountId` - ID de la cuenta, `userId` - ID del usuario
- **Respuesta:** `AccountUserResponse` con información sobre la desasociación realizada (HTTP 200)
  - Incluye: `accountId`, `userId`, `associatedAt` (fecha original de asociación), `message`
- **Errores:** HTTP 404 si no se encuentra la cuenta o el usuario, HTTP 500 si no existe la asociación

#### GET /api/accounts/{accountId}/users
**Descripción:** Obtiene todos los usuarios asociados a una cuenta. Retorna la lista de usuarios que pueden utilizar los créditos cargados en esa cuenta, junto con un mensaje informativo.
- **Roles permitidos:** `ROLE_USER`, `ROLE_ADMIN`
- **Path Variable:** `accountId` - ID de la cuenta
- **Respuesta:** `UsersByAccountResponse` con información sobre los usuarios asociados (HTTP 200)
  - Incluye: `accountId`, `users` (lista de usuarios con sus roles), `count` (cantidad de usuarios), `message` (mensaje informativo)
  - Si no hay usuarios: `users` será una lista vacía y `message` indicará "La cuenta no tiene usuarios asociados"
- **Errores:** HTTP 404 si no se encuentra la cuenta

#### GET /api/accounts/users/{userId}/accounts
**Descripción:** Obtiene todas las cuentas asociadas a un usuario. Retorna la lista de cuentas cuyos créditos puede utilizar el usuario, junto con un mensaje informativo. Un usuario puede estar asociado a múltiples cuentas.
- **Roles permitidos:** `ROLE_USER`, `ROLE_ADMIN`
- **Path Variable:** `userId` - ID del usuario
- **Respuesta:** `AccountsByUserResponse` con información sobre las cuentas asociadas (HTTP 200)
  - Incluye: `userId`, `accounts` (lista de cuentas), `count` (cantidad de cuentas), `message` (mensaje informativo)
  - Si no hay cuentas: `accounts` será una lista vacía y `message` indicará "El usuario no tiene cuentas asociadas"
- **Errores:** HTTP 404 si no se encuentra el usuario
