# Diagramas del Sistema - NTT-MVC Authentication System

Esta documentación contiene diferentes tipos de diagramas que puedes usar para presentar el sistema.

---

## 1. Diagrama de Arquitectura del Sistema

```mermaid
graph TB
    subgraph "Cliente"
        Client[Cliente Web/Mobile]
    end
    
    subgraph "Capa de Presentación"
        Controller[AuthController<br/>REST API]
    end
    
    subgraph "Capa de Seguridad"
        JwtFilter[JwtAuthenticationFilter]
        JwtService[JwtService]
        SecurityConfig[SecurityConfig]
    end
    
    subgraph "Capa de Negocio"
        AuthService[AuthService]
        UserService[UserService]
        PhoneService[PhoneService]
    end
    
    subgraph "Capa de Validación"
        PasswordValidator[PasswordValidator]
        PhoneValidator[PhoneValidator]
    end
    
    subgraph "Capa de Persistencia"
        UserRepo[UserRepository]
        RoleRepo[RoleRepository]
        PhoneRepo[PhoneRepository]
        TokenRepo[RefreshTokenRepository]
        AuditRepo[AuthAuditRepository]
    end
    
    subgraph "Base de Datos"
        DB[(H2/PostgreSQL/MySQL)]
    end
    
    Client -->|HTTP Request| Controller
    Controller -->|Intercepta| JwtFilter
    JwtFilter -->|Valida JWT| JwtService
    JwtFilter -->|Autoriza| SecurityConfig
    Controller -->|Llama| AuthService
    AuthService -->|Usa| UserService
    AuthService -->|Usa| PhoneService
    AuthService -->|Valida| PasswordValidator
    PhoneService -->|Valida| PhoneValidator
    UserService -->|CRUD| UserRepo
    AuthService -->|CRUD| RoleRepo
    PhoneService -->|CRUD| PhoneRepo
    AuthService -->|CRUD| TokenRepo
    AuthService -->|Audita| AuditRepo
    UserRepo -->|JPA/Hibernate| DB
    RoleRepo -->|JPA/Hibernate| DB
    PhoneRepo -->|JPA/Hibernate| DB
    TokenRepo -->|JPA/Hibernate| DB
    AuditRepo -->|JPA/Hibernate| DB
    
    style Client fill:#e1f5ff
    style Controller fill:#fff4e1
    style JwtFilter fill:#ffe1e1
    style AuthService fill:#e1ffe1
    style DB fill:#f0e1ff
```

---

## 2. Diagrama de Flujo de Autenticación

### 2.1 Flujo de Registro

```mermaid
sequenceDiagram
    participant C as Cliente
    participant AC as AuthController
    participant AS as AuthService
    participant PV as PasswordValidator
    participant US as UserService
    participant DB as Database
    participant JS as JwtService
    
    C->>AC: POST /register
    AC->>AS: register(RegisterRequest)
    AS->>PV: validate(password)
    
    alt Contraseña inválida
        PV-->>AS: ValidationException
        AS-->>AC: Error
        AC-->>C: 400 Bad Request
    else Contraseña válida
        PV-->>AS: OK
        AS->>US: findByUsername(email)
        
        alt Usuario ya existe
            US-->>AS: User found
            AS-->>AC: UserAlreadyExistsException
            AC-->>C: 409 Conflict
        else Usuario nuevo
            US-->>AS: null
            AS->>AS: hashPassword(password)
            AS->>DB: save(User, Roles, Phones)
            DB-->>AS: User saved
            AS->>JS: generateToken(user)
            JS-->>AS: JWT Token
            AS->>DB: save(RefreshToken)
            AS->>DB: save(AuthAudit - REGISTER)
            AS-->>AC: AuthResponse
            AC-->>C: 200 OK + Tokens
        end
    end
```

### 2.2 Flujo de Login

