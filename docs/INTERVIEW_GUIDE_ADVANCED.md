# Guía de Entrevista Técnica Avanzada - Backend Java

> **Nota**: Este documento complementa la guía principal con preguntas adicionales y más profundas, enfocándose en Java Core, Microservices, Testing, DevOps y Seguridad.

---

## 📚 Índice

1. [Java Core Avanzado](#1-java-core-avanzado)
2. [Microservicios y Arquitectura (Profundización)](#2-microservicios-y-arquitectura-profundización)
3. [Testing Avanzado y Estrategias](#3-testing-avanzado-y-estrategias)
4. [DevOps, SRE y Cloud Native](#4-devops-sre-y-cloud-native)
5. [Seguridad Avanzada (AppSec)](#5-seguridad-avanzada-appsec)

---

## 1. Java Core Avanzado

### **1. ¿Qué es el String Pool y cómo afecta la memoria?**
**Respuesta**: Es un área especial en el Heap donde se almacenan literales de cadenas. Si se crea un String con `""`, se reutiliza del pool. Si se usa `new String()`, se crea un nuevo objeto en el Heap fuera del pool. Ayuda a ahorrar memoria.

### **2. Explica el funcionamiento de los ClassLoaders y la jerarquía de delegación.**
**Respuesta**:
- **Bootstrap ClassLoader**: Carga clases del núcleo (rt.jar).
- **Platform/Extension ClassLoader**: Carga extensiones.
- **App/System ClassLoader**: Carga clases del classpath de la aplicación.
- **Delegación**: Un ClassLoader pide a su padre que cargue la clase antes de intentarlo él mismo.

### **3. ¿Qué son los Java Modules (JPMS) introducidos en Java 9?**
**Respuesta**: Sistema para modularizar la JDK y aplicaciones. Permite encapsulamiento fuerte (exportar solo paquetes específicos) y dependencias explícitas (`module-info.java`). Reduce el tamaño del runtime (jlink).

### **4. Diferencia entre `fail-fast` y `fail-safe` iterators.**
**Respuesta**:
- **Fail-fast**: Lanza `ConcurrentModificationException` si la colección cambia durante la iteración (ej. `ArrayList`, `HashMap`).
- **Fail-safe**: Trabaja sobre una copia o vista consistente, no lanza excepción (ej. `CopyOnWriteArrayList`, `ConcurrentHashMap`).

### **5. ¿Qué es el "Diamond Problem" en herencia y cómo lo resuelve Java?**
**Respuesta**: Ambigüedad cuando una clase hereda de dos clases que tienen el mismo método. Java no permite herencia múltiple de clases, pero sí de interfaces. Si dos interfaces tienen el mismo método `default`, la clase implementadora **debe** sobrescribirlo para resolver el conflicto.

### **6. Explica el concepto de "Type Erasure" en Generics y sus limitaciones.**
**Respuesta**: El compilador elimina la información de tipos genéricos (`<T>`) después de la compilación para mantener compatibilidad hacia atrás. En runtime, `List<String>` y `List<Integer>` son lo mismo (`List`). Limitaciones: no se puede hacer `new T()`, ni `instanceof T`.

### **7. ¿Qué son los "Switch Expressions" (Java 14+)?**
**Respuesta**: Sintaxis mejorada de `switch` que puede retornar valores y no necesita `break` (usa `yield` o flecha `->`).
```java
var result = switch(day) {
    case MONDAY, FRIDAY -> "Work";
    case SUNDAY -> "Rest";
    default -> "Normal";
};
```

### **8. ¿Qué son los Text Blocks (Java 15+)?**
**Respuesta**: Strings multilínea delimitados por `"""`. Evitan la necesidad de escapar saltos de línea y comillas, mejorando la legibilidad de JSON, SQL o HTML embebido.

### **9. ¿Qué es la inmutabilidad y cómo crear una clase verdaderamente inmutable?**
**Respuesta**:
1. Clase `final` (no heredable).
2. Todos los campos `private` y `final`.
3. No setters.
4. Si tiene campos mutables (ej. Date, List), retornar copias en los getters y copiar en el constructor (Defensive Copying).

### **10. Diferencia entre `try-with-resources` y `try-catch-finally` clásico.**
**Respuesta**: `try-with-resources` (Java 7+) cierra automáticamente recursos que implementan `AutoCloseable` al finalizar el bloque, evitando fugas de memoria y código verboso en `finally`.

### **11. ¿Qué es JIT (Just-In-Time) Compiler?**
**Respuesta**: Parte de la JVM que compila bytecode a código máquina nativo en tiempo de ejecución para mejorar el rendimiento de métodos "calientes" (hotspots), en lugar de interpretar línea por línea.

### **12. ¿Qué es el "Double Brace Initialization" y por qué es considerado un anti-patrón?**
**Respuesta**: `new ArrayList<>() {{ add("a"); }}`. Crea una clase anónima interna extra por cada uso, lo que puede causar fugas de memoria y overhead en el ClassLoader.

### **13. ¿Cómo funciona el método `hashCode()` y su relación con `equals()`?**
**Respuesta**: `hashCode()` retorna un entero que representa la ubicación en tablas hash. Contrato: Si `a.equals(b)` es true, `a.hashCode() == b.hashCode()` **debe** ser true. Si no se cumple, colecciones como `HashMap` o `HashSet` fallarán.

### **14. ¿Qué es `invokedynamic`?**
**Respuesta**: Instrucción de bytecode introducida en Java 7 para soportar lenguajes dinámicos y usada intensivamente por Lambdas en Java 8. Permite enlazar métodos en tiempo de ejecución.

### **15. ¿Qué son las referencias `SoftReference`, `WeakReference` y `PhantomReference`?**
**Respuesta**:
- **Strong**: Referencia normal.
- **Soft**: El GC solo la limpia si falta memoria. Útil para cachés sensibles a memoria.
- **Weak**: El GC la limpia en la siguiente recolección si no hay referencias fuertes. Útil para metadatos (`WeakHashMap`).
- **Phantom**: Para post-mortem cleanup, más flexible que `finalize()`.

---

## 2. Microservicios y Arquitectura (Profundización)

### **1. ¿Qué es el Teorema CAP y cómo aplica a microservicios?**
**Respuesta**: En un sistema distribuido solo puedes tener 2 de 3:
- **C**onsistency (Todos ven los mismos datos al mismo tiempo).
- **A**vailability (El sistema siempre responde).
- **P**artition Tolerance (El sistema funciona aunque falle la red).
Microservicios suelen elegir AP (disponibilidad) o CP (consistencia), sacrificando uno por la tolerancia a particiones (P es obligatoria en redes).

### **2. Explica el patrón "Database per Service" vs "Shared Database".**
**Respuesta**:
- **DB per Service**: Cada microservicio tiene su propia BD privada. Desacoplamiento total, elección de tecnología políglota, pero transacciones complejas (Saga). **Recomendado**.
- **Shared DB**: Varios servicios usan la misma BD. Fácil transacción ACID, pero fuerte acoplamiento y riesgo de romper otros servicios al cambiar esquema. **Anti-patrón**.

### **3. ¿Qué es un Service Mesh (ej. Istio, Linkerd)?**
**Respuesta**: Capa de infraestructura dedicada para manejar la comunicación entre servicios. Proporciona observabilidad, seguridad (mTLS), y gestión de tráfico (retries, circuit breakers) de forma transparente, sin ensuciar el código del servicio (sidecar pattern).

### **4. ¿Qué es gRPC y cuándo usarlo sobre REST?**
**Respuesta**: Framework RPC de Google basado en HTTP/2 y Protobuf.
- **Ventajas**: Binario (más rápido/ligero que JSON), tipado fuerte, streaming bidireccional, generación de código.
- **Uso**: Comunicación interna entre microservicios de alto rendimiento. REST para APIs públicas/externas.

### **5. ¿Qué es el patrón "BFF" (Backend for Frontend)?**
**Respuesta**: Crear un backend específico para cada interfaz de usuario (Web, Mobile, IoT). Permite optimizar las respuestas para las necesidades exactas de cada cliente, evitando over-fetching o under-fetching.

### **6. ¿Qué es "Distributed Tracing" y la diferencia entre Trace y Span?**
**Respuesta**: Rastreo de una petición a través de múltiples microservicios.
- **Trace**: El viaje completo de la petición (ID único global).
- **Span**: Una operación individual dentro del trace (ej. llamada a DB, llamada a otro servicio).

### **7. Explica el patrón "Strangler Fig" para migración de monolitos.**
**Respuesta**: Migrar gradualmente un monolito a microservicios reemplazando funcionalidades específicas con nuevos servicios y desviando el tráfico mediante un proxy/gateway, hasta que el monolito desaparece o queda reducido.

### **8. ¿Qué es "Event-Driven Architecture" (EDA)?**
**Respuesta**: Arquitectura donde los componentes se comunican emitiendo y reaccionando a eventos (cambios de estado), en lugar de llamadas directas. Desacopla productores y consumidores, mejora escalabilidad.

### **9. ¿Qué es el "Distributed Monolith" y cómo evitarlo?**
**Respuesta**: Anti-patrón donde los microservicios están tan acoplados que deben desplegarse juntos. Causas: compartir librerías de dominio, compartir base de datos, comunicación síncrona excesiva. Evitar con: DB per service, comunicación asíncrona, contratos definidos.

### **10. ¿Qué es "Contract Testing" (ej. Pact)?**
**Respuesta**: Técnica para asegurar que los microservicios (consumidor y proveedor) cumplen un contrato acordado (API schema, respuestas). Permite probar la integración sin levantar todos los servicios reales.

### **11. ¿Qué es "Service Discovery" Client-side vs Server-side?**
**Respuesta**:
- **Client-side**: El cliente consulta al registro (Eureka) y elige la instancia (Ribbon).
- **Server-side**: El cliente llama a un LB (AWS ELB, Nginx) y este consulta al registro y redirige.

### **12. ¿Qué son los "Idempotency Keys" en APIs transaccionales?**
**Respuesta**: Un header único enviado por el cliente (ej. `Idempotency-Key: uuid`) para asegurar que una operación de pago o creación no se ejecute dos veces si hay reintentos por fallos de red.

---

## 3. Testing Avanzado y Estrategias

### **1. Test Pyramid vs Testing Trophy.**
**Respuesta**:
- **Pirámide**: Base amplia de Unit Tests, menos Integration, pocos E2E. Énfasis en velocidad y aislamiento.
- **Trofeo**: Énfasis en Integration Tests (la parte más ancha), menos Unit y E2E. Argumenta que los tests de integración dan más confianza sobre el funcionamiento real ("Write tests. Not too many. Mostly integration").

### **2. ¿Qué es "Property-Based Testing"?**
**Respuesta**: En lugar de ejemplos específicos (`add(2,2) == 4`), se definen propiedades generales que deben cumplirse siempre (`add(x,y) == add(y,x)`). El framework (ej. jqwik) genera cientos de inputs aleatorios para intentar romper la propiedad.

### **3. ¿Qué es "Chaos Engineering" (ej. Chaos Monkey)?**
**Respuesta**: Introducir fallos deliberados en producción (latencia, caída de servicios, particiones de red) para verificar que el sistema es resiliente y se recupera automáticamente.

### **4. ¿Qué es "Testcontainers" y por qué es mejor que H2?**
**Respuesta**: Librería Java que levanta contenedores Docker reales (Postgres, Redis, Kafka) para tests de integración.
- **Ventaja**: Pruebas contra la tecnología real, evitando discrepancias entre H2 (memoria) y la BD de producción.

### **5. ¿Qué son los "Flaky Tests" y cómo combatirlos?**
**Respuesta**: Tests que a veces pasan y a veces fallan sin cambios en el código. Causas: dependencia de orden, tiempos/sleeps, concurrencia, datos compartidos. Solución: aislar datos, eliminar `Thread.sleep()`, usar `Awaitility`.

### **6. ¿Qué es "ArchUnit"?**
**Respuesta**: Librería para testear la arquitectura del código Java. Permite definir reglas como "Las clases de `Service` no deben acceder a `Controller`" o "Las clases de `Domain` no deben depender de `Infrastructure`".

### **7. ¿Qué es "Mutation Testing" (ej. PIT)?**
**Respuesta**: Evalúa la calidad de tus tests modificando tu código (mutantes) y ejecutando los tests. Si los tests siguen pasando (mutante sobrevive), tus tests no son lo suficientemente buenos.

### **8. Diferencia entre Mocking y Stubbing.**
**Respuesta**:
- **Stub**: Provee respuestas predefinidas a llamadas durante el test (estado).
- **Mock**: Verifica que se realizaron ciertas llamadas (comportamiento/interacción).

### **9. ¿Qué es "Consumer-Driven Contracts" (CDC)?**
**Respuesta**: Los consumidores de una API definen sus expectativas (contrato). El proveedor implementa y valida estos contratos en su CI, asegurando que no rompe a ningún consumidor.

### **10. Estrategias para testear microservicios.**
**Respuesta**:
1. **Unit**: Lógica de dominio.
2. **Component**: Servicio aislado con dependencias mockeadas (o Testcontainers).
3. **Contract**: Integración entre servicios.
4. **E2E**: Flujos críticos completos (pocos).

---

## 4. DevOps, SRE y Cloud Native

### **1. ¿Qué es GitOps (ej. ArgoCD, Flux)?**
**Respuesta**: Usar un repositorio Git como única fuente de verdad para la infraestructura y despliegue. Un agente en el clúster sincroniza el estado real con el definido en Git automáticamente.

### **2. Explica los "Three Pillars of Observability".**
**Respuesta**:
1. **Logs**: Eventos discretos (¿Qué pasó?).
2. **Metrics**: Datos agregados numéricos (¿Qué tendencia hay? CPU, RAM, RPS).
3. **Traces**: Contexto de una petición a través de servicios (¿Dónde pasó?).

### **3. ¿Qué son SLI, SLO y SLA?**
**Respuesta**:
- **SLI (Indicator)**: Métrica real (ej. latencia actual 200ms).
- **SLO (Objective)**: Meta interna (ej. 99% de requests < 300ms).
- **SLA (Agreement)**: Contrato legal con cliente (ej. si disponibilidad < 99.9%, hay penalización).

### **4. ¿Qué es "Infrastructure as Code" (IaC) y herramientas?**
**Respuesta**: Gestionar infraestructura mediante archivos de definición en lugar de configuración manual.
- **Terraform**: Declarativo, multi-cloud, estado gestionado.
- **Ansible**: Procedural/Declarativo, gestión de configuración, sin agente.
- **Pulumi**: IaC usando lenguajes de programación reales (Java, TS, Python).

### **5. Estrategias de Deployment: Blue/Green vs Canary.**
**Respuesta**:
- **Blue/Green**: Dos entornos idénticos. Router cambia 100% del tráfico del viejo (Blue) al nuevo (Green). Rollback instantáneo. Requiere doble recurso.
- **Canary**: Despliegue gradual. 1% tráfico a nueva versión, luego 10%, 50%, 100%. Menor riesgo, feedback temprano.

### **6. ¿Qué es un "Distroless" image en Docker?**
**Respuesta**: Imágenes de contenedor mínimas que solo contienen la aplicación y sus dependencias de runtime, sin shell, gestores de paquetes ni herramientas de sistema. Reduce superficie de ataque y tamaño.

### **7. ¿Qué es "Horizontal Pod Autoscaling" (HPA) en K8s?**
**Respuesta**: Escala automáticamente el número de pods basándose en métricas de CPU, memoria o custom metrics (ej. mensajes en cola).

### **8. ¿Qué es "Sidecar Pattern" en Kubernetes?**
**Respuesta**: Contenedor auxiliar que corre en el mismo Pod que el contenedor principal para extender su funcionalidad (ej. proxy de logging, service mesh proxy, gestión de secretos).

### **9. ¿Qué es "Immutable Infrastructure"?**
**Respuesta**: Una vez desplegado un servidor/contenedor, nunca se modifica. Si hay cambios, se reemplaza por uno nuevo. Evita "configuration drift".

### **10. ¿Qué es "Shift Left" en DevOps?**
**Respuesta**: Mover pruebas, seguridad y validaciones a etapas más tempranas del ciclo de desarrollo (antes del commit o merge), en lugar de esperar al final.

---

## 5. Seguridad Avanzada (AppSec)

### **1. ¿Qué es OWASP Top 10? Menciona 3 críticos.**
**Respuesta**: Lista de las vulnerabilidades web más críticas.
1. **Broken Access Control**: Usuario accede a datos/funciones no permitidas.
2. **Cryptographic Failures**: Datos sensibles no cifrados o cifrados débilmente.
3. **Injection**: SQL, NoSQL, Command injection.

### **2. ¿Qué es "Zero Trust Architecture"?**
**Respuesta**: Modelo de seguridad que asume que no hay perímetro seguro. "Nunca confiar, siempre verificar". Cada petición, interna o externa, debe ser autenticada, autorizada y cifrada.

### **3. Diferencia entre OAuth2 y OpenID Connect (OIDC).**
**Respuesta**:
- **OAuth2**: Protocolo de **Autorización** (delegar acceso).
- **OIDC**: Capa de identidad sobre OAuth2 para **Autenticación** (saber quién es el usuario, ID Token).

### **4. ¿Qué es "Secret Management" (ej. HashiCorp Vault)?**
**Respuesta**: Práctica y herramientas para gestionar credenciales (passwords, API keys, certificados) de forma segura, centralizada, con rotación automática y auditoría, evitando hardcoding en código o config.

### **5. ¿Qué es SAST vs DAST?**
**Respuesta**:
- **SAST (Static Application Security Testing)**: Analiza código fuente en reposo (White-box).
- **DAST (Dynamic Application Security Testing)**: Ataca la aplicación en ejecución desde fuera (Black-box).

### **6. ¿Qué es "Dependency Scanning" (SCA)?**
**Respuesta**: Software Composition Analysis. Herramientas (Snyk, OWASP Dependency Check) que analizan librerías de terceros en busca de vulnerabilidades conocidas (CVEs).

### **7. ¿Qué es "Penetration Testing" (Pentesting)?**
**Respuesta**: Ciberataque simulado autorizado para evaluar la seguridad del sistema.

### **8. ¿Qué es un ataque "Man-in-the-Middle" (MitM) y cómo prevenirlo?**
**Respuesta**: Atacante intercepta comunicación entre dos partes. Prevención: Usar HTTPS/TLS con certificados válidos, HSTS, y pinning de certificados en móviles.

### **9. ¿Qué es "Salt" en hashing de contraseñas?**
**Respuesta**: Dato aleatorio añadido a la contraseña antes de hashear. Previene ataques de Rainbow Tables (tablas precalculadas) asegurando que dos contraseñas iguales tengan hashes diferentes.

### **10. ¿Qué es "Security Misconfiguration"?**
**Respuesta**: Configuraciones por defecto inseguras, mensajes de error detallados, puertos abiertos innecesarios, headers de seguridad faltantes.

### **11. ¿Qué es "Server-Side Request Forgery" (SSRF)?**
**Respuesta**: Atacante induce al servidor a hacer peticiones HTTP a dominios arbitrarios (ej. red interna, metadatos de cloud). Prevención: Validar y sanear URLs de entrada, listas blancas de dominios.

### **12. ¿Qué es CORS (Cross-Origin Resource Sharing) y sus riesgos?**
**Respuesta**: Mecanismo que permite a un navegador acceder a recursos de otro origen. Riesgo: Si se configura `Access-Control-Allow-Origin: *` con credenciales, permite ataques. Configurar solo orígenes de confianza.
