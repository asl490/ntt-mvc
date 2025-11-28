# Principios y Estrategias de Testing

## 📋 Tabla de Contenidos

- [Contexto del Proyecto](#contexto-del-proyecto)
- [Estrategia de Testing](#estrategia-de-testing)
- [Principios Aplicados](#principios-aplicados)
- [Arquitectura de Tests](#arquitectura-de-tests)
- [Patrones Utilizados](#patrones-utilizados)
- [Justificación de Decisiones](#justificación-de-decisiones)

---

## 🎯 Contexto del Proyecto

Este proyecto implementa un **sistema de autenticación JWT** con las siguientes características:

- Registro y autenticación de usuarios
- Gestión de roles y permisos
- Refresh tokens para renovación de sesiones
- Auditoría completa de eventos de autenticación
- Validación dinámica de contraseñas
- Soporte para múltiples teléfonos por usuario

### Desafíos de Testing

1. **Seguridad Crítica**: La autenticación es un componente crítico que requiere alta confiabilidad
2. **Lógica de Negocio Compleja**: Múltiples flujos (registro, login, refresh, logout)
3. **Integración con Múltiples Capas**: Controllers, Services, Repositories, Security
4. **Auditoría**: Verificar que todos los eventos se registren correctamente

---

## 🧪 Estrategia de Testing

Se implementó una **estrategia de testing en dos niveles**:

### 1. Tests Unitarios (Unit Tests)
- **Objetivo**: Probar la lógica de negocio de forma aislada
- **Alcance**: Capa de servicio (`AuthServiceImpl`, `UserServiceImpl`, etc.)
- **Herramientas**: JUnit 5 + Mockito
- **Cobertura**: ~80% de la lógica de negocio

### 2. Tests de Integración (Integration Tests)
- **Objetivo**: Probar el flujo completo end-to-end
- **Alcance**: Desde el controller hasta la base de datos
- **Herramientas**: Spring Boot Test + MockMvc
- **Cobertura**: Todos los endpoints principales

---

## 🎓 Principios Aplicados

### 1. **AAA Pattern (Arrange-Act-Assert)**

Todos los tests siguen el patrón AAA para máxima claridad:

```java
@Test
void testRegister_Success_DefaultRole() {
    // Arrange - Preparar datos y mocks
    RegisterRequest request = TestDataBuilder.defaultRegisterRequest().build();
    when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
    
    // Act - Ejecutar la acción a probar
    AuthResponse response = authService.register(request);
    
    // Assert - Verificar resultados
    assertNotNull(response);
    verify(userRepository, times(1)).save(any(User.class));
}
```

**¿Por qué?**
- **Legibilidad**: Cualquier desarrollador puede entender el test rápidamente
- **Mantenibilidad**: Cambios en el código son fáciles de reflejar en los tests
- **Debugging**: Fácil identificar qué parte del test falla

### 2. **Test Isolation (Aislamiento de Tests)**

Cada test es completamente independiente:

```java
@BeforeEach
void setUp() {
    testUser = TestDataBuilder.defaultUser().build();
    userRole = TestDataBuilder.defaultRole().build();
    testRefreshToken = TestDataBuilder.defaultRefreshToken(testUser).build();
}
```

**¿Por qué?**
- **Confiabilidad**: Un test no afecta a otro
- **Paralelización**: Los tests pueden ejecutarse en paralelo
- **Debugging**: Fácil reproducir fallos individuales

### 3. **Given-When-Then (BDD Style)**

Los nombres de los tests describen el comportamiento esperado:

```java
@Test
@DisplayName("Should register new user successfully with default USER role")
void testRegister_Success_DefaultRole() { ... }

@Test
@DisplayName("Should throw exception when registering with existing email")
void testRegister_DuplicateEmail() { ... }
```

**¿Por qué?**
- **Documentación Viva**: Los tests sirven como documentación del comportamiento
- **Comunicación**: Facilita la comunicación entre desarrolladores y stakeholders
- **Especificación**: Define claramente qué debe hacer el sistema

### 4. **Test Data Builders Pattern**

Se creó una clase `TestDataBuilder` para construir datos de prueba:

```java
public static User.UserBuilder<?, ?> defaultUser() {
    return User.builder()
        .id(UUID.randomUUID())
        .username("test@example.com")
        .name("Test User")
        .password("$2a$10$XYZ123")
        .roles(List.of(defaultRole().build()))
        .phones(new ArrayList<>());
}
```

**¿Por qué?**
- **Reutilización**: Evita duplicación de código de setup
- **Consistencia**: Datos de prueba uniformes en todos los tests
- **Flexibilidad**: Fácil crear variaciones (usuarios admin, con teléfonos, etc.)
- **Mantenibilidad**: Cambios en entidades se reflejan en un solo lugar

### 5. **Mocking Strategy**

Se usa Mockito para simular dependencias:

```java
@Mock
private UserRepository userRepository;

@Mock
private JwtService jwtService;

@InjectMocks
private AuthServiceImpl authService;
```

**¿Por qué?**
- **Velocidad**: Los tests unitarios son extremadamente rápidos
- **Control**: Control total sobre el comportamiento de las dependencias
- **Aislamiento**: Probar solo la lógica del servicio, no sus dependencias

### 6. **Test Coverage for Edge Cases**

Se prueban tanto casos exitosos como casos de error:

```java
// Caso exitoso
@Test
void testAuthenticate_Success() { ... }

// Casos de error
@Test
void testAuthenticate_InvalidCredentials() { ... }

@Test
void testAuthenticate_UserNotFound() { ... }
```

**¿Por qué?**
- **Robustez**: Asegurar que el sistema maneja errores correctamente
- **Seguridad**: Verificar que las validaciones funcionan
- **Confiabilidad**: Cubrir todos los flujos posibles

### 7. **Integration Testing with Real Context**

Los tests de integración usan el contexto completo de Spring:

```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testRegister_Success() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken", notNullValue()));
    }
}
```

**¿Por qué?**
- **Realismo**: Prueba el sistema como lo usaría un cliente real
- **Validación Completa**: Verifica serialización, validaciones, seguridad, etc.
- **Confianza**: Mayor confianza en que el sistema funciona end-to-end

---

## 🏗️ Arquitectura de Tests

### Estructura de Directorios

```
src/test/java/
├── com/ntt/prueba/
│   ├── auth/
│   │   ├── controller/
│   │   │   └── AuthControllerIntegrationTest.java    # Tests de integración
│   │   ├── service/
│   │   │   └── impl/
│   │   │       ├── AuthServiceImplTest.java          # Tests unitarios
│   │   │       ├── UserServiceImplTest.java
│   │   │       └── PhoneServiceImplTest.java
│   │   └── validation/
│   │       └── PasswordValidatorTest.java            # Tests de validación
│   └── util/
│       └── TestDataBuilder.java                      # Builder de datos de prueba
```

### Capas de Testing

```
┌─────────────────────────────────────┐
│   Integration Tests (E2E)           │  ← Flujo completo
│   AuthControllerIntegrationTest     │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│   Unit Tests (Service Layer)        │  ← Lógica de negocio
│   AuthServiceImplTest               │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│   Unit Tests (Validation Layer)     │  ← Validaciones
│   PasswordValidatorTest              │
└─────────────────────────────────────┘
```

---

## 🎨 Patrones Utilizados

### 1. **Builder Pattern** (Test Data)

```java
// Uso del builder para crear datos de prueba
User testUser = TestDataBuilder.defaultUser()
    .username("custom@example.com")
    .name("Custom User")
    .build();
```

**Ventajas**:
- Código fluido y legible
- Fácil personalización de datos
- Valores por defecto sensatos

### 2. **Factory Pattern** (Test Data Creation)

```java
public class TestDataBuilder {
    public static User.UserBuilder<?, ?> defaultUser() { ... }
    public static User.UserBuilder<?, ?> adminUser() { ... }
    public static RefreshToken.RefreshTokenBuilder<?, ?> expiredRefreshToken(User user) { ... }
}
```

**Ventajas**:
- Centralización de la creación de objetos de prueba
- Fácil mantenimiento
- Consistencia en los tests

### 3. **Arrange-Act-Assert Pattern**

Ya explicado anteriormente, es el patrón fundamental de estructura de tests.

### 4. **Test Fixture Pattern**

```java
@BeforeEach
void setUp() {
    // Preparar el estado inicial común a todos los tests
    testUser = TestDataBuilder.defaultUser().build();
    userRole = TestDataBuilder.defaultRole().build();
}
```

**Ventajas**:
- Reduce duplicación de código de setup
- Garantiza estado inicial consistente
- Facilita el mantenimiento

---

## 💡 Justificación de Decisiones

### ¿Por qué JUnit 5 en lugar de JUnit 4?

**Decisión**: Usar JUnit 5 (Jupiter)

**Razones**:
1. **Anotaciones más expresivas**: `@DisplayName`, `@Nested`, `@ParameterizedTest`
2. **Mejor integración con Java 8+**: Lambdas, streams
3. **Extensiones más potentes**: `@ExtendWith(MockitoExtension.class)`
4. **Mejor soporte para tests parametrizados**

### ¿Por qué Mockito para Unit Tests?

**Decisión**: Usar Mockito para mockear dependencias

**Razones**:
1. **Estándar de la industria**: Ampliamente usado y documentado
2. **API fluida**: `when().thenReturn()` es muy legible
3. **Verificación potente**: `verify()` para asegurar interacciones
4. **Integración con Spring**: `@MockBean` para tests de integración

### ¿Por qué MockMvc en lugar de RestTemplate?

**Decisión**: Usar MockMvc para tests de integración

**Razones**:
1. **No requiere servidor**: Más rápido que levantar un servidor real
2. **Control total**: Acceso completo al contexto de Spring
3. **Assertions potentes**: `andExpect()` para verificar respuestas
4. **Mejor para CI/CD**: Más rápido y confiable

### ¿Por qué TestDataBuilder en lugar de datos inline?

**Decisión**: Crear una clase `TestDataBuilder` centralizada

**Razones**:
1. **DRY Principle**: No repetir datos de prueba en cada test
2. **Mantenibilidad**: Cambios en entidades se reflejan en un solo lugar
3. **Legibilidad**: `TestDataBuilder.defaultUser()` es más claro que crear el objeto manualmente
4. **Flexibilidad**: Fácil crear variaciones (usuarios admin, con teléfonos, etc.)

### ¿Por qué @Transactional en Integration Tests?

**Decisión**: Usar `@Transactional` en tests de integración

**Razones**:
1. **Rollback automático**: Cada test deja la BD en estado limpio
2. **Aislamiento**: Tests no se afectan entre sí
3. **Velocidad**: No necesita limpiar la BD manualmente
4. **Simplicidad**: No requiere código de limpieza

### ¿Por qué separar Unit e Integration Tests?

**Decisión**: Tener clases separadas para tests unitarios e integración

**Razones**:
1. **Velocidad**: Tests unitarios son mucho más rápidos
2. **Feedback rápido**: Ejecutar solo unit tests durante desarrollo
3. **CI/CD**: Ejecutar unit tests en cada commit, integration tests antes de merge
4. **Claridad**: Separación clara de responsabilidades

---

## 📊 Cobertura de Tests

### Tests Unitarios (AuthServiceImplTest)

| Funcionalidad | Tests | Cobertura |
|---------------|-------|-----------|
| Registro | 4 tests | ✅ Éxito, duplicado, con roles, con teléfonos |
| Autenticación | 3 tests | ✅ Éxito, credenciales inválidas, usuario no encontrado |
| Refresh Token | 3 tests | ✅ Éxito, token no encontrado, token expirado |
| Logout | 2 tests | ✅ Éxito, token inválido |
| **Total** | **12 tests** | **100% de flujos principales** |

### Tests de Integración (AuthControllerIntegrationTest)

| Endpoint | Tests | Cobertura |
|----------|-------|-----------|
| POST /register | 6 tests | ✅ Validaciones completas |
| POST /authenticate | 2 tests | ✅ Casos de error |
| POST /refresh-token | 2 tests | ✅ Éxito y error |
| POST /logout | 2 tests | ✅ Éxito y error |
| **Total** | **12 tests** | **Todos los endpoints** |

---

## 🎯 Beneficios Obtenidos

### 1. **Confianza en el Código**
- Los tests garantizan que el sistema funciona como se espera
- Refactorings seguros: los tests detectan regresiones

### 2. **Documentación Viva**
- Los tests describen cómo debe comportarse el sistema
- Ejemplos de uso de las APIs

### 3. **Desarrollo Más Rápido**
- Feedback inmediato al hacer cambios
- Menos tiempo debugging en producción

### 4. **Mejor Diseño**
- El código testeable tiende a ser mejor diseñado
- Fomenta la separación de responsabilidades

### 5. **Seguridad**
- Tests de seguridad verifican autenticación y autorización
- Auditoría verificada automáticamente

---

## 🚀 Mejores Prácticas Aplicadas

### ✅ DO (Hacer)

1. **Nombres descriptivos**: `testRegister_Success_DefaultRole`
2. **Un concepto por test**: Cada test verifica una sola cosa
3. **Tests independientes**: No dependen del orden de ejecución
4. **Datos de prueba consistentes**: Usar `TestDataBuilder`
5. **Verificar comportamiento, no implementación**: `verify()` solo lo necesario
6. **Tests rápidos**: Unit tests < 100ms, Integration tests < 5s

### ❌ DON'T (Evitar)

1. **Tests frágiles**: No depender de detalles de implementación
2. **Datos hardcodeados**: Usar builders en su lugar
3. **Tests que dependen de otros**: Cada test debe ser independiente
4. **Sobre-mocking**: No mockear todo, usar objetos reales cuando sea simple
5. **Tests sin assertions**: Siempre verificar el resultado
6. **Ignorar tests fallidos**: Arreglar o eliminar, nunca ignorar

---

## 📚 Recursos y Referencias

### Frameworks y Librerías

- **JUnit 5**: https://junit.org/junit5/
- **Mockito**: https://site.mockito.org/
- **Spring Boot Test**: https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing
- **AssertJ**: https://assertj.github.io/doc/ (opcional, para assertions más fluidas)

### Principios y Patrones

- **Test-Driven Development (TDD)**: Kent Beck
- **Clean Code**: Robert C. Martin (Uncle Bob)
- **xUnit Test Patterns**: Gerard Meszaros
- **Growing Object-Oriented Software, Guided by Tests**: Steve Freeman & Nat Pryce

### Artículos Recomendados

- Martin Fowler - "Mocks Aren't Stubs"
- Martin Fowler - "Test Pyramid"
- Google Testing Blog - "Test Sizes"

---

## 🔄 Evolución Futura

### Mejoras Planificadas

1. **Tests Parametrizados**: Para reducir duplicación en tests similares
2. **Contract Testing**: Para APIs externas
3. **Performance Testing**: Para endpoints críticos
4. **Security Testing**: Tests específicos de seguridad (OWASP)
5. **Mutation Testing**: Para verificar calidad de los tests (PIT)

### Métricas a Monitorear

- **Cobertura de código**: Objetivo > 80%
- **Tiempo de ejecución**: Unit tests < 5s, Integration tests < 30s
- **Tasa de fallos**: Objetivo < 1%
- **Deuda técnica**: Tests pendientes o ignorados

---

## 📝 Conclusión

La estrategia de testing implementada en este proyecto sigue las **mejores prácticas de la industria** y está diseñada para:

1. **Garantizar la calidad** del código
2. **Facilitar el mantenimiento** a largo plazo
3. **Documentar el comportamiento** del sistema
4. **Permitir refactorings seguros**
5. **Acelerar el desarrollo** con feedback rápido

Los principios aplicados (AAA, Test Isolation, BDD, Test Data Builders, etc.) son **estándares de la industria** que han demostrado su eficacia en proyectos de todos los tamaños.

La combinación de **tests unitarios** (rápidos, aislados) y **tests de integración** (realistas, completos) proporciona una **red de seguridad robusta** que permite desarrollar con confianza.