```mermaid
sequenceDiagram
    participant C as Cliente
    participant AC as AuthController
    participant AS as AuthService
    participant US as UserService
    participant DB as Database
    participant JS as JwtService
    
    C->>AC: POST /authenticate
    AC->>AS: authenticate(AuthRequest)
    AS->>US: findByUsername(email)
    
    alt Usuario no existe
        US-->>AS: null
        AS->>DB: save(AuthAudit - FAILED_LOGIN)
        AS-->>AC: BadCredentialsException
        AC-->>C: 401 Unauthorized
    else Usuario existe
        US-->>AS: User
        AS->>AS: verifyPassword(password, hash)
        
        alt Contraseña incorrecta
            AS->>DB: save(AuthAudit - FAILED_LOGIN)
            AS-->>AC: BadCredentialsException
            AC-->>C: 401 Unauthorized
        else Contraseña correcta
            AS->>JS: generateToken(user)
            JS-->>AS: JWT Token
            AS->>DB: save(RefreshToken)
            AS->>DB: update(User.lastLogin)
            AS->>DB: save(AuthAudit - LOGIN)
            AS-->>AC: AuthResponse
            AC-->>C: 200 OK + Tokens
        end
    end
```

### 2.3 Flujo de Refresh Token

```mermaid
sequenceDiagram
    participant C as Cliente
    participant AC as AuthController
    participant AS as AuthService
    participant DB as Database
    participant JS as JwtService
    
    C->>AC: POST /refresh-token
    AC->>AS: refreshToken(RefreshTokenRequest)
    AS->>DB: findByToken(refreshToken)
    
    alt Token no existe
        DB-->>AS: null
        AS-->>AC: InvalidTokenException
        AC-->>C: 401 Unauthorized
    else Token existe
        DB-->>AS: RefreshToken
        AS->>AS: verifyExpiration(token)
        
        alt Token expirado
            AS->>DB: delete(RefreshToken)
            AS-->>AC: TokenExpiredException
            AC-->>C: 401 Unauthorized
        else Token válido
            AS->>JS: generateToken(user)
            JS-->>AS: New JWT Token
            AS->>DB: save(new RefreshToken)
            AS->>DB: delete(old RefreshToken)
            AS->>DB: save(AuthAudit - TOKEN_REFRESH)
            AS-->>AC: AuthResponse
            AC-->>C: 200 OK + New Tokens
        end
    end
```

---

## 3. Diagrama de Componentes

```mermaid
graph LR
    subgraph "Frontend Layer"
        UI[User Interface]
    end
    
    subgraph "API Gateway"
        Gateway[Spring Boot<br/>Port 8080]
    end
    
    subgraph "Security Layer"
        JWT[JWT Filter]
        Auth[Spring Security]
    end
    
    subgraph "Business Logic"
        AuthModule[Auth Module]
        UserModule[User Module]
        PhoneModule[Phone Module]
    end
    
    subgraph "Data Access Layer"
        JPA[Spring Data JPA]
        Repos[Repositories]
    end
    
    subgraph "Database"
        H2[(H2 Database)]
        Postgres[(PostgreSQL)]
        MySQL[(MySQL)]
    end
    
    UI -->|REST API| Gateway
    Gateway --> JWT
    JWT --> Auth
    Auth --> AuthModule
    Auth --> UserModule
    Auth --> PhoneModule
    AuthModule --> JPA
    UserModule --> JPA
    PhoneModule --> JPA
    JPA --> Repos
    Repos -.->|Dev| H2
    Repos -.->|Prod| Postgres
    Repos -.->|Prod| MySQL
```

---

## 4. Diagrama de Clases (Entidades)

```mermaid
classDiagram
    class BaseEntity {
        <<abstract>>
        -UUID id
        -Boolean isDeleted
    }
    
    class Auditable {
        <<abstract>>
        -String createdBy
        -LocalDateTime createdDate
        -String lastModifiedBy
        -LocalDateTime lastModifiedDate
    }
    
    class User {
        -String username
        -String name
        -String password
        -LocalDateTime lastlogin
        -List~Role~ roles
        -List~Phone~ phones
        -List~RefreshToken~ refreshTokens
    }
    
    class Role {
        -String name
    }
    
    class Phone {
        -String number
        -String cityCode
        -String countryCode
        -User user
    }
    
    class RefreshToken {
        -String token
        -Instant expiryDate
        -User user
    }
    
    class AuthenticationAudit {
        -User user
        -AuthEventType eventType
        -String accessTokenHash
        -UUID refreshTokenId
        -String ipAddress
        -String userAgent
        -LocalDateTime eventTime
        -Boolean successful
    }
    
    class AuthEventType {
        <<enumeration>>
        LOGIN
        LOGOUT
        FAILED_LOGIN
        TOKEN_REFRESH
        REGISTER
    }
    
    BaseEntity <|-- Auditable
    Auditable <|-- User
    Auditable <|-- Role
    Auditable <|-- Phone
    Auditable <|-- RefreshToken
    Auditable <|-- AuthenticationAudit
    
    User "1" --> "*" Phone
    User "*" --> "*" Role
    User "1" --> "*" RefreshToken
    User "1" --> "*" AuthenticationAudit
    AuthenticationAudit --> AuthEventType
```

