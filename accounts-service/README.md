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
