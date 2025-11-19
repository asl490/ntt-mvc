# Politech Backend - Sistema de Autenticación

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

### Configuración de Validación de Contraseñas

Puedes personalizar los requisitos de contraseña en `application.yml`:

```yaml
validation:
  password:
    pattern: "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$"
    message: "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial"
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

- **URL Base**: `http://localhost:6969/api/v1`
- **Swagger UI**: `http://localhost:6969/api/v1/swagger-ui.html`
- **OpenAPI Docs**: `http://localhost:6969/api/v1/v3/api-docs`
- **Health Check**: `http://localhost:6969/api/v1/actuator/health`

---

## 📡 Endpoints Principales

### Autenticación

#### Registro de Usuario
```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "correo": "usuario@example.com",
  "nombre": "Juan Pérez",
  "password": "Password123!",
  "roleNames": ["USER"],
  "phones": [
    {
      "number": "1234567890",
      "cityCode": "01",
      "countryCode": "+57"
    }
  ]
}
```

#### Login
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "usuario@example.com",
  "password": "Password123!"
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
POST /api/v1/auth/refresh
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

## 📚 Documentación API

### Swagger UI

Accede a la documentación interactiva en:
```
http://localhost:6969/api/v1/swagger-ui.html
```

Desde aquí puedes:
- ✅ Ver todos los endpoints disponibles
- ✅ Probar las APIs directamente
- ✅ Ver los esquemas de request/response
- ✅ Autenticarte y probar endpoints protegidos

---

## 🧪 Testing

### Ejecutar Todos los Tests

```bash
mvn test
```

### Ejecutar Tests con Cobertura

```bash
mvn clean test jacoco:report
```

### Ver Reporte de Cobertura

El reporte se genera en: `target/site/jacoco/index.html`

---

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

### Requisitos de Contraseña

Por defecto, las contraseñas deben cumplir:
- ✅ Mínimo 8 caracteres
- ✅ Al menos una letra mayúscula
- ✅ Al menos una letra minúscula
- ✅ Al menos un número
- ✅ Al menos un carácter especial (@#$%^&+=)

### JWT Configuration

Los tokens JWT tienen una expiración configurable. Revisa `SecurityConfig.java` para ajustar:
- Tiempo de expiración del access token
- Tiempo de expiración del refresh token
- Clave secreta (cambiar en producción)

---

## 🐛 Troubleshooting

### Puerto ya en uso

Si el puerto 6969 está ocupado, cámbialo en `application.yml`:

```yaml
server:
  port: 8080  # Cambiar a otro puerto
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

Accede en: `http://localhost:6969/api/v1/h2-console`

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

---

## 🤝 Contribuir

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

---

## 📄 Licencia

Este proyecto es privado y confidencial.

---

## 👥 Contacto

Para preguntas o soporte, contacta al equipo de desarrollo.

---

## 🔄 Actualizaciones Recientes

### v0.0.1-SNAPSHOT
- ✅ Sistema de autenticación JWT
- ✅ Gestión de usuarios y roles
- ✅ Soporte para múltiples teléfonos por usuario
- ✅ Validación dinámica de contraseñas
- ✅ Documentación OpenAPI/Swagger
- ✅ Relaciones bidireccionales con cascade
- ✅ Refresh token functionality
