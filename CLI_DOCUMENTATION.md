# 🖥️ Documentación del Cliente CLI

Este documento detalla el funcionamiento, arquitectura y uso del Cliente de Línea de Comandos (CLI) para el Sistema de Gestión de Clínica Veterinaria.

## 📋 Descripción General

La CLI es una aplicación de interfaz de usuario basada en texto (TUI) construida con **Lanterna**. Permite a los administradores, veterinarios y recepcionistas interactuar con el sistema backend sin necesidad de un navegador web, ofreciendo una experiencia rápida y eficiente controlada por teclado y ratón.

## ✨ Características Principales

- **Interfaz Gráfica en Terminal**: Ventanas, botones, tablas y diálogos dentro de la terminal.
- **Autenticación Segura**: Inicio de sesión con usuario y contraseña, gestionando tokens JWT.
- **Gestión de Propietarios**: Listar, crear, editar y eliminar propietarios.
- **Gestión de Mascotas**: Registro completo de mascotas asociadas a propietarios.
- **Gestión de Citas**: Agendamiento, visualización y actualización de estados de citas.
- **Historial Médico**: Visualización detallada de registros médicos y creación de nuevos registros con soporte para signos vitales y notas.
- **Estadísticas**: Panel de control con métricas en tiempo real.

## 🛠️ Arquitectura Técnica

El proyecto CLI (`cli/`) sigue una arquitectura en capas separada del backend, diseñada para la modularidad y mantenibilidad.

### Estructura de Directorios

```
cli/src/main/java/com/example/vetclinic/cli/
├── client/          # Capa de Cliente API
│   ├── ApiClient.java       # Configuración de Retrofit
│   ├── AuthClient.java      # Interface para endpoints de Auth
│   ├── OwnerClient.java     # Interface para endpoints de Owners
│   └── ...
├── config/          # Configuración global
├── model/           # Modelos de Datos (DTOs)
│   ├── Session.java         # Estado de la sesión actual
│   ├── Owner.java
│   ├── Pet.java
│   └── ...
├── service/         # Capa de Servicio (Lógica de Negocio)
│   ├── AuthService.java     # Gestión de login/logout
│   ├── OwnerService.java    # Orquestación de operaciones de Owners
│   └── ...
├── storage/         # Persistencia Local
│   └── StorageService.java  # Guardado de tokens/sesión en disco
└── ui/              # Capa de Presentación (Lanterna)
    ├── VetClinicCLI.java    # Punto de entrada (Main)
    ├── components/          # Componentes UI reutilizables
    └── modules/             # Ventanas funcionales
        ├── LoginWindow.java
        ├── MainWindow.java
        ├── OwnersWindow.java
        └── ...
```

### Tecnologías Clave

- **Lanterna**: Librería Java para crear interfaces de usuario basadas en texto (TUI). Maneja el dibujo de ventanas, componentes y eventos de entrada.
- **Retrofit**: Cliente HTTP seguro y tipado para consumir la API REST del backend.
- **Jackson**: Serialización y deserialización de JSON.

### Flujo de Datos

1.  **Interacción**: El usuario realiza una acción en la UI (ej. clic en "Guardar").
2.  **Servicio**: La UI llama al método correspondiente en la capa `service`.
3.  **Cliente API**: El servicio invoca al `client` de Retrofit.
4.  **Red**: Retrofit realiza la petición HTTP al Backend.
5.  **Respuesta**: El Backend responde, Retrofit convierte el JSON a objetos `model`.
6.  **Actualización**: El servicio retorna los datos a la UI, que se redibuja.

## 🚀 Instalación y Ejecución

### Prerrequisitos

- Java 17 o superior instalado.
- El backend de la aplicación debe estar ejecutándose (por defecto en `http://localhost:8080`).

### Ejecución Rápida

Desde la raíz del proyecto:

```bash
chmod +x run_cli.sh
./run_cli.sh
```

### Compilación Manual

Para compilar y ejecutar manualmente usando Maven:

```bash
cd cli
mvn clean package
java -jar target/vetclinic-cli-0.0.1-SNAPSHOT-shaded.jar
```

## 📖 Guía de Uso

### Navegación

La interfaz soporta tanto ratón como teclado:

- **Tab / Shift+Tab**: Moverse entre campos y botones.
- **Enter**: Activar el botón seleccionado o editar la fila seleccionada.
- **Flechas**: Navegar dentro de listas y tablas.
- **Ratón**: Clic para seleccionar, activar botones o cambiar pestañas.

### Módulos

#### 1. Login
Al iniciar, se solicitarán credenciales.
- **Usuario**: `admin` (por defecto)
- **Contraseña**: `password123` (por defecto)

#### 2. Dashboard (Panel Principal)
Muestra un resumen del estado de la clínica y botones de acceso rápido a los módulos.

#### 3. Owners (Propietarios)
- **Tabla**: Muestra ID, Nombre, Email, Teléfono.
- **Acciones**:
    - `Create New`: Abre formulario para nuevo propietario.
    - `Edit`: Modifica el propietario seleccionado.
    - `Delete`: Elimina el propietario seleccionado.
    - `Refresh`: Recarga la lista.

#### 4. Pets (Mascotas)
Gestiona las mascotas. Al crear una mascota, se debe seleccionar un propietario existente de una lista.

#### 5. Appointments (Citas)
- Permite agendar citas seleccionando Mascota, Veterinario y Servicio.
- **Validación**: Verifica disponibilidad de horarios para evitar conflictos.
- **Estados**: Permite Confirmar, Cancelar o Completar citas.

#### 6. Medical History (Historial Médico)
- **Ver Detalles**: Selecciona un registro para ver un reporte completo "impreso" en pantalla.
- **Crear Registro**: Formulario detallado que incluye:
    - Diagnóstico y Tratamiento
    - Peso y Temperatura
    - Vacunas administradas
    - Observaciones/Notas

## ⚙️ Configuración

La URL base de la API se configura en `cli/src/main/java/com/example/vetclinic/cli/client/ApiClient.java`. Por defecto es `http://localhost:8080/api/`.

Para cambiarla sin recompilar, se puede implementar una variable de entorno (pendiente de implementación futura).

## 🔧 Solución de Problemas

- **"Connection Refused"**: Asegúrate de que el backend esté corriendo en el puerto 8080.
- **"401 Unauthorized"**: Tu sesión ha expirado o las credenciales son incorrectas. Intenta hacer Logout y volver a entrar.
- **Caracteres extraños en pantalla**: Asegúrate de que tu terminal soporte UTF-8 y colores ANSI.
