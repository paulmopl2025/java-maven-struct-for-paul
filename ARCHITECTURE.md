# 🏗️ Arquitectura del Sistema

Este documento describe la arquitectura del Sistema de Gestión para Clínica Veterinaria, basada en **Clean Architecture** y principios de **Domain-Driven Design (DDD)**.

## 📐 Principios Arquitectónicos

### Clean Architecture

El proyecto sigue los principios de Clean Architecture de Robert C. Martin, organizando el código en capas concéntricas donde:

- **Las dependencias apuntan hacia adentro**: Las capas externas dependen de las internas, nunca al revés
- **El dominio es independiente**: La lógica de negocio no depende de frameworks o tecnologías externas
- **Separación de responsabilidades**: Cada capa tiene una responsabilidad clara y bien definida

### Domain-Driven Design (DDD)

- **Entidades de Dominio**: Representan conceptos del negocio con identidad única
- **Value Objects**: Objetos inmutables que representan conceptos del dominio
- **Repositorios**: Abstracciones para acceso a datos
- **Servicios de Dominio**: Lógica de negocio que no pertenece a una entidad específica

## 🏛️ Estructura de Capas

```
┌─────────────────────────────────────────────────────────┐
│                  Presentation Layer                     │
│  (Controllers, DTOs de Request/Response, Exception)     │
└──────────────────────┬──────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────┐
│                 Application Layer                       │
│  (Use Cases, Services, DTOs, Mappers)                   │
└──────────────────────┬──────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────┐
│                   Domain Layer                          │
│  (Entities, Value Objects, Repository Interfaces)       │
└──────────────────────┬──────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────┐
│              Infrastructure Layer                       │
│  (JPA Repositories, Mapper Implementations, Config)     │
└─────────────────────────────────────────────────────────┘
```

## 📦 Capas Detalladas

### 1. Domain Layer (Capa de Dominio)

**Ubicación**: `com.example.vetclinic.domain`

**Responsabilidades**:
- Define las entidades de negocio y sus reglas
- Contiene interfaces de repositorios (no implementaciones)
- Define excepciones de dominio
- **NO depende de ninguna otra capa**

**Componentes principales**:

```
domain/
├── model/              # Entidades de dominio
│   ├── User.java
│   ├── Owner.java
│   ├── Pet.java
│   ├── Appointment.java
│   ├── MedicalRecord.java
│   ├── Vet.java
│   ├── VeterinaryService.java
│   └── ...
├── exception/          # Excepciones de dominio
│   └── BusinessException.java
└── (Repository Interfaces - definidas aquí conceptualmente)
```

**Características**:
- Entidades con anotaciones JPA mínimas (solo para persistencia)
- Lógica de negocio encapsulada en las entidades
- Value Objects para conceptos complejos (ServiceType, AppointmentStatus)
- Interfaces de repositorios definen contratos sin implementación

### 2. Application Layer (Capa de Aplicación)

**Ubicación**: `com.example.vetclinic.application`

**Responsabilidades**:
- Implementa casos de uso (use cases)
- Orquesta la lógica de negocio entre múltiples entidades
- Define DTOs para transferencia de datos
- Contiene mappers para conversión entre entidades y DTOs
- **Depende solo de Domain Layer**

**Componentes principales**:

```
application/
├── dto/                # Data Transfer Objects
│   ├── owner/
│   ├── pet/
│   ├── appointment/
│   └── ...
├── mapper/             # Interfaces de MapStruct
│   ├── OwnerMapper.java
│   ├── PetMapper.java
│   └── ...
└── service/            # Servicios de aplicación
    ├── OwnerService.java
    ├── PetService.java
    ├── AppointmentService.java
    └── ...
```

**Características**:
- Servicios transaccionales (usando `@Transactional`)
- Validación de entrada usando Bean Validation
- Mapeo entre entidades y DTOs con MapStruct
- Manejo de excepciones de dominio

**Ejemplo de flujo**:

```java
@Service
public class AppointmentService {
    
    // Depende de repositorios (interfaces del dominio)
    private final AppointmentRepository appointmentRepository;
    
    // Caso de uso: Agendar cita
    public AppointmentDTO createAppointment(CreateAppointmentDTO dto) {
        // 1. Validar datos
        // 2. Convertir DTO a entidad
        // 3. Aplicar reglas de negocio
        // 4. Persistir
        // 5. Convertir entidad a DTO
        // 6. Retornar
    }
}
```

