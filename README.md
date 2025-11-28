# NTT Prueba Backend - Sistema de Autenticación

Sistema backend desarrollado con Spring Boot que implementa autenticación JWT, gestión de usuarios, roles y teléfonos.

## 📋 Tabla de Contenidos

- [Requisitos Previos](#requisitos-previos)
- [Tecnologías](#tecnologías)
- [Instalación](#instalación)
- [Configuración](#configuración)
- [Ejecución](#ejecución)
- [Endpoints Principales](#endpoints-principales)
- [Documentación API](#documentación-api)
- [Testing](#testing)
- [Estructura del Proyecto](#estructura-del-proyecto)

---

## 🔧 Requisitos Previos

Antes de ejecutar el proyecto, asegúrate de tener instalado:

- **Java 21** o superior
- **Maven 3.8+** (o usar el wrapper incluido)
- **Git** (opcional, para clonar el repositorio)

### Verificar Instalación

```bash
# Verificar Java
java -version

# Verificar Maven (opcional si usas el wrapper)
mvn -version
```

---

## 🚀 Tecnologías

| Tecnología | Versión | Descripción |
|------------|---------|-------------|
| Spring Boot | 3.5.7 | Framework principal |
| Java | 21 | Lenguaje de programación |
| H2 Database | Runtime | Base de datos en memoria |
| Spring Security | 3.5.7 | Seguridad y autenticación |
| JWT | 0.12.6 | Tokens de autenticación |
| MapStruct | 1.5.5 | Mapeo de DTOs |
| Lombok | Latest | Reducción de código boilerplate |
| SpringDoc OpenAPI | 2.7.0 | Documentación API |

---

## 📦 Instalación

### Opción 1: Clonar el Repositorio

```bash
git clone <repository-url>
cd prueba
```

### Opción 2: Descargar ZIP

1. Descarga el proyecto como ZIP
2. Extrae el contenido
3. Navega a la carpeta del proyecto

---

## ⚙️ Configuración

### Perfiles Disponibles

El proyecto soporta diferentes perfiles de configuración:

- **dev** (por defecto): Desarrollo local
- **prod**: Producción

### Configuración de Base de Datos

Por defecto, el proyecto usa **H2** (base de datos en memoria). La configuración se encuentra en:

```yaml
# src/main/resources/application.yml
spring:
  datasource:
    url: # Configurar según ambiente
    username: # Usuario de BD
    password: # Contraseña de BD
```

### Esquema de Base de Datos

El proyecto incluye scripts SQL para inicializar la base de datos en el directorio `database/`:

- **`schema.sql`**: Script compatible con PostgreSQL y MySQL
- **`schema-h2.sql`**: Script optimizado para H2 Database (usado por defecto)

#### Estructura de Tablas

El sistema utiliza las siguientes tablas:

| Tabla | Descripción |
|-------|-------------|
| `users` | Información de usuarios y credenciales |
| `roles` | Roles del sistema (USER, ADMIN, MODERATOR) |
| `user_roles` | Relación muchos-a-muchos entre usuarios y roles |
| `phone` | Números telefónicos asociados a usuarios |
| `refresh_tokens` | Tokens de refresco para autenticación JWT |
| `authentication_audit` | Auditoría de eventos de autenticación |

Todas las tablas heredan campos de auditoría:
- `id` (UUID): Identificador único
- `created_by`: Usuario que creó el registro
- `created_date`: Fecha de creación
- `last_modified_by`: Último usuario que modificó
- `last_modified_date`: Fecha de última modificación
- `is_deleted`: Flag para borrado lógico

#### Ejecutar Scripts de Base de Datos

**Para H2 :**
```bash
# El esquema se crea automáticamente con JPA
# O ejecutar manualmente desde H2 Console
```


### Configuración de Validación de Contraseñas

Puedes personalizar los requisitos de contraseña en `application.yml`:

```yaml
validation:
  password:
    pattern: "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$"
    message: "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial"
```
Actualmente esta con el perfil de desarrollo el cual es mas permisivo `application-dev.yml`:

```yaml
validation:
  password:
    pattern: "^.{4,}$"
    message: "La contraseña debe tener al menos 4 caracteres"
```

### Variables de Entorno (Opcional)

Puedes sobrescribir la configuración usando variables de entorno:

```bash
export SERVER_PORT=8080
export SPRING_PROFILES_ACTIVE=prod
export VALIDATION_PASSWORD_PATTERN="^.{6,}$"
```

---

## 🏃 Ejecución

### Opción 1: Usando Maven Wrapper (Recomendado)

#### Windows
```bash
.\mvnw.cmd spring-boot:run
```

#### Linux/Mac
```bash
./mvnw spring-boot:run
```

### Opción 2: Usando Maven Instalado

```bash
mvn spring-boot:run
```

### Opción 3: Ejecutar el JAR

```bash
# 1. Compilar el proyecto
mvn clean package

# 2. Ejecutar el JAR generado
java -jar target/prueba-0.0.1-SNAPSHOT.jar
```

### Opción 4: Desde tu IDE

1. Abre el proyecto en IntelliJ IDEA / Eclipse / VS Code
2. Busca la clase `PruebaApplication.java`
3. Ejecuta el método `main()`

---

## 🌐 Acceso al Sistema

Una vez iniciado, el sistema estará disponible en:

- **URL Base**: `http://localhost:8080/api/v1`
- **Swagger UI**: `http://localhost:8080/api/v1/swagger-ui.html`
- **OpenAPI Docs**: `http://localhost:8080/api/v1/v3/api-docs`
- **Health Check**: `http://localhost:8080/api/v1/actuator/health`


---

## 📡 Endpoints Principales

### Autenticación

#### Registro de Usuario
```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "correo": "juan.perez@example.com",
  "nombre": "Juan Pérez",
  "password": "SecurePass123!",
  "phones": [
    {
      "number": "987654321",
      "cityCode": "1",
      "countryCode": "+57"
    }
  ]
}
```

**Nota:** El campo `roleNames` no es necesario ya que por defecto se asigna el rol `USER`. La contraseña debe cumplir con los requisitos de validación (mínimo 8 caracteres, mayúscula, minúscula, número y carácter especial).

#### Login
```http
POST /api/v1/auth/authenticate
Content-Type: application/json

{
  "username": "juan.perez@example.com",
  "password": "SecurePass123!"
}
```

**Respuesta:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "activo": true,
  "creado": "2025-11-19T15:30:00",
  "modificado": "2025-11-19T15:30:00",
  "ultimoLogin": "2025-11-19T15:30:00",
  "id": "123e4567-e89b-12d3-a456-426614174000"
}
```

#### Refresh Token
```http
POST /api/v1/auth/refresh-token
Content-Type: application/json

{
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}
```

#### Logout
```http
POST /api/v1/auth/logout
Content-Type: application/json

{
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}
```

### Endpoints Protegidos

Para acceder a endpoints protegidos, incluye el token JWT en el header:

```http
GET /api/v1/users
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
```


---

## 🚀 Ejemplo de Uso Rápido

A continuación se muestra un flujo completo de autenticación usando el usuario de ejemplo:

### 1. Registrar un Nuevo Usuario

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "correo": "juan.perez@example.com",
    "nombre": "Juan Pérez",
    "password": "SecurePass123!",
    "phones": [
      {
        "number": "987654321",
        "cityCode": "1",
        "countryCode": "+57"
      }
    ]
  }'
```

**Respuesta:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "activo": true,
  "creado": "2025-11-20T15:30:00",
  "modificado": "2025-11-20T15:30:00",
  "ultimoLogin": "2025-11-20T15:30:00",
  "id": "123e4567-e89b-12d3-a456-426614174000"
}
```

### 2. Iniciar Sesión

```bash
curl -X POST http://localhost:8080/api/v1/auth/authenticate \
  -H "Content-Type: application/json" \
  -d '{
    "username": "juan.perez@example.com",
    "password": "SecurePass123!"
  }'
```

### 3. Usar un Endpoint Protegido

```bash
# Reemplaza YOUR_ACCESS_TOKEN con el token recibido
curl -X GET http://localhost:8080/api/v1/users \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### 4. Refrescar el Token

```bash
# Reemplaza YOUR_REFRESH_TOKEN con el refresh token recibido
curl -X POST http://localhost:8080/api/v1/auth/refresh-token \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "YOUR_REFRESH_TOKEN"
  }'
```

### 5. Cerrar Sesión

```bash
curl -X POST http://localhost:8080/api/v1/auth/logout \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "YOUR_REFRESH_TOKEN"
  }'
```

---

## 📚 Documentación API

### Swagger UI

Accede a la documentación interactiva en:
```
http://localhost:8080/api/v1/swagger-ui.html
```

Desde aquí puedes:
- ✅ Ver todos los endpoints disponibles
- ✅ Probar las APIs directamente
- ✅ Ver los esquemas de request/response
- ✅ Autenticarte y probar endpoints protegidos

---

## 🧪 Testing

El proyecto implementa una estrategia de testing completa con **tests unitarios** y **tests de integración**.

### Ejecutar Todos los Tests

```bash
mvn test
```

### Ejecutar Solo Tests Unitarios

```bash
mvn test -Dtest="*Test"
```

### Ejecutar Solo Tests de Integración

```bash
mvn test -Dtest="*IntegrationTest"
```

### Cobertura de Tests

El proyecto incluye:

- ✅ **Tests Unitarios**: Prueban la lógica de negocio de forma aislada usando Mockito
  - `AuthServiceImplTest`: 12 tests cubriendo registro, login, refresh token, logout
  - `UserServiceImplTest`: Tests de gestión de usuarios
  - `PhoneServiceImplTest`: Tests de gestión de teléfonos
  - `PasswordValidatorTest`: Tests de validación de contraseñas

- ✅ **Tests de Integración**: Prueban el flujo completo end-to-end
  - `AuthControllerIntegrationTest`: 12 tests cubriendo todos los endpoints de autenticación
  - Usa MockMvc para simular peticiones HTTP reales
  - Verifica validaciones, seguridad y respuestas JSON

### Principios de Testing

Para entender los **principios, patrones y mejores prácticas** aplicados en los tests, consulta:

📖 **[Documentación de Principios de Testing](docs/TESTING_PRINCIPLES.md)**

Este documento explica:
- Estrategia de testing (Unit vs Integration)
- Principios aplicados (AAA, Test Isolation, BDD, etc.)
- Patrones utilizados (Builder, Factory, Test Fixture)
- Justificación de decisiones técnicas
- Mejores prácticas y anti-patrones


## 📁 Estructura del Proyecto

```
prueba/
├── src/
│   ├── main/
│   │   ├── java/com/ntt/prueba/
│   │   │   ├── auth/                    # Módulo de autenticación
│   │   │   │   ├── controller/          # Controladores REST
│   │   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   │   ├── entity/              # Entidades JPA
│   │   │   │   ├── mapper/              # Mappers MapStruct
│   │   │   │   ├── repository/          # Repositorios JPA
│   │   │   │   ├── service/             # Servicios de negocio
│   │   │   │   └── validation/          # Validadores personalizados
│   │   │   ├── security/                # Configuración de seguridad
│   │   │   ├── shared/                  # Clases compartidas
│   │   │   ├── exception/               # Manejo de excepciones
│   │   │   └── PruebaApplication.java   # Clase principal
│   │   └── resources/
│   │       ├── application.yml          # Configuración principal
│   │       └── application-dev.yml      # Configuración desarrollo
│   └── test/                            # Tests unitarios e integración
├── logs/                                # Archivos de log
├── target/                              # Archivos compilados
├── pom.xml                              # Dependencias Maven
└── README.md                            # Este archivo
```

---

## 🔐 Seguridad

### Requisitos de Contraseña(En el perfil de Produccion)

Por defecto, las contraseñas deben cumplir:
- ✅ Mínimo 8 caracteres
- ✅ Al menos una letra mayúscula
- ✅ Al menos una letra minúscula
- ✅ Al menos un número
- ✅ Al menos un carácter especial (@#$%^&+=)

### Requisitos de Contraseña(En el perfil de Desarrollo)

Por defecto, las contraseñas deben cumplir:
- ✅ Mínimo 4 caracteres

### JWT Configuration

Los tokens JWT tienen una expiración configurable. Revisa `SecurityConfig.java` para ajustar:
- Tiempo de expiración del access token
- Tiempo de expiración del refresh token
- Clave secreta (cambiar en producción)

### Sistema de Auditoría de Autenticación

El sistema incluye un módulo completo de auditoría que registra todos los eventos de autenticación:

#### Eventos Registrados

| Evento | Descripción |
|--------|-------------|
| `LOGIN` | Inicio de sesión exitoso (registro o autenticación) |
| `LOGOUT` | Cierre de sesión |
| `TOKEN_REFRESH` | Renovación de token de acceso |
| `TOKEN_EXPIRED` | Intento de uso de token expirado |
| `FAILED_LOGIN` | Intento fallido de inicio de sesión |

#### Información Capturada

Para cada evento de auditoría se registra:
- ✅ Usuario asociado
- ✅ Tipo de evento
- ✅ Hash SHA-256 del access token (para seguridad)
- ✅ ID del refresh token
- ✅ Dirección IP del cliente
- ✅ User-Agent del navegador/cliente
- ✅ Timestamp del evento
- ✅ Estado (exitoso/fallido)

#### Características de Seguridad

- Los access tokens se almacenan como hash SHA-256, nunca en texto plano
- Los registros de auditoría son permanentes (no se eliminan con el logout)
- Captura de IP real considerando proxies (header `X-Forwarded-For`)
- Registro de errores sin afectar el flujo de autenticación

---

## 🐛 Troubleshooting

### Puerto ya en uso

Si el puerto 8080 está ocupado, cámbialo en `application.yml`:

```yaml
server:
  port: 9090  # Cambiar a otro puerto disponible
```

### Error de compilación con Lombok

Asegúrate de tener el plugin de Lombok instalado en tu IDE:
- **IntelliJ IDEA**: Settings → Plugins → Lombok
- **Eclipse**: Instalar desde https://projectlombok.org/
- **VS Code**: Instalar extensión "Lombok Annotations Support"

### Base de datos H2 no accesible

Para habilitar la consola H2, agrega en `application.yml`:

```yaml
spring:
  h2:
    console:
      enabled: true
      path: /h2-console
```

Accede en: `http://localhost:8080/api/v1/h2-console`

---

## 📝 Logs

Los logs se guardan en:
- **Archivo**: `logs/backend.log`
- **Consola**: Salida estándar con formato personalizado

### Cambiar Nivel de Logs

En `application.yml`:

```yaml
logging:
  level:
    root: INFO
    com.ntt.prueba: DEBUG
```


