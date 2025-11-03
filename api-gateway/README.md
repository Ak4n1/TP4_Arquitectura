# API Gateway

## Descripción

Gateway API que actúa como punto de entrada único para todos los microservicios del sistema. Se encarga de enrutar las peticiones HTTP a los microservicios correspondientes según la ruta solicitada, validar tokens JWT para proteger los endpoints, y gestionar CORS. También puede implementar rate limiting y otras políticas de seguridad. Los clientes solo se comunican con el gateway, y este se encarga de redirigir las peticiones a los servicios internos.

## Dependencias

### spring-cloud-starter-gateway
**Para qué sirve:** Gateway de Spring Cloud. Permite enrutar requests HTTP a los diferentes microservicios, configurar rutas, filtros, y actuar como punto de entrada único para todas las peticiones.

### spring-boot-starter-actuator
**Para qué sirve:** Health checks y métricas del gateway. Expone endpoints como `/actuator/health` para verificar que el gateway está funcionando correctamente y ver el estado de las rutas.

### spring-boot-starter-test
**Para qué sirve:** Testing. Permite escribir y ejecutar tests unitarios e integración del gateway.

