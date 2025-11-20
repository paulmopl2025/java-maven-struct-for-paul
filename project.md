# PROJECT.MD - Sistema de Gestión para Clínica Veterinaria

## 📋 Información del Proyecto

**Nombre:** Sistema de Gestión para Clínica Veterinaria
**Tecnología:** Spring Boot 3.x
**Base de Datos:** PostgreSQL
**Arquitectura:** Clean Architecture + DDD
**Autenticación:** Spring Security + JWT

---

## 🎯 Objetivos del Proyecto

- [ ] Implementar sistema integral para gestión de clientes, mascotas y servicios veterinarios
- [ ] Aplicar los cuatro pilares de la POO: Abstracción, Encapsulamiento, Herencia y Polimorfismo
- [ ] Crear arquitectura flexible que facilite la incorporación de nuevos módulos
- [ ] Desarrollar interfaz de consola intuitiva y fácil de usar para el usuario final
- [ ] Implementar sistema de autenticación y autorización robusto

---

## 🏗️ Arquitectura y Estructura del Proyecto

### Capas de la Aplicación
- [ ] **Domain Layer** - Entidades, Value Objects, Repository Interfaces
- [ ] **Application Layer** - Use Cases, DTOs, Service Interfaces
- [ ] **Infrastructure Layer** - Implementaciones de Repositorios, Configuraciones
- [ ] **Presentation Layer** - Controllers REST, Request/Response Models
- [ ] **Security Layer** - Configuración de seguridad, JWT, filtros

---

## 📦 Configuración Inicial

### Dependencias Maven
- [ ] Spring Boot Starter Web
- [ ] Spring Boot Starter Data JPA
- [ ] Spring Boot Starter Security
- [ ] Spring Boot Starter Validation
- [ ] PostgreSQL Driver
- [ ] Lombok
- [ ] MapStruct
- [ ] JWT (jjwt-api, jjwt-impl, jjwt-jackson)
- [ ] SpringDoc OpenAPI (Swagger)
- [ ] Spring Boot Starter Test
- [ ] Spring Security Test

### Configuración de Base de Datos
- [ ] Configurar application.properties/yml
- [ ] Configurar pool de conexiones (HikariCP)
- [ ] Configurar JPA/Hibernate properties
- [ ] Script de inicialización de BD

---

## 🔐 Módulo de Seguridad y Autenticación

### 1. Entidades de Seguridad
- [ ] Entity: Usuario (User)
- [ ] Entity: Rol (Role)
- [ ] Entity: Permiso (Permission)
- [ ] Relación Usuario-Rol (Many-to-Many)
- [ ] Relación Rol-Permiso (Many-to-Many)

### 2. Configuración de Seguridad
- [x] SecurityConfig - Configuración de Spring Security
- [x] JwtAuthenticationFilter - Filtro para validar tokens
- [x] JwtTokenProvider - Generación y validación de tokens
- [x] PasswordEncoder - BCrypt para encriptación
- [ ] CorsConfiguration - Configuración de CORS
- [x] AuthenticationEntryPoint - Manejo de errores de autenticación
- [x] OpenApiConfig - Configuración de Swagger UI con JWT <!-- id: 21 -->

### 3. Endpoints de Autenticación
- [x] POST /api/auth/register - Registro de usuarios
- [x] POST /api/auth/login - Inicio de sesión
- [ ] POST /api/auth/refresh - Renovar token
- [ ] POST /api/auth/logout - Cerrar sesión
- [ ] GET /api/auth/profile - Obtener perfil del usuario

### 4. Roles y Permisos
- [ ] Definir roles: ADMIN, VETERINARIO, RECEPCIONISTA
- [ ] Definir permisos por módulo
- [ ] Implementar @PreAuthorize en endpoints
- [ ] Sistema de auditoría (createdBy, modifiedBy)

---

## 👥 Módulo de Usuarios y Roles

### 1. Domain Layer
- [x] Entity: User
- [ ] Value Object: Email
- [ ] Value Object: PhoneNumber
- [ ] Repository Interface: UserRepository

### 2. Application Layer
- [ ] UseCase: RegisterUser
- [ ] UseCase: UpdateUser
- [ ] UseCase: DeactivateUser
- [ ] UseCase: AssignRole
- [x] DTO: UserDTO, CreateUserDTO, UpdateUserDTO

### 3. Infrastructure Layer
- [x] JPA Repository: UserJpaRepository
- [ ] Repository Implementation: UserRepositoryImpl
- [x] Mapper: UserMapper

### 4. Presentation Layer
- [x] Controller: UserController
- [ ] Endpoints:
  - [x] GET /api/users - Listar usuarios
  - [x] GET /api/users/{id} - Obtener usuario por ID
  - [ ] POST /api/users - Crear usuario
  - [x] PUT /api/users/{id} - Actualizar usuario
  - [x] DELETE /api/users/{id} - Desactivar usuario

