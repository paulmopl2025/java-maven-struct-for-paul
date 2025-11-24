# 🏥 Sistema de Gestión para Clínica Veterinaria

Sistema integral de gestión para clínicas veterinarias desarrollado con Spring Boot 3.x, implementando Clean Architecture y Domain-Driven Design (DDD).

## 📋 Tabla de Contenidos

- [Características](#-características)
- [Tecnologías](#-tecnologías)
- [Requisitos](#-requisitos)
- [Instalación](#-instalación)
- [Configuración](#-configuración)
- [Uso](#-uso)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [API Documentation](#-api-documentation)
- [Seguridad](#-seguridad)
- [Testing](#-testing)
- [Despliegue](#-despliegue)
- [Contribución](#-contribución)

## ✨ Características

- **Gestión de Usuarios**: Sistema completo de usuarios con roles y permisos (ADMIN, VET, RECEPCIONISTA)
- **Gestión de Clientes**: Registro y administración de propietarios de mascotas
- **Gestión de Mascotas**: Registro completo de mascotas con historial médico
- **Gestión de Veterinarios**: Administración de veterinarios y especialidades
- **Gestión de Citas**: Sistema de agendamiento y seguimiento de citas
- **Historial Médico**: Registro completo de historiales médicos y visitas
- **Servicios Veterinarios**: Catálogo de servicios disponibles
- **Autenticación JWT**: Sistema seguro de autenticación basado en tokens
- **API RESTful**: API completa documentada con Swagger/OpenAPI
- **Interfaz de Línea de Comandos (CLI)**: Cliente TUI completo para gestión administrativa.
- **Auditoría**: Sistema de auditoría automática (createdBy, modifiedBy, timestamps)

## 🛠️ Tecnologías

- **Java 17**
- **Spring Boot 3.1.6**
- **Spring Security** - Autenticación y autorización
- **Spring Data JPA** - Persistencia de datos
- **PostgreSQL** - Base de datos principal
- **H2 Database** - Base de datos para testing
- **JWT (jjwt)** - Tokens de autenticación
- **MapStruct** - Mapeo de objetos
- **Lombok** - Reducción de código boilerplate
- **SpringDoc OpenAPI** - Documentación de API (Swagger)
- **Maven** - Gestión de dependencias
- **Lanterna** - Librería para TUI (Terminal UI)
- **Retrofit** - Cliente HTTP para la CLI

## 📦 Requisitos

- Java 17 o superior
- Maven 3.6+
- PostgreSQL 12+ (para producción) o H2 (para desarrollo local)
- Docker y Docker Compose (opcional, para base de datos)

## 🚀 Instalación

### 1. Clonar el repositorio

```bash
git clone <repository-url>
cd java-maven-struct-for-paul
```

### 2. Configurar la base de datos

#### Opción A: Usando Docker Compose (Recomendado)

```bash
docker compose up -d
```

Esto iniciará PostgreSQL en el puerto 5432 con:
- Base de datos: `vetdb`
- Usuario: `postgres`
- Contraseña: `postgres`

#### Opción B: PostgreSQL local

Asegúrate de tener PostgreSQL instalado y crea una base de datos:

```sql
CREATE DATABASE vetdb;
```

### 3. Configurar variables de entorno (Opcional)

Puedes configurar las siguientes variables de entorno o usar los valores por defecto:

```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=vetdb
export DB_USER=postgres
export DB_PASSWORD=postgres
```

### 4. Compilar y ejecutar

```bash
# Compilar el proyecto
mvn clean install

# Ejecutar la aplicación
mvn spring-boot:run
```

O usando el script proporcionado:

```bash
chmod +x run.sh
./run.sh
```

La aplicación estará disponible en: `http://localhost:8080`

### 5. Ejecutar la CLI (Cliente de Terminal)

Para iniciar la interfaz de usuario en terminal:

```bash
chmod +x run_cli.sh
./run_cli.sh
```

> **Nota**: Asegúrate de que la aplicación backend esté ejecutándose antes de iniciar la CLI.

Para una guía completa de uso, arquitectura y solución de problemas, consulta la [Documentación del Cliente CLI](./CLI_DOCUMENTATION.md).

## ⚙️ Configuración

### Perfiles de Spring

La aplicación soporta múltiples perfiles:

- **default**: Usa PostgreSQL (configuración de producción)
- **local**: Usa H2 en memoria (para desarrollo rápido)

Para usar el perfil local:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

### Archivos de configuración

- `application.yml`: Configuración principal (PostgreSQL)
- `application-local.yml`: Configuración para desarrollo local (H2)

### Configuración de la base de datos

Edita `src/main/resources/application.yml` para ajustar la conexión:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/vetdb
    username: postgres
    password: postgres
```

## 📖 Uso

### Acceso a la documentación de la API

Una vez que la aplicación esté ejecutándose, puedes acceder a:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### Autenticación

1. **Registrar un nuevo usuario**:

```bash
POST /api/auth/register
Content-Type: application/json

{
  "username": "admin",
  "email": "admin@vetclinic.com",
  "password": "password123",
  "role": "ADMIN"
}
```

2. **Iniciar sesión**:

```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password123"
}
```

La respuesta incluirá un token JWT que debes usar en las siguientes peticiones:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer"
}
```

3. **Usar el token en peticiones**:

```bash
Authorization: Bearer <tu-token-jwt>
```

### Ejemplos de uso

Ver [API_DOCUMENTATION.md](./API_DOCUMENTATION.md) para ejemplos detallados de todos los endpoints.

## 📁 Estructura del Proyecto

El proyecto sigue los principios de **Clean Architecture** y **DDD**:

```
src/main/java/com/example/vetclinic/
├── application/          # Capa de Aplicación
│   ├── dto/             # Data Transfer Objects
│   ├── mapper/          # Mappers (MapStruct)
│   └── service/         # Servicios de aplicación
├── domain/              # Capa de Dominio
│   ├── exception/       # Excepciones de dominio
│   └── model/           # Entidades de dominio
├── infrastructure/       # Capa de Infraestructura
│   ├── mapper/          # Implementaciones de mappers
│   └── persistence/     # Repositorios JPA
├── presentation/        # Capa de Presentación
│   ├── controller/      # Controladores REST
│   └── exception/       # Manejo global de excepciones
├── security/            # Configuración de seguridad
│   ├── SecurityConfig.java
│   ├── JwtTokenProvider.java
│   └── JwtAuthenticationFilter.java
└── config/              # Configuraciones generales

#### Estructura de la CLI

El cliente de terminal (`cli/`) sigue su propia estructura modular:

```
cli/src/main/java/com/example/vetclinic/cli/
├── client/          # Clientes HTTP (Retrofit) para consumir la API
├── config/          # Configuración (ApiClient, etc.)
├── model/           # Modelos de datos (DTOs)
├── service/         # Lógica de negocio y orquestación
├── storage/         # Almacenamiento local (Sesión)
└── ui/              # Interfaz de Usuario (Lanterna)
    ├── components/  # Componentes reutilizables de UI
    └── modules/     # Ventanas y módulos principales (Owners, Pets, etc.)
```
```

Para más detalles sobre la arquitectura, consulta [ARCHITECTURE.md](./ARCHITECTURE.md).

## 🔐 Seguridad

### Roles disponibles

- **ADMIN**: Acceso completo al sistema
- **VET**: Veterinario con acceso a historiales médicos y citas
- **RECEPCIONISTA**: Personal de recepción con acceso limitado

### Endpoints públicos

Los siguientes endpoints no requieren autenticación:

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /v3/api-docs/**`
- `GET /swagger-ui/**`

Todos los demás endpoints requieren autenticación JWT.

### Configuración de seguridad

La configuración de seguridad se encuentra en `SecurityConfig.java` y utiliza:

- **JWT Tokens** para autenticación stateless
- **BCrypt** para encriptación de contraseñas
- **Method Security** con `@PreAuthorize` para autorización basada en roles

## 🧪 Testing

### Ejecutar tests

```bash
# Todos los tests
mvn test

# Tests específicos
mvn test -Dtest=AppointmentServiceTest
```

### Cobertura de tests

El proyecto incluye tests unitarios y de integración para:

- Servicios de aplicación
- Controladores REST
- Repositorios JPA

Los reportes de tests se generan en `target/surefire-reports/`.

## 🐳 Despliegue

### Docker

El proyecto incluye un `Dockerfile` para crear una imagen de la aplicación:

```bash
# Construir la imagen
docker build -t vetclinic:latest .

# Ejecutar el contenedor
docker run -p 8080:8080 \
  -e DB_HOST=host.docker.internal \
  -e DB_PORT=5432 \
  -e DB_NAME=vetdb \
  -e DB_USER=postgres \
  -e DB_PASSWORD=postgres \
  vetclinic:latest
```

### Docker Compose completo

Puedes usar Docker Compose para ejecutar tanto la aplicación como la base de datos:

```yaml
# docker-compose.yml (extendido)
services:
  postgres:
    # ... configuración existente
  
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      DB_HOST: postgres
      DB_PORT: 5432
      DB_NAME: vetdb
      DB_USER: postgres
      DB_PASSWORD: postgres
    depends_on:
      - postgres
```

## 📚 Documentación Adicional

- [ARCHITECTURE.md](./ARCHITECTURE.md) - Arquitectura detallada del sistema
- [API_DOCUMENTATION.md](./API_DOCUMENTATION.md) - Documentación completa de la API
- [CLI_DOCUMENTATION.md](./CLI_DOCUMENTATION.md) - Guía completa del cliente CLI
- [project.md](./project.md) - Plan de proyecto y estado de desarrollo

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

### Estándares de código

- Seguir Google Java Style Guide
- Escribir tests para nuevas funcionalidades
- Documentar código complejo
- Mantener cobertura de tests > 80%

## 📝 Licencia

Este proyecto es de uso educativo y demostrativo.

## 👥 Autores

- **Equipo de Desarrollo** - Desarrollo inicial

## 🙏 Agradecimientos

- Spring Boot Community
- Todos los contribuidores de las librerías utilizadas

---

**Nota**: Este es un proyecto en desarrollo activo. Para el estado actual del proyecto, consulta [project.md](./project.md).
