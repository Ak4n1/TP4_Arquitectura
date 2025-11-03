# Comandos Docker - Sistema de Monopatines

## 📦 Docker Compose - Gestión de Servicios

### Levantar servicios
```bash
# Levantar todos los servicios
docker-compose up -d

# Reconstruir imágenes y levantar
docker-compose up -d --build
```

### Ver logs
```bash
# Ver logs de todos los servicios
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f accounts-service
docker-compose logs -f auth-service
docker-compose logs -f api-gateway
docker-compose logs -f accounts-db
```

### Detener servicios
```bash
# Detener todos los servicios
docker-compose down

# Detener y eliminar volúmenes (BD)
docker-compose down -v

# Detener y eliminar imágenes creadas por este proyecto
docker-compose down --rmi local
```

### Ver estado
```bash
# Ver estado de servicios
docker-compose ps
```

---

## 🧹 Limpieza de Docker

### Limpiar solo este proyecto
```bash
# Desde la raíz del proyecto
docker-compose down -v
docker-compose down --rmi local
```

### Limpiar recursos huérfanos (sin afectar otros proyectos)
```bash
# Contenedores detenidos
docker container prune

# Volúmenes sin usar
docker volume prune

# Imágenes sin etiquetas (dangling)
docker image prune

# Todo junto (conservador - no elimina imágenes con contenedores)
docker system prune
```

### Limpiar TODO (⚠️ CUIDADO: elimina todo lo no usado)
```bash
# Elimina todo lo no usado de TODOS los proyectos
docker system prune -a --volumes
```

---

## 🔍 Diagnóstico de Espacio en Docker

### Ver espacio usado
```bash
# Ver resumen de espacio usado por Docker
docker system df

# Ver detalle completo (imágenes, volúmenes, contenedores)
docker system df -v

# Ver qué está usando más espacio
docker system df -v | grep -E "IMAGE|VOLUME|CONTAINER"
```

### Ver imágenes
```bash
# Ver todas las imágenes
docker images

# Ver todas las imágenes (incluyendo ocultas)
docker images -a

# Ver tamaño de imágenes
docker images --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}"

# Ver imágenes sin etiquetas (dangling)
docker images -f "dangling=true"

# Buscar imágenes por patrón
docker images | grep "practica"
```

### Ver volúmenes
```bash
# Ver todos los volúmenes
docker volume ls

# Ver información detallada de un volumen
docker volume inspect <nombre-volumen>
```

### Ver contenedores
```bash
# Ver todos los contenedores (activos y detenidos)
docker ps -a

# Ver contenedores detenidos
docker ps -a -f "status=exited"

# Ver tamaño de contenedores
docker ps -a --format "table {{.Names}}\t{{.Size}}\t{{.Status}}"

# Ver tamaño de un contenedor específico
docker ps -s
```

### Ver redes
```bash
# Ver todas las redes
docker network ls
```

---

## 🗑️ Eliminación Específica de Recursos

### Eliminar imágenes específicas
```bash
# Eliminar imagen por ID
docker rmi <image-id>

# Eliminar múltiples imágenes
docker rmi <image-id> <image-id2>

# Forzar eliminación (aunque tengan contenedores asociados)
docker rmi -f <image-id>

# Eliminar imágenes por patrón
docker images | grep "<patron>" | awk '{print $3}' | xargs docker rmi -f
```

### Eliminar imágenes de proyecto anterior
```bash
# Buscar imágenes del proyecto anterior
docker images | grep "practica"

# Eliminar imágenes específicas
docker rmi practica_microservicios-api-gateway
docker rmi practica_microservicios-auth-service
docker rmi practica_microservicios-catalog-service
```

### Eliminar volúmenes específicos
```bash
# Eliminar volumen por nombre
docker volume rm <nombre-volumen>

# Eliminar volumen de proyecto anterior
docker volume rm practica_microservicios_mariadb_data

# Forzar eliminación si está en uso
docker volume rm -f practica_microservicios_mariadb_data
```

### Limpiar build cache
```bash
# Limpiar build cache manualmente
docker builder prune -a --force
```

---

## 📊 Ver Recursos de Docker

### Listar recursos
```bash
# Ver imágenes
docker images

# Ver volúmenes
docker volume ls

# Ver contenedores
docker ps -a

# Ver redes
docker network ls
```

### Ver logs del sistema
```bash
# Ver eventos de Docker (pueden ocupar espacio)
docker system events
```