### 3. Infrastructure Layer (Capa de Infraestructura)

**Ubicación**: `com.example.vetclinic.infrastructure`

**Responsabilidades**:
- Implementa repositorios JPA
- Implementa mappers de MapStruct
- Configuraciones técnicas
- **Depende de Domain y Application Layers**

**Componentes principales**:

```
infrastructure/
├── persistence/        # Implementaciones JPA
│   ├── OwnerJpaRepository.java
│   ├── PetJpaRepository.java
│   └── ...
└── mapper/            # Implementaciones de mappers
    ├── OwnerMapperImpl.java (generado)
    └── ...
```

**Características**:
- Extiende `JpaRepository` de Spring Data JPA
- Implementaciones automáticas de mappers por MapStruct
- Configuraciones de persistencia

### 4. Presentation Layer (Capa de Presentación)

**Ubicación**: `com.example.vetclinic.presentation`

**Responsabilidades**:
- Expone endpoints REST
- Maneja HTTP requests/responses
- Valida entrada
- Maneja excepciones globalmente
- **Depende de Application Layer**

**Componentes principales**:

```
presentation/
├── controller/         # Controladores REST
│   ├── OwnerController.java
│   ├── PetController.java
│   ├── AppointmentController.java
│   └── ...
└── exception/          # Manejo global de excepciones
    └── GlobalExceptionHandler.java
```

**Características**:
- Anotaciones de Spring MVC (`@RestController`, `@RequestMapping`)
- Validación con `@Valid`
- Autorización con `@PreAuthorize`
- Documentación con Swagger/OpenAPI
- Manejo centralizado de excepciones

**Ejemplo**:

```java
@RestController
@RequestMapping("/api/owners")
public class OwnerController {
    
    private final OwnerService ownerService;
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VET', 'RECEPCIONISTA')")
    public ResponseEntity<OwnerDTO> getOwnerById(@PathVariable Long id) {
        return ResponseEntity.ok(ownerService.getOwnerById(id));
    }
}
```

## 🔐 Security Layer (Capa de Seguridad)

**Ubicación**: `com.example.vetclinic.security`

**Responsabilidades**:
- Configuración de Spring Security
- Autenticación JWT
- Filtros de seguridad
- **Depende de Domain e Infrastructure Layers**

**Componentes**:

```
security/
├── SecurityConfig.java              # Configuración principal
├── JwtTokenProvider.java            # Generación/validación de tokens
├── JwtAuthenticationFilter.java     # Filtro de autenticación
├── JwtAuthenticationEntryPoint.java # Manejo de errores de auth
├── UserDetailsServiceImpl.java      # Carga de usuarios
└── UserSecurity.java                # Wrapper de seguridad
```

## 🔄 Flujo de Datos

### Flujo típico de una petición HTTP:

```
1. HTTP Request
   ↓
2. Security Filter (JWT validation)
   ↓
3. Controller (Presentation Layer)
   - Valida entrada (@Valid)
   - Verifica autorización (@PreAuthorize)
   ↓
4. Service (Application Layer)
   - Ejecuta lógica de negocio
   - Usa repositorios para acceso a datos
   ↓
5. Repository (Infrastructure Layer)
   - Accede a base de datos vía JPA
   ↓
6. Entity (Domain Layer)
   - Representa datos en memoria
   ↓
7. Response (inverso del flujo)
   - Entity → DTO (Mapper)
   - DTO → JSON (Spring MVC)
   - HTTP Response
```

## 📊 Modelo de Datos

### Entidades Principales

```
User (Usuario del sistema)
  ├── Role (Rol: ADMIN, VET, RECEPCIONISTA)
  └── ...

Owner (Cliente/Propietario)
  └── Pet (Mascota)
      ├── MedicalRecord (Historial Médico)
      │   └── Visit (Visita)
      └── Appointment (Cita)
          ├── Vet (Veterinario)
          └── VeterinaryService (Servicio)

Vet (Veterinario)
  └── Specialty (Especialidad)

Clinic (Clínica)
  └── Configuración y estadísticas
```

### Relaciones Principales

- **Owner** 1:N **Pet** (Un propietario tiene muchas mascotas)
- **Pet** 1:N **Appointment** (Una mascota tiene muchas citas)
- **Pet** 1:1 **MedicalRecord** (Una mascota tiene un historial médico)
- **MedicalRecord** 1:N **Visit** (Un historial tiene muchas visitas)
- **Vet** N:M **Specialty** (Un veterinario tiene muchas especialidades)
- **Appointment** N:1 **Vet** (Una cita tiene un veterinario)
- **Appointment** N:1 **VeterinaryService** (Una cita tiene un servicio)