---

## 5. Diagrama de Casos de Uso

```mermaid
graph LR
    subgraph "Sistema de Autenticación"
        UC1[Registrar Usuario]
        UC2[Iniciar Sesión]
        UC3[Cerrar Sesión]
        UC4[Refrescar Token]
        UC5[Gestionar Teléfonos]
        UC6[Gestionar Roles]
        UC7[Auditar Eventos]
    end
    
    User[Usuario] --> UC1
    User --> UC2
    User --> UC3
    User --> UC4
    User --> UC5
    
    Admin[Administrador] --> UC6
    Admin --> UC7
    Admin --> UC1
    Admin --> UC2
    Admin --> UC3
    Admin --> UC4
    Admin --> UC5
    
    UC1 -.->|include| Validate[Validar Contraseña]
    UC1 -.->|include| Hash[Hash Password]
    UC2 -.->|include| GenToken[Generar JWT]
    UC4 -.->|include| VerifyToken[Verificar Token]
    
    style User fill:#e1f5ff
    style Admin fill:#ffe1e1
```

---

## 6. Diagrama de Paquetes

```mermaid
graph TB
    subgraph "com.ntt.prueba"
        Main[PruebaApplication]
        
        subgraph "auth"
            AuthController[controller]
            AuthService[service]
            AuthDTO[dto]
            AuthEntity[entity]
            AuthRepo[repository]
            AuthMapper[mapper]
            AuthValidation[validation]
        end
        
        subgraph "security"
            JwtFilter[JwtAuthenticationFilter]
            JwtService[JwtService]
            SecurityConf[SecurityConfig]
            AuthEntry[CustomAuthenticationEntryPoint]
        end
        
        subgraph "config"
            WebConfig[WebConfig]
            AuditConfig[AuditConfig]
            OpenAPIConfig[OpenAPIConfig]
        end
        
        subgraph "shared"
            BaseEntity[BaseEntity]
            Auditable[Auditable]
        end
        
        subgraph "exception"
            GlobalHandler[GlobalExceptionHandler]
            CustomExceptions[Custom Exceptions]
        end
    end
    
    Main --> auth
    Main --> security
    Main --> config
    
    AuthController --> AuthService
    AuthService --> AuthRepo
    AuthService --> AuthMapper
    AuthService --> AuthValidation
    AuthEntity -.->|extends| Auditable
    Auditable -.->|extends| BaseEntity
    
    security --> auth
    config --> security
    exception --> auth
```

---

## Cómo Usar Estos Diagramas

### Renderizar en Markdown
Estos diagramas usan **Mermaid**, que es compatible con:
- GitHub
- GitLab
- VS Code (con extensión)
- Notion
- Confluence
- Documentación web

### Exportar como Imagen
1. **Online**: Usa [Mermaid Live Editor](https://mermaid.live/)
   - Copia el código del diagrama
   - Exporta como PNG/SVG

2. **VS Code**: Instala la extensión "Markdown Preview Mermaid Support"
   - Abre este archivo
   - Click derecho → "Markdown: Export (pdf, html, png, jpeg)"

3. **CLI**: Usa `mermaid-cli`
   ```bash
   npm install -g @mermaid-js/mermaid-cli
   mmdc -i diagrama.md -o diagrama.png
   ```

### Personalizar
Puedes modificar los colores y estilos agregando al final de cada diagrama:
```
style NombreNodo fill:#color,stroke:#color,stroke-width:2px
```

---

## Recomendaciones por Contexto

| Contexto | Diagramas Recomendados |
|----------|------------------------|
| **Presentación Técnica** | Arquitectura + Componentes + Clases |
| **Documentación de Usuario** | Casos de Uso + Flujo de Autenticación |
| **Reunión con Stakeholders** | Arquitectura + Despliegue |
| **Onboarding de Desarrolladores** | Componentes + Paquetes + Clases |
| **Documentación de API** | Flujos de Autenticación (Secuencia) |
| **Revisión de Seguridad** | Flujo de Login + Estados + Arquitectura |