---

## 🐕 Módulo de Clientes

### 1. Domain Layer
- [x] Entity: Cliente
- [ ] Value Object: Documento (tipo y número)
- [ ] Value Object: Dirección
- [x] Repository Interface: ClienteRepository

### 2. Application Layer
- [x] UseCase: RegisterCliente
- [x] UseCase: UpdateCliente
- [x] UseCase: SearchCliente
- [ ] UseCase: GetClienteWithMascotas
- [x] DTO: ClienteDTO, CreateClienteDTO, UpdateClienteDTO

### 3. Infrastructure Layer
- [x] JPA Repository: ClienteJpaRepository
- [ ] Repository Implementation: ClienteRepositoryImpl
- [x] Mapper: ClienteMapper

### 4. Presentation Layer
- [x] Controller: ClienteController
- [ ] Endpoints:
  - [x] GET /api/clientes - Listar clientes (paginado)
  - [x] GET /api/clientes/{id} - Obtener cliente por ID
  - [ ] GET /api/clientes/search - Buscar clientes
  - [x] POST /api/clientes - Crear cliente
  - [x] PUT /api/clientes/{id} - Actualizar cliente
  - [x] DELETE /api/clientes/{id} - Desactivar cliente

---

## 🐾 Módulo de Mascotas

### 1. Domain Layer
- [x] Entity: Mascota
- [ ] Value Object: Especie (enum)
- [ ] Value Object: Raza
- [ ] Value Object: Peso
- [x] Repository Interface: MascotaRepository

### 2. Application Layer
- [x] UseCase: RegisterMascota
- [x] UseCase: UpdateMascota
- [ ] UseCase: AssociateMascotaToCliente
- [x] UseCase: GetMascotasByCliente
- [x] DTO: MascotaDTO, CreateMascotaDTO, UpdateMascotaDTO

### 3. Infrastructure Layer
- [x] JPA Repository: MascotaJpaRepository
- [ ] Repository Implementation: MascotaRepositoryImpl
- [x] Mapper: MascotaMapper

### 4. Presentation Layer
- [x] Controller: MascotaController
- [ ] Endpoints:
  - [x] GET /api/mascotas - Listar mascotas
  - [x] GET /api/mascotas/{id} - Obtener mascota por ID
  - [x] GET /api/clientes/{clienteId}/mascotas - Mascotas por cliente
  - [x] POST /api/mascotas - Crear mascota
  - [x] PUT /api/mascotas/{id} - Actualizar mascota
  - [x] DELETE /api/mascotas/{id} - Desactivar mascota

---

## 💉 Módulo de Servicios Veterinarios

### 1. Domain Layer
- [ ] Entity: Servicio
- [ ] Value Object: TipoServicio (vacunación, control, urgencia, cirugía)
- [ ] Value Object: Costo
- [ ] Value Object: Duración
- [ ] Repository Interface: ServicioRepository

### 2. Application Layer
- [ ] UseCase: CreateServicio
- [ ] UseCase: UpdateServicio
- [ ] UseCase: SearchServicioByType
- [ ] UseCase: CalculateCosto
- [ ] DTO: ServicioDTO, CreateServicioDTO, UpdateServicioDTO

### 3. Infrastructure Layer
- [ ] JPA Repository: ServicioJpaRepository
- [ ] Repository Implementation: ServicioRepositoryImpl
- [ ] Mapper: ServicioMapper

### 4. Presentation Layer
- [ ] Controller: ServicioController
- [ ] Endpoints:
  - [ ] GET /api/servicios - Listar servicios
  - [ ] GET /api/servicios/{id} - Obtener servicio por ID
  - [ ] GET /api/servicios/tipo/{tipo} - Buscar por tipo
  - [ ] POST /api/servicios - Crear servicio
  - [ ] PUT /api/servicios/{id} - Actualizar servicio
  - [ ] DELETE /api/servicios/{id} - Desactivar servicio

---

## 📅 Módulo de Citas

### 1. Domain Layer
- [ ] Entity: Cita
- [ ] Value Object: EstadoCita (pendiente, confirmada, cancelada, completada)
- [ ] Value Object: FechaHora
- [ ] Repository Interface: CitaRepository

### 2. Application Layer
- [ ] UseCase: ScheduleCita
- [ ] UseCase: UpdateCita
- [ ] UseCase: CancelCita
- [ ] UseCase: ConfirmCita
- [ ] UseCase: CheckAvailability
- [ ] UseCase: GetCitasByVeterinario
- [ ] UseCase: GetCitasByCliente
- [ ] DTO: CitaDTO, CreateCitaDTO, UpdateCitaDTO