## 🎯 Principios Aplicados

### SOLID

- **S**ingle Responsibility: Cada clase tiene una única responsabilidad
- **O**pen/Closed: Extensible sin modificar código existente
- **L**iskov Substitution: Interfaces bien definidas
- **I**nterface Segregation: Interfaces específicas y pequeñas
- **D**ependency Inversion: Dependencias hacia abstracciones

### DRY (Don't Repeat Yourself)

- Mappers centralizados con MapStruct
- Servicios reutilizables
- Excepciones globales manejadas centralmente

### KISS (Keep It Simple, Stupid)

- Código claro y directo
- Sin sobre-ingeniería
- Soluciones simples cuando son suficientes

## 🔧 Tecnologías y Frameworks

### Persistencia
- **Spring Data JPA**: Abstracción de acceso a datos
- **Hibernate**: ORM
- **PostgreSQL**: Base de datos principal
- **H2**: Base de datos para testing

### Mapeo
- **MapStruct**: Generación de código para mapeo DTO ↔ Entity

### Seguridad
- **Spring Security**: Framework de seguridad
- **JWT (jjwt)**: Tokens de autenticación
- **BCrypt**: Encriptación de contraseñas

### Documentación
- **SpringDoc OpenAPI**: Documentación automática de API

## 📝 Convenciones de Código

### Nomenclatura

- **Entidades**: PascalCase singular (`Owner`, `Pet`)
- **DTOs**: PascalCase con sufijo DTO (`OwnerDTO`, `CreateOwnerDTO`)
- **Repositorios**: PascalCase con sufijo Repository (`OwnerRepository`)
- **Servicios**: PascalCase con sufijo Service (`OwnerService`)
- **Controladores**: PascalCase con sufijo Controller (`OwnerController`)

### Estructura de Paquetes

Cada módulo sigue la misma estructura:

```
module/
├── domain/model/Entity.java
├── application/
│   ├── dto/EntityDTO.java
│   └── service/EntityService.java
├── infrastructure/
│   └── persistence/EntityJpaRepository.java
└── presentation/
    └── controller/EntityController.java
```

## 🖥️ Arquitectura del Cliente CLI

El cliente de línea de comandos (`cli/`) sigue una arquitectura en capas simplificada, diseñada para separar la interfaz de usuario de la lógica de negocio y la comunicación con la API.

### Estructura de Capas CLI

1.  **UI Layer (`ui/`)**:
    - Responsable de la interacción con el usuario usando **Lanterna**.
    - Organizada en `modules` (ventanas funcionales) y `components` (elementos reutilizables).
    - **No contiene lógica de negocio compleja**, solo lógica de presentación.

2.  **Service Layer (`service/`)**:
    - Actúa como intermediario entre la UI y el Cliente API.
    - Maneja la lógica de negocio del lado del cliente.
    - Gestiona el estado de la sesión (`AuthService`).

3.  **Client Layer (`client/`)**:
    - Define las interfaces de comunicación con el Backend usando **Retrofit**.
    - Mapea los endpoints REST a métodos Java.

4.  **Model Layer (`model/`)**:
    - Contiene los DTOs (Data Transfer Objects) que reflejan la estructura de datos de la API.

### Flujo de Datos CLI

```
1. Usuario interactúa con UI (Ventana)
   ↓
2. UI llama a Service
   ↓
3. Service llama a Client (Retrofit)
   ↓
4. Client hace HTTP Request al Backend
   ↓
5. Backend responde
   ↓
6. Client retorna Call/Response
   ↓
7. Service procesa respuesta y retorna Modelo
   ↓
8. UI actualiza la vista
```

## 🚀 Ventajas de esta Arquitectura

1. **Testabilidad**: Fácil de testear cada capa independientemente
2. **Mantenibilidad**: Código organizado y fácil de entender
3. **Escalabilidad**: Fácil agregar nuevas funcionalidades
4. **Flexibilidad**: Cambiar implementaciones sin afectar otras capas
5. **Separación de responsabilidades**: Cada capa tiene un propósito claro

## 📚 Referencias

- [Clean Architecture - Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Domain-Driven Design - Eric Evans](https://www.domainlanguage.com/ddd/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [MapStruct Documentation](https://mapstruct.org/)

---

**Última actualización**: 2025-01-27