### 3. Infrastructure Layer
- [ ] JPA Repository: CitaJpaRepository
- [ ] Repository Implementation: CitaRepositoryImpl
- [ ] Mapper: CitaMapper
- [ ] Service: DisponibilidadService

### 4. Presentation Layer
- [ ] Controller: CitaController
- [ ] Endpoints:
  - [ ] GET /api/citas - Listar citas (paginado, filtrado)
  - [ ] GET /api/citas/{id} - Obtener cita por ID
  - [ ] GET /api/citas/disponibilidad - Verificar disponibilidad
  - [ ] POST /api/citas - Agendar cita
  - [ ] PUT /api/citas/{id} - Actualizar cita
  - [ ] PATCH /api/citas/{id}/confirmar - Confirmar cita
  - [ ] PATCH /api/citas/{id}/cancelar - Cancelar cita

---

## 📋 Módulo de Historial Médico

### 1. Domain Layer
- [ ] Entity: HistorialMedico
- [ ] Entity: RegistroMedico (peso, vacunas, tratamientos)
- [ ] Value Object: Diagnostico
- [ ] Value Object: Tratamiento
- [ ] Repository Interface: HistorialMedicoRepository

### 2. Application Layer
- [ ] UseCase: CreateHistorial
- [ ] UseCase: AddRegistroMedico
- [ ] UseCase: GetHistorialByMascota
- [ ] UseCase: GetVacunasPendientes
- [ ] DTO: HistorialMedicoDTO, RegistroMedicoDTO

### 3. Infrastructure Layer
- [ ] JPA Repository: HistorialMedicoJpaRepository
- [ ] Repository Implementation: HistorialMedicoRepositoryImpl
- [ ] Mapper: HistorialMedicoMapper

### 4. Presentation Layer
- [ ] Controller: HistorialMedicoController
- [ ] Endpoints:
  - [ ] GET /api/historiales/mascota/{mascotaId} - Historial completo
  - [ ] POST /api/historiales - Crear historial
  - [ ] POST /api/historiales/{id}/registros - Agregar registro
  - [ ] GET /api/historiales/{id}/registros - Listar registros

---

## 🏥 Módulo de Clínica (Gestión Operativa)

### 1. Domain Layer
- [ ] Entity: Clinica
- [ ] Value Object: HorarioAtencion
- [ ] Value Object: CapacidadAtencion
- [ ] Repository Interface: ClinicaRepository

### 2. Application Layer
- [ ] UseCase: ConfigureClinica
- [ ] UseCase: UpdateHorarios
- [ ] UseCase: GetEstadisticas
- [ ] DTO: ClinicaDTO, EstadisticasDTO

### 3. Infrastructure Layer
- [ ] JPA Repository: ClinicaJpaRepository
- [ ] Repository Implementation: ClinicaRepositoryImpl

### 4. Presentation Layer
- [ ] Controller: ClinicaController
- [ ] Endpoints:
  - [ ] GET /api/clinica - Obtener configuración
  - [ ] PUT /api/clinica - Actualizar configuración
  - [ ] GET /api/clinica/estadisticas - Estadísticas generales

---

## 🔍 Funcionalidades Transversales

### 1. Manejo de Excepciones
- [ ] GlobalExceptionHandler
- [ ] Custom Exceptions: EntityNotFoundException, BusinessException, etc.
- [ ] ErrorResponse DTO
- [ ] Validación de errores de validación (@Valid)

### 2. Validaciones
- [ ] Bean Validation (@NotNull, @NotBlank, @Email, etc.)
- [ ] Custom Validators
- [ ] Validation Groups

### 3. Auditoría
- [ ] @CreatedDate, @LastModifiedDate
- [ ] @CreatedBy, @LastModifiedBy
- [ ] AuditorAware implementation
- [ ] JPA Auditing configuration

### 4. Paginación y Ordenamiento
- [ ] Implementar Pageable en repositorios
- [ ] PagedResponse DTO
- [ ] Sorting y filtering

### 5. Logging
- [ ] Configurar SLF4J + Logback
- [ ] Log levels por paquete
- [ ] Request/Response logging interceptor
- [ ] Logging de errores y excepciones

---

## 📊 Documentación API

### Swagger/OpenAPI
- [ ] Configurar SpringDoc OpenAPI
- [ ] Documentar todos los endpoints
- [ ] Ejemplos de request/response
- [ ] Documentar esquemas de autenticación
- [ ] Tags y descriptions

---

## 🧪 Testing

### Unit Tests
- [ ] Tests de Domain Entities
- [ ] Tests de Use Cases
- [ ] Tests de Validators
- [ ] Tests de Mappers
- [ ] Coverage mínimo: 80%

### Integration Tests
- [ ] Tests de Repositories
- [ ] Tests de Controllers (MockMvc)
- [ ] Tests de Security
- [ ] Testcontainers para PostgreSQL

### E2E Tests
- [ ] Flujo completo de autenticación
- [ ] Flujo de gestión de citas
- [ ] Flujo de historial médico

---

## 🚀 Despliegue y DevOps

### Configuración
- [ ] Profiles (dev, test, prod)
- [ ] Variables de entorno
- [ ] Docker Compose para desarrollo
- [ ] Dockerfile para producción

### CI/CD
- [ ] Pipeline de integración continua
- [ ] Tests automatizados
- [ ] Code quality (SonarQube)
- [ ] Build y deployment

---

## 📝 Documentación del Proyecto

- [ ] README.md con instrucciones de setup
- [ ] ARCHITECTURE.md con decisiones arquitectónicas
- [ ] API_DOCUMENTATION.md
- [ ] Diagramas UML (clases, secuencia, casos de uso)
- [ ] Diagramas de base de datos (ERD)
- [ ] Guía de contribución

---

## 🎨 Estándares de Código

### Convenciones
- [ ] Google Java Style Guide
- [ ] Checkstyle configuration
- [ ] SonarLint configuration
- [ ] Commit message conventions

### Code Review Checklist
- [ ] Nomenclatura clara y descriptiva
- [ ] Principios SOLID aplicados
- [ ] DRY (Don't Repeat Yourself)
- [ ] KISS (Keep It Simple, Stupid)
- [ ] Tests escritos y pasando
- [ ] Documentación actualizada

---

## 📈 Fases del Proyecto

### Fase 1: Fundación (Semanas 1-2)
- [x] Setup inicial del proyecto
- [x] Configuración de base de datos
- [x] Módulo de seguridad y autenticación
- [x] Configuraciones transversales

### Fase 2: Módulos Core (Semanas 3-5)
- [x] Módulo de Usuarios
- [x] Módulo de Clientes
- [x] Módulo de Mascotas
- [ ] Módulo de Servicios

### Fase 3: Módulos de Negocio (Semanas 6-8)
- [ ] Módulo de Citas
- [ ] Módulo de Historial Médico
- [ ] Módulo de Clínica
- [ ] Integración entre módulos

### Fase 4: Refinamiento (Semanas 9-10)
- [ ] Testing completo
- [ ] Documentación
- [ ] Optimizaciones de performance
- [ ] Bug fixing

### Fase 5: Despliegue (Semana 11)
- [ ] Configuración de producción
- [ ] Despliegue
- [ ] Monitoreo
- [ ] Capacitación

---

## 🔄 Estado Actual del Proyecto

**Fase Actual:** Fase 2: Módulos Core
**Última Actualización:** 2025-11-19
**Progreso General:** 40%

---

## 📞 Contacto y Soporte

**Equipo de Desarrollo:** [Nombres]
**Tech Lead:** [Nombre]
**Repositorio:** [URL]

---

## 🎯 Prompt para Agentes IA

```
Estás trabajando en el "Sistema de Gestión para Clínica Veterinaria" usando Spring Boot 3.x con Clean Architecture y DDD.

CONTEXTO:
- Proyecto: Sistema integral para gestión de clínica veterinaria
- Stack: Spring Boot, PostgreSQL, Spring Security + JWT
- Arquitectura: Clean Architecture (Domain, Application, Infrastructure, Presentation)
- Estado actual: [Consultar checkboxes en PROJECT.md]

DIRECTRICES:
1. Seguir estrictamente Clean Architecture y principios DDD
2. Aplicar principios SOLID en cada implementación
3. Implementar seguridad en todos los endpoints según roles definidos
4. Escribir código limpio, documentado y con tests
5. Usar DTOs para comunicación entre capas
6. Implementar validaciones robustas
7. Manejar excepciones apropiadamente
8. Seguir Google Java Style Guide

ANTES DE CODIFICAR:
1. Verifica el estado actual en PROJECT.md
2. Identifica en qué fase/módulo estamos
3. Revisa dependencias con otros módulos
4. Asegúrate de que los prerrequisitos estén completados

DESPUÉS DE CODIFICAR:
1. Actualiza los checkboxes correspondientes en PROJECT.md
2. Escribe/actualiza tests unitarios
3. Actualiza documentación si es necesario
4. Verifica que el código compile y los tests pasen

MÓDULO ACTUAL: [Especificar módulo]
TAREA ACTUAL: [Especificar tarea del checkbox]
```

---

**Notas:** Este documento es vivo y debe actualizarse conforme avanza el proyecto. Cada checkbox marcado representa un entregable completado y revisado.