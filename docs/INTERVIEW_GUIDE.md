# Guía de Entrevista Técnica - Backend Java

> **Nota**: Este documento contiene 200+ preguntas con respuestas concisas para preparación de entrevistas técnicas.

---

## 📚 Índice

1. [Java Core (50 preguntas)](#1-java-core)
2. [Concurrencia y Multihilo (30 preguntas)](#2-concurrencia-y-multihilo)
3. [Patrones de Diseño (20 preguntas)](#3-patrones-de-diseño)
4. [Spring y Spring Boot (30 preguntas)](#4-spring-y-spring-boot)
5. [Microservicios y Arquitectura Distribuida (40 preguntas)](#5-microservicios-y-arquitectura-distribuida)
6. [Testing y Calidad de Código (30 preguntas)](#6-testing-y-calidad-de-código)
7. [DevOps y Cloud (30 preguntas)](#7-devops-y-cloud)
8. [Seguridad y Buenas Prácticas (20 preguntas)](#8-seguridad-y-buenas-prácticas)

---

## 1. Java Core

### **1. Diferencia entre clase abstracta e interfaz**

**Respuesta**: 
- **Clase abstracta**: Puede tener métodos concretos y abstractos, constructores, variables de instancia. Herencia simple.
- **Interfaz**: Solo métodos abstractos (antes Java 8), puede tener default/static methods. Herencia múltiple.
- **Cuándo usar**: Clase abstracta para relación "es-un" con código compartido; interfaz para contratos/capacidades.

### **2. Qué es Optional y cuándo usarlo**

**Respuesta**: Contenedor que puede o no contener un valor no-nulo. Evita `NullPointerException`.
```java
Optional<String> opt = Optional.ofNullable(value);
String result = opt.orElse("default");
```
**Cuándo**: Retornos de métodos que pueden no tener valor, nunca como parámetro.

### **3. Diferencia entre == y .equals()**

**Respuesta**:
- `==`: Compara referencias (misma dirección de memoria)
- `.equals()`: Compara contenido (debe sobrescribirse)
```java
String a = new String("test");
String b = new String("test");
a == b        // false (diferentes objetos)
a.equals(b)   // true (mismo contenido)
```

### **4. Qué es un Singleton y cómo hacerlo thread-safe**

**Respuesta**: Patrón que garantiza una única instancia.
```java
// Thread-safe con enum (mejor opción)
public enum Singleton {
    INSTANCE;
}

// O con double-checked locking
public class Singleton {
    private static volatile Singleton instance;
    private Singleton() {}
    
    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
```

### **5. Qué es un constructor estático**

**Respuesta**: No existe. Existe **bloque estático** que se ejecuta una vez al cargar la clase.
```java
public class Example {
    static {
        // Inicialización de variables estáticas
    }
}
```

### **6. Qué son las excepciones checked y unchecked**

**Respuesta**:
- **Checked**: Deben manejarse o declararse (`IOException`, `SQLException`). Compilador las verifica.
- **Unchecked**: Heredan de `RuntimeException` (`NullPointerException`, `IllegalArgumentException`). No obligatorias de manejar.

### **7. Cómo funciona el garbage collector**

**Respuesta**: Libera memoria de objetos sin referencias. Algoritmos: Mark-Sweep, Generational (Young/Old Gen), G1GC, ZGC. No se puede forzar, solo sugerir con `System.gc()`.

### **8. Diferencia entre stack y heap**

**Respuesta**:
- **Stack**: Variables locales, referencias, llamadas a métodos. LIFO, rápido, tamaño limitado.
- **Heap**: Objetos, arrays. Más lento, mayor tamaño, gestionado por GC.

### **9. Qué es final, finally y finalize**

**Respuesta**:
- `final`: Variable constante, método no sobrescribible, clase no heredable
- `finally`: Bloque que siempre se ejecuta después de try-catch
- `finalize()`: Método deprecated llamado antes de GC (no usar)

### **10. Diferencia entre String, StringBuilder y StringBuffer**

**Respuesta**:
- **String**: Inmutable, thread-safe
- **StringBuilder**: Mutable, no thread-safe, más rápido
- **StringBuffer**: Mutable, thread-safe (synchronized), más lento

### **11. Diferencia entre sobrecarga (overload) y sobrescritura (override)**

**Respuesta**:
- **Overload**: Mismo nombre, diferentes parámetros, misma clase
- **Override**: Mismo nombre y parámetros, clase hija redefine método padre

### **12. Qué es polimorfismo y tipos**

**Respuesta**: Capacidad de un objeto de tomar múltiples formas.
- **Compilación (estático)**: Overloading
- **Ejecución (dinámico)**: Overriding
```java
Animal a = new Dog(); // Polimorfismo
a.sound(); // Llama al método de Dog
```

### **13. Qué es encapsulamiento y su importancia**

**Respuesta**: Ocultar datos internos, exponer solo lo necesario mediante getters/setters. Beneficios: seguridad, mantenibilidad, control de acceso.

### **14. Qué es herencia y cómo se aplica**

**Respuesta**: Clase hija hereda atributos/métodos de clase padre. Usa `extends`.
```java
public class Dog extends Animal {
    // Hereda de Animal
}
```

### **15. Qué es el operador instanceof y cómo usarlo**

**Respuesta**: Verifica si un objeto es instancia de una clase.
```java
if (obj instanceof String) {
    String str = (String) obj;
}
```

### **16. Qué es la palabra clave super y this**

**Respuesta**:
- `this`: Referencia al objeto actual
- `super`: Referencia a la clase padre
```java
super.method(); // Llama método del padre
this.field = value; // Campo del objeto actual
```

### **17. Diferencia entre static y instance members**

**Respuesta**:
- **Static**: Pertenece a la clase, compartido por todas las instancias
- **Instance**: Pertenece al objeto, cada instancia tiene su copia

### **18. Qué son bloques estáticos y de instancia**

**Respuesta**:
- **Estático**: Se ejecuta al cargar la clase, una sola vez
- **Instancia**: Se ejecuta antes del constructor, cada vez que se crea un objeto

### **19. Qué es varargs y cómo se usa**

**Respuesta**: Número variable de argumentos.
```java
public void print(String... args) {
    for (String s : args) {
        System.out.println(s);
    }
}
print("a", "b", "c");
```

### **20. Diferencia entre enum y constantes estáticas**

**Respuesta**:
- **Enum**: Type-safe, puede tener métodos, singleton garantizado
- **Constantes**: Solo valores, no type-safe
```java
public enum Status { ACTIVE, INACTIVE }
```

### **21. Qué son las expresiones lambda y ventajas**

**Respuesta**: Funciones anónimas, sintaxis concisa para interfaces funcionales.
```java
list.forEach(item -> System.out.println(item));
```
**Ventajas**: Código más limpio, programación funcional, paralelización fácil.

### **22. Diferencia entre Stream y Collection**

**Respuesta**:
- **Collection**: Estructura de datos, almacena elementos
- **Stream**: Secuencia de elementos para procesamiento, no almacena, lazy evaluation

### **23. Qué es el API Stream y operaciones intermedias/finales**

**Respuesta**:
- **Intermedias**: `filter()`, `map()`, `sorted()` - retornan Stream
- **Finales**: `collect()`, `forEach()`, `reduce()` - retornan resultado
```java
list.stream()
    .filter(x -> x > 5)  // Intermedia
    .collect(Collectors.toList()); // Final
```

### **24. Diferencia entre map() y flatMap()**

**Respuesta**:
- `map()`: Transforma cada elemento 1:1
- `flatMap()`: Transforma y aplana (1:N), útil para listas anidadas
```java
list.stream().map(s -> s.toUpperCase())
list.stream().flatMap(list -> list.stream())
```

### **25. Qué son las referencias a métodos**

**Respuesta**: Sintaxis abreviada para lambdas que llaman a un método.
```java
list.forEach(System.out::println);
// Equivalente a: list.forEach(x -> System.out.println(x));
```

### **26. Qué es Optional.isPresent() y orElse()**

**Respuesta**:
- `isPresent()`: Verifica si hay valor
- `orElse()`: Retorna valor o default si vacío
```java
opt.orElse("default");
opt.orElseGet(() -> computeDefault());
```

### **27. Cómo funcionan los predicados en Java**

**Respuesta**: Interfaz funcional que evalúa condición.
```java
Predicate<Integer> isEven = n -> n % 2 == 0;
list.stream().filter(isEven);
```

### **28. Diferencia entre HashMap y TreeMap**

**Respuesta**:
- **HashMap**: O(1), no ordenado, permite null
- **TreeMap**: O(log n), ordenado por clave, no permite null en clave

### **29. Diferencia entre HashSet y TreeSet**

**Respuesta**:
- **HashSet**: O(1), no ordenado, permite null
- **TreeSet**: O(log n), ordenado, no permite null

### **30. Diferencia entre LinkedList y ArrayList**

**Respuesta**:
- **ArrayList**: Array dinámico, acceso O(1), inserción/eliminación O(n)
- **LinkedList**: Lista enlazada, acceso O(n), inserción/eliminación O(1)

### **31. Qué es Comparable y Comparator**

**Respuesta**:
- **Comparable**: Orden natural, implementa `compareTo()` en la clase
- **Comparator**: Orden personalizado, clase separada
```java
class Person implements Comparable<Person> {
    public int compareTo(Person p) { return this.age - p.age; }
}
Comparator<Person> byName = (p1, p2) -> p1.name.compareTo(p2.name);
```

### **32. Qué es Iterator y ListIterator**

**Respuesta**:
- **Iterator**: Recorre colección, solo hacia adelante, puede eliminar
- **ListIterator**: Solo para listas, bidireccional, puede modificar

### **33. Qué son WeakHashMap y IdentityHashMap**

**Respuesta**:
- **WeakHashMap**: Claves con referencias débiles, GC puede eliminar
- **IdentityHashMap**: Compara claves con `==` en lugar de `equals()`

### **34. Qué son colecciones synchronized y concurrentes**

**Respuesta**:
- **Synchronized**: `Collections.synchronizedList()`, lock en toda la colección
- **Concurrent**: `ConcurrentHashMap`, lock por segmentos, mejor rendimiento

### **35. Diferencia entre copy-on-write y synchronized collections**

**Respuesta**:
- **Copy-on-write**: Copia en cada modificación, ideal para muchas lecturas
- **Synchronized**: Lock en cada operación, mejor para escrituras frecuentes

### **36. Qué es Serializable y cómo se usa**

**Respuesta**: Interfaz que permite convertir objeto a bytes.
```java
class Person implements Serializable {
    private static final long serialVersionUID = 1L;
}
```

### **37. Qué es transient y su uso**

**Respuesta**: Marca campos que no deben serializarse.
```java
private transient String password; // No se serializa
```

### **38. Diferencia entre shallow copy y deep copy**

**Respuesta**:
- **Shallow**: Copia referencias, objetos internos compartidos
- **Deep**: Copia completa, objetos internos también copiados

### **39. Qué es clone() y cómo implementarlo correctamente**

**Respuesta**: Crea copia del objeto. Implementar `Cloneable` y sobrescribir `clone()`.
```java
@Override
public Object clone() throws CloneNotSupportedException {
    return super.clone(); // Shallow copy
}
```

### **40. Qué es reflection y usos**

**Respuesta**: Inspeccionar/modificar clases en runtime. Usos: frameworks, testing, serialización.
```java
Class<?> clazz = Class.forName("com.example.MyClass");
Method method = clazz.getMethod("myMethod");
```

### **41. Qué son annotations y ejemplos comunes**

**Respuesta**: Metadatos para clases/métodos. Ejemplos: `@Override`, `@Deprecated`, `@Entity`, `@Autowired`.

### **42. Diferencia entre @Override y @Deprecated**

**Respuesta**:
- `@Override`: Indica que sobrescribe método padre
- `@Deprecated`: Marca como obsoleto, no usar

### **43. Qué son generics y beneficios**

**Respuesta**: Tipos parametrizados, type-safety en compilación.
```java
List<String> list = new ArrayList<>();
```
**Beneficios**: Type-safety, no casting, reutilización.

### **44. Qué es type erasure**

**Respuesta**: Generics se eliminan en runtime, solo existen en compilación. `List<String>` se convierte en `List`.

### **45. Qué es wildcards en generics (? extends / ? super)**

**Respuesta**:
- `? extends T`: Upper bound, lee T o subclases
- `? super T`: Lower bound, escribe T o superclases
```java
List<? extends Number> list1; // Lee Number o subclases
List<? super Integer> list2;  // Escribe Integer o superclases
```

### **46. Qué es var y cuándo usarlo**

**Respuesta**: Inferencia de tipos (Java 10+). Solo variables locales.
```java
var list = new ArrayList<String>(); // Infiere tipo
```

### **47. Diferencia entre interface funcional y abstract class**

**Respuesta**:
- **Interface funcional**: Un solo método abstracto, para lambdas
- **Abstract class**: Múltiples métodos, constructores, estado

### **48. Qué es record en Java 16+**

**Respuesta**: Clase inmutable para datos, genera automáticamente constructor, getters, `equals()`, `hashCode()`.
```java
public record Person(String name, int age) {}
```

### **49. Qué son sealed classes en Java 17+**

**Respuesta**: Controla qué clases pueden heredar.
```java
public sealed class Shape permits Circle, Square {}
```

### **50. Qué es pattern matching para instanceof**

**Respuesta**: Simplifica casting después de instanceof.
```java
if (obj instanceof String s) {
    System.out.println(s.length()); // s ya es String
}
```

---

## 2. Concurrencia y Multihilo

### **1. Diferencia entre Thread y Runnable**

**Respuesta**:
- **Thread**: Clase, herencia simple limitada
- **Runnable**: Interfaz, más flexible, mejor práctica
```java
new Thread(() -> System.out.println("Hello")).start();
```

### **2. Qué es ExecutorService y ventajas**

**Respuesta**: Pool de hilos reutilizables. Ventajas: gestión automática, mejor rendimiento.
```java
ExecutorService executor = Executors.newFixedThreadPool(10);
executor.submit(() -> task());
```

### **3. Diferencia entre Future y CompletableFuture**

**Respuesta**:
- **Future**: Resultado asíncrono, bloqueante con `get()`
- **CompletableFuture**: No bloqueante, composable, callbacks
```java
CompletableFuture.supplyAsync(() -> compute())
    .thenApply(result -> process(result));
```

### **4. Qué es synchronized y ReentrantLock**

**Respuesta**:
- **synchronized**: Palabra clave, lock implícito
- **ReentrantLock**: Clase, lock explícito, más control (tryLock, timeout)

### **5. Qué es un race condition**

**Respuesta**: Múltiples hilos acceden/modifican dato compartido simultáneamente, resultado impredecible.

### **6. Qué es un deadlock**

**Respuesta**: Dos o más hilos esperan recursos que otros tienen, bloqueados permanentemente.

### **7. Cómo prevenir deadlocks**

**Respuesta**:
- Orden consistente de locks
- Timeout en locks
- Evitar locks anidados
- Usar `tryLock()`

### **8. Qué significa volatile**

**Respuesta**: Variable siempre leída/escrita desde memoria principal, no cache. Garantiza visibilidad, no atomicidad.

### **9. Qué es wait(), notify() y notifyAll()**

**Respuesta**: Comunicación entre hilos.
- `wait()`: Libera lock y espera
- `notify()`: Despierta un hilo
- `notifyAll()`: Despierta todos los hilos

### **10. Diferencia entre notify() y notifyAll()**

**Respuesta**:
- `notify()`: Despierta un hilo aleatorio
- `notifyAll()`: Despierta todos, más seguro

### **11. Qué es thread-safe**

**Respuesta**: Código que funciona correctamente con múltiples hilos concurrentes.

### **12. Qué es un CountDownLatch**

**Respuesta**: Sincronización que espera N eventos antes de continuar.
```java
CountDownLatch latch = new CountDownLatch(3);
latch.countDown(); // Decrementa
latch.await(); // Espera a 0
```

### **13. Qué es CyclicBarrier**

**Respuesta**: Sincronización donde N hilos esperan en un punto, reutilizable.

### **14. Qué es Semaphore**

**Respuesta**: Controla acceso a recurso limitado con N permisos.
```java
Semaphore sem = new Semaphore(5); // 5 permisos
sem.acquire();
sem.release();
```

### **15. Qué es Lock y ReadWriteLock**

**Respuesta**:
- **Lock**: Interfaz para locks explícitos
- **ReadWriteLock**: Múltiples lectores, un escritor

### **16. Qué son ConcurrentHashMap y CopyOnWriteArrayList**

**Respuesta**:
- **ConcurrentHashMap**: HashMap thread-safe, lock por segmentos
- **CopyOnWriteArrayList**: Lista thread-safe, copia en escritura

### **17. Qué es fork/join framework**

**Respuesta**: Divide tareas grandes en subtareas, las ejecuta en paralelo y combina resultados.

### **18. Diferencia entre parallelStream() y stream()**

**Respuesta**:
- `stream()`: Secuencial
- `parallelStream()`: Paralelo, usa ForkJoinPool

### **19. Qué es AtomicInteger y clases atómicas**

**Respuesta**: Operaciones atómicas sin locks.
```java
AtomicInteger count = new AtomicInteger(0);
count.incrementAndGet(); // Thread-safe
```

### **20. Diferencia entre spinlock y lock blocking**

**Respuesta**:
- **Spinlock**: Espera activa (busy-wait), consume CPU
- **Blocking**: Hilo duerme, no consume CPU

### **21. Qué es la sincronización fina (fine-grained locking)**

**Respuesta**: Locks en partes pequeñas del código, mejor concurrencia.

### **22. Qué es la sincronización gruesa (coarse-grained)**

**Respuesta**: Lock en secciones grandes, más simple pero menos concurrencia.

### **23. Qué es un thread pool**

**Respuesta**: Conjunto de hilos reutilizables para ejecutar tareas.

### **24. Qué es daemon thread**

**Respuesta**: Hilo en background, JVM termina sin esperarlo.
```java
thread.setDaemon(true);
```

### **25. Qué es ThreadLocal y usos**

**Respuesta**: Variable local por hilo, no compartida.
```java
ThreadLocal<Integer> threadLocal = new ThreadLocal<>();
```

### **26. Cómo se maneja interrupción de hilos**

**Respuesta**: `Thread.interrupt()` y verificar `Thread.interrupted()`.
```java
if (Thread.interrupted()) {
    // Manejar interrupción
}
```

### **27. Qué es fair lock**

**Respuesta**: Lock que garantiza orden FIFO de adquisición.

### **28. Qué es busy-waiting y cómo evitarlo**

**Respuesta**: Espera activa consumiendo CPU. Evitar con `wait()`, `Condition`, `LockSupport`.

### **29. Diferencia entre wait-notify y Condition**

**Respuesta**:
- **wait-notify**: Mecanismo básico
- **Condition**: Más flexible, múltiples condiciones por lock

### **30. Qué son los hazards en concurrencia (ABA problem)**

**Respuesta**: Valor cambia de A→B→A, parece no modificado. Solución: versionado, `AtomicStampedReference`.

---

## 3. Patrones de Diseño

### **1. Qué es Singleton y variantes thread-safe**

**Respuesta**: Una sola instancia. Variantes:
- Enum (mejor)
- Double-checked locking
- Static inner class

### **2. Qué es Factory y Factory Method**

**Respuesta**:
- **Factory**: Crea objetos sin exponer lógica
- **Factory Method**: Método en clase abstracta, subclases deciden implementación

### **3. Qué es Abstract Factory**

**Respuesta**: Crea familias de objetos relacionados sin especificar clases concretas.

### **4. Qué es Builder**

**Respuesta**: Construye objetos complejos paso a paso.
```java
Person.builder()
    .name("John")
    .age(30)
    .build();
```

### **5. Qué es Prototype**

**Respuesta**: Crea objetos clonando instancia existente.

### **6. Qué es Observer**

**Respuesta**: Notifica cambios a múltiples observadores.

### **7. Qué es Strategy**

**Respuesta**: Encapsula algoritmos intercambiables.

### **8. Qué es Command**

**Respuesta**: Encapsula petición como objeto, permite deshacer/rehacer.

### **9. Qué es DAO y Repository**

**Respuesta**:
- **DAO**: Abstrae acceso a datos
- **Repository**: Colección de objetos de dominio, más orientado a dominio

### **10. Qué es Dependency Injection**

**Respuesta**: Inyecta dependencias en lugar de crearlas internamente. Inversión de control.

### **11. Qué es Adapter**

**Respuesta**: Convierte interfaz de clase a otra esperada.

### **12. Qué es Decorator**

**Respuesta**: Añade funcionalidad a objeto dinámicamente.

### **13. Qué es Facade**

**Respuesta**: Interfaz simplificada para sistema complejo.

### **14. Qué es Composite**

**Respuesta**: Trata objetos individuales y composiciones uniformemente (árbol).

### **15. Qué es Proxy**

**Respuesta**: Intermediario que controla acceso a objeto.

### **16. Qué es Mediator**

**Respuesta**: Centraliza comunicación entre objetos.

### **17. Qué es Template Method**

**Respuesta**: Define esqueleto de algoritmo, subclases implementan pasos.

### **18. Qué es State**

**Respuesta**: Cambia comportamiento según estado interno.

### **19. Qué es Chain of Responsibility**

**Respuesta**: Cadena de manejadores, cada uno decide si procesa petición.

### **20. Qué es Circuit Breaker en microservicios**

**Respuesta**: Previene llamadas a servicio fallido, estados: Closed, Open, Half-Open.

---

## 4. Spring y Spring Boot

### **1. Qué es IoC y contenedor de Spring**

**Respuesta**: Inversión de Control. Contenedor gestiona creación y ciclo de vida de beans.

### **2. Diferencia entre @Component, @Service y @Repository**

**Respuesta**:
- `@Component`: Genérico
- `@Service`: Lógica de negocio
- `@Repository`: Acceso a datos, traduce excepciones

### **3. Qué es @Autowired**

**Respuesta**: Inyección automática de dependencias.

### **4. Tipos de inyección de dependencias**

**Respuesta**:
- Constructor (recomendado)
- Setter
- Field

### **5. Qué es @Transactional y ejemplos**

**Respuesta**: Gestión declarativa de transacciones.
```java
@Transactional
public void saveUser(User user) {
    userRepository.save(user);
}
```

### **6. Qué es @RestController vs @Controller**

**Respuesta**:
- `@RestController`: `@Controller` + `@ResponseBody`, retorna JSON
- `@Controller`: Retorna vistas

### **7. Diferencia entre @RequestMapping, @GetMapping, @PostMapping**

**Respuesta**:
- `@RequestMapping`: Genérico, cualquier método HTTP
- `@GetMapping`: Solo GET
- `@PostMapping`: Solo POST

### **8. Qué son los scopes de beans**

**Respuesta**:
- **Singleton**: Una instancia (default)
- **Prototype**: Nueva instancia cada vez
- **Request**: Una por request HTTP
- **Session**: Una por sesión HTTP

### **9. Qué es @Qualifier y cuándo usarlo**

**Respuesta**: Desambigua cuando hay múltiples beans del mismo tipo.
```java
@Autowired
@Qualifier("specificBean")
private MyService service;
```

### **10. Qué es ApplicationContext vs BeanFactory**

**Respuesta**:
- **BeanFactory**: Contenedor básico, lazy loading
- **ApplicationContext**: Extiende BeanFactory, eager loading, más funcionalidades

### **11. Qué es @Value y cómo inyectar propiedades**

**Respuesta**: Inyecta valores de properties.
```java
@Value("${app.name}")
private String appName;
```

### **12. Qué es @Configuration y @Bean**

**Respuesta**:
- `@Configuration`: Clase de configuración
- `@Bean`: Define bean manualmente

### **13. Qué es @ControllerAdvice y @ExceptionHandler**

**Respuesta**:
- `@ControllerAdvice`: Manejo global de excepciones
- `@ExceptionHandler`: Maneja excepción específica

### **14. Cómo se manejan excepciones globales en Spring**

**Respuesta**: Con `@ControllerAdvice` y `@ExceptionHandler`.
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handle(Exception e) {
        return ResponseEntity.status(500).body(e.getMessage());
    }
}
```

### **15. Qué es Spring Boot y ventajas**

**Respuesta**: Framework sobre Spring. Ventajas: autoconfiguración, embedded server, starters, producción-ready.

### **16. Qué es autoconfiguración**

**Respuesta**: Configura automáticamente beans según dependencias en classpath.

### **17. Qué es starter dependency**

**Respuesta**: Dependencias agrupadas (`spring-boot-starter-web`, `spring-boot-starter-data-jpa`).

### **18. Qué es actuator y monitorización**

**Respuesta**: Endpoints para monitoreo (`/health`, `/metrics`, `/info`).

### **19. Qué es profile en Spring**

**Respuesta**: Configuraciones por ambiente (dev, prod).
```yaml
spring.profiles.active=dev
```

### **20. Qué es Spring Data JPA y diferencias con Hibernate**

**Respuesta**:
- **Hibernate**: ORM implementation
- **Spring Data JPA**: Abstracción sobre JPA, reduce boilerplate

### **21. Qué es @Entity, @Table, @Id**

**Respuesta**:
- `@Entity`: Marca clase como entidad JPA
- `@Table`: Especifica tabla
- `@Id`: Marca primary key

### **22. Qué es @OneToMany y @ManyToOne**

**Respuesta**: Relaciones entre entidades.
```java
@OneToMany(mappedBy = "user")
private List<Phone> phones;

@ManyToOne
@JoinColumn(name = "user_id")
private User user;
```

### **23. Diferencia entre LAZY y EAGER**

**Respuesta**:
- **LAZY**: Carga bajo demanda
- **EAGER**: Carga inmediata

### **24. Qué es Pageable y Sort en Spring Data**

**Respuesta**: Paginación y ordenamiento.
```java
Page<User> users = userRepository.findAll(PageRequest.of(0, 10));
```

### **25. Qué es RestTemplate y WebClient**

**Respuesta**:
- **RestTemplate**: Cliente HTTP síncrono (deprecated)
- **WebClient**: Cliente HTTP asíncrono, reactivo

### **26. Qué es HATEOAS**

**Respuesta**: Hypermedia As The Engine Of Application State. APIs con links navegables.

### **27. Cómo versionar APIs en Spring**

**Respuesta**:
- URL: `/api/v1/users`
- Header: `Accept: application/vnd.api.v1+json`
- Query param: `/api/users?version=1`

### **28. Qué es CORS y cómo configurarlo**

**Respuesta**: Cross-Origin Resource Sharing. Permite peticiones desde otros dominios.
```java
@CrossOrigin(origins = "http://localhost:3000")
```

### **29. Qué es Spring Security básico**

**Respuesta**: Framework de seguridad. Autenticación, autorización, protección CSRF.

### **30. Qué es JWT y cómo se integra en Spring**

**Respuesta**: JSON Web Token. Filtro que valida token en cada request.

---

## 5. Microservicios y Arquitectura Distribuida

### **1. Qué es un microservicio**

**Respuesta**: Servicio pequeño, independiente, desplegable por separado, comunicación por API.

### **2. Diferencia entre monolito y microservicio**

**Respuesta**:
- **Monolito**: Una aplicación, un despliegue
- **Microservicio**: Múltiples servicios, despliegues independientes

### **3. Qué es REST y principios básicos**

**Respuesta**: Representational State Transfer. Principios: stateless, URIs como recursos, verbos HTTP.

### **4. Diferencia entre PUT y POST**

**Respuesta**:
- **POST**: Crea recurso, no idempotente
- **PUT**: Actualiza/crea, idempotente

### **5. Qué es idempotencia y ejemplos en APIs**

**Respuesta**: Múltiples llamadas = mismo resultado. GET, PUT, DELETE son idempotentes. POST no.

### **6. Qué es versionado de APIs y estrategias**

**Respuesta**: Ver pregunta 27 de Spring.

### **7. Qué es caching y cómo usar Redis**

**Respuesta**: Almacenamiento temporal de datos. Redis: cache distribuido, key-value store.

### **8. Qué es SAGA y cómo funciona en transacciones distribuidas**

**Respuesta**: Secuencia de transacciones locales con compensación. Tipos: Choreography, Orchestration.

### **9. Qué es consistencia eventual**

**Respuesta**: Sistema alcanza consistencia después de un tiempo, no inmediatamente.

### **10. Diferencia entre transacción ACID y BASE**

**Respuesta**:
- **ACID**: Atomicity, Consistency, Isolation, Durability (SQL)
- **BASE**: Basically Available, Soft state, Eventually consistent (NoSQL)

### **11. Qué es un API Gateway**

**Respuesta**: Punto de entrada único, routing, autenticación, rate limiting.

### **12. Qué es service discovery**

**Respuesta**: Registro y descubrimiento dinámico de servicios (Eureka, Consul).

### **13. Qué es load balancing en microservicios**

**Respuesta**: Distribuye tráfico entre instancias (Ribbon, Spring Cloud LoadBalancer).

### **14. Qué es circuit breaker y cómo mejora resiliencia**

**Respuesta**: Previene cascada de fallos, abre circuito si servicio falla.

### **15. Qué es retry con backoff exponencial**

**Respuesta**: Reintenta con espera creciente (1s, 2s, 4s, 8s...).

### **16. Qué es throttling y rate limiting**

**Respuesta**: Limita número de requests por tiempo.

### **17. Qué es publish/subscribe y cómo funciona en Kafka**

**Respuesta**: Productores publican a topics, consumidores suscriben.

### **18. Qué es messaging asincrónico y ventajas**

**Respuesta**: Comunicación no bloqueante. Ventajas: desacoplamiento, escalabilidad.

### **19. Diferencia entre topic y queue en Kafka**

**Respuesta**:
- **Topic**: Múltiples consumidores (pub/sub)
- **Queue**: Un consumidor (point-to-point)

### **20. Qué es consumer group en Kafka**

**Respuesta**: Grupo de consumidores que procesan particiones en paralelo.

### **21. Qué es partitioning en Kafka y su importancia**

**Respuesta**: Divide topic en particiones para paralelismo y escalabilidad.

### **22. Qué es offset en Kafka**

**Respuesta**: Posición de mensaje en partición, permite reprocessar.

### **23. Qué es idempotencia en procesamiento de mensajes**

**Respuesta**: Procesar mismo mensaje múltiples veces = mismo resultado.

### **24. Qué es stream processing y ejemplos**

**Respuesta**: Procesamiento en tiempo real de flujos de datos (Kafka Streams, Flink).

### **25. Qué es dead letter queue y para qué se usa**

**Respuesta**: Cola para mensajes que fallaron procesamiento, permite análisis.

### **26. Qué es event sourcing**

**Respuesta**: Almacena eventos en lugar de estado actual, reconstruye estado desde eventos.

### **27. Qué es CQRS**

**Respuesta**: Command Query Responsibility Segregation. Separa lecturas y escrituras.

### **28. Qué es throttling en APIs**

**Respuesta**: Ver pregunta 16.

### **29. Qué es rate limiting y cómo aplicarlo**

**Respuesta**: Limita requests por IP/usuario. Implementación: Token Bucket, Leaky Bucket.

### **30. Diferencia entre request-response y messaging**

**Respuesta**:
- **Request-response**: Síncrono, bloqueante
- **Messaging**: Asíncrono, desacoplado

### **31. Qué es latency y cómo medirlo en microservicios**

**Respuesta**: Tiempo de respuesta. Medir: APM tools, distributed tracing.

### **32. Qué es resiliencia en sistemas distribuidos**

**Respuesta**: Capacidad de recuperarse de fallos (circuit breaker, retry, timeout).

### **33. Diferencia entre synchronous y asynchronous communication**

**Respuesta**:
- **Sync**: Espera respuesta (REST)
- **Async**: No espera (messaging)

### **34. Qué es monitoring y observability en microservicios**

**Respuesta**:
- **Monitoring**: Métricas, alertas
- **Observability**: Logs, métricas, traces

### **35. Qué es logging centralizado**

**Respuesta**: Logs de todos los servicios en un lugar (ELK, Splunk).

### **36. Qué es tracing distribuido y herramientas**

**Respuesta**: Rastrea request a través de servicios (Jaeger, Zipkin).

### **37. Qué es health check y readiness/liveness probes**

**Respuesta**:
- **Liveness**: Servicio vivo
- **Readiness**: Servicio listo para tráfico

### **38. Diferencia entre stateful y stateless service**

**Respuesta**:
- **Stateless**: No guarda estado, escalable
- **Stateful**: Guarda estado, complejo de escalar

### **39. Qué es containerization y ventajas**

**Respuesta**: Empaqueta app con dependencias (Docker). Ventajas: portabilidad, aislamiento.

### **40. Diferencia entre Docker y Kubernetes**

**Respuesta**:
- **Docker**: Containerización
- **Kubernetes**: Orquestación de contenedores

---

## 6. Testing y Calidad de Código

### **1. Diferencia entre unit test y integration test**

**Respuesta**:
- **Unit**: Prueba componente aislado, mocks
- **Integration**: Prueba interacción entre componentes

### **2. Qué son mocks, stubs y spies**

**Respuesta**:
- **Mock**: Objeto simulado, verifica interacciones
- **Stub**: Retorna valores predefinidos
- **Spy**: Objeto real con métodos mockeados

### **3. Cómo hacer pruebas unitarias con JUnit**

**Respuesta**:
```java
@Test
void testAdd() {
    assertEquals(5, calculator.add(2, 3));
}
```

### **4. Cómo hacer pruebas unitarias con Spock**

**Respuesta**: Framework para Groovy, sintaxis BDD.
```groovy
def "should add two numbers"() {
    expect:
    calculator.add(2, 3) == 5
}
```

### **5. Qué es test coverage**

**Respuesta**: Porcentaje de código cubierto por tests.

### **6. Qué es TDD**

**Respuesta**: Test-Driven Development. Escribir test antes que código.

### **7. Qué es BDD**

**Respuesta**: Behavior-Driven Development. Tests describen comportamiento (Given-When-Then).

### **8. Diferencia entre black-box y white-box testing**

**Respuesta**:
- **Black-box**: Sin conocer implementación
- **White-box**: Conociendo implementación

### **9. Qué son pruebas funcionales en backend**

**Respuesta**: Verifican funcionalidad completa (endpoints, flujos).

### **10. Qué es regression testing**

**Respuesta**: Verifica que cambios no rompan funcionalidad existente.

### **11. Qué son pruebas de performance y stress testing**

**Respuesta**:
- **Performance**: Mide tiempos de respuesta
- **Stress**: Prueba límites del sistema

### **12. Qué es test fixture**

**Respuesta**: Estado inicial para tests.

### **13. Cómo usar @BeforeEach, @AfterEach, @BeforeAll, @AfterAll**

**Respuesta**:
- `@BeforeEach`: Antes de cada test
- `@AfterEach`: Después de cada test
- `@BeforeAll`: Una vez antes de todos
- `@AfterAll`: Una vez después de todos

### **14. Qué es assertEquals, assertTrue, assertThrows**

**Respuesta**: Assertions de JUnit.
```java
assertEquals(expected, actual);
assertTrue(condition);
assertThrows(Exception.class, () -> method());
```

### **15. Qué es mocking con Mockito o Spock**

**Respuesta**:
```java
@Mock
private UserRepository repo;

when(repo.findById(1)).thenReturn(user);
```

### **16. Qué es spy y cuándo se usa**

**Respuesta**: Objeto real con métodos mockeados parcialmente.

### **17. Qué es integration testing con Spring Boot**

**Respuesta**:
```java
@SpringBootTest
@AutoConfigureMockMvc
class IntegrationTest {
    @Autowired
    private MockMvc mockMvc;
}
```

### **18. Qué es contract testing en microservicios**

**Respuesta**: Verifica contratos entre servicios (Pact, Spring Cloud Contract).

### **19. Qué son pruebas end-to-end**

**Respuesta**: Prueban flujo completo desde UI hasta DB.

### **20. Diferencia entre unit test y integration test de base de datos**

**Respuesta**:
- **Unit**: Mock de repositorio
- **Integration**: BD real o en memoria

### **21. Qué es test isolation y por qué es importante**

**Respuesta**: Tests independientes, no se afectan entre sí.

### **22. Qué son fixtures y datos de prueba**

**Respuesta**: Datos predefinidos para tests.

### **23. Cómo manejar dependencias externas en tests**

**Respuesta**: Mocks, stubs, test containers.

### **24. Qué es code smell y refactoring**

**Respuesta**:
- **Code smell**: Indicador de mal diseño
- **Refactoring**: Mejorar código sin cambiar funcionalidad

### **25. Qué es static code analysis y herramientas**

**Respuesta**: Analiza código sin ejecutar (SonarQube, Checkstyle).

### **26. Qué es mutation testing**

**Respuesta**: Modifica código para verificar calidad de tests (PIT).

### **27. Qué es CI/CD testing pipeline**

**Respuesta**: Tests automáticos en cada commit/deploy.

### **28. Qué es test-driven deployment**

**Respuesta**: Deploy basado en tests exitosos.

### **29. Qué es feature toggling y pruebas en producción**

**Respuesta**: Activar/desactivar features sin deploy.

### **30. Buenas prácticas de testing en microservicios**

**Respuesta**: Contract testing, test pyramid, test containers, chaos engineering.

---

## 7. DevOps y Cloud

### **1. Qué es Jenkins y para qué sirve**

**Respuesta**: Servidor de automatización CI/CD.

### **2. Qué es un pipeline de CI/CD**

**Respuesta**: Automatización de build, test, deploy.

### **3. Diferencia entre build, deploy y release**

**Respuesta**:
- **Build**: Compilar código
- **Deploy**: Instalar en ambiente
- **Release**: Disponible para usuarios

### **4. Qué es Continuous Integration**

**Respuesta**: Integrar código frecuentemente, tests automáticos.

### **5. Qué es Continuous Delivery**

**Respuesta**: Código siempre listo para deploy, manual.

### **6. Qué es Continuous Deployment**

**Respuesta**: Deploy automático a producción.

### **7. Qué son stages y jobs en Jenkins**

**Respuesta**:
- **Stage**: Fase del pipeline (build, test, deploy)
- **Job**: Tarea específica

### **8. Qué es un Jenkinsfile y declarative vs scripted pipelines**

**Respuesta**:
- **Jenkinsfile**: Pipeline as code
- **Declarative**: Sintaxis estructurada
- **Scripted**: Groovy completo, más flexible

### **9. Qué es version control y branch strategy (GitFlow)**

**Respuesta**: Git. GitFlow: master, develop, feature, release, hotfix branches.

### **10. Cómo integrar pruebas unitarias en Jenkins pipeline**

**Respuesta**:
```groovy
stage('Test') {
    steps {
        sh 'mvn test'
    }
}
```

### **11. Cómo hacer build de microservicios Spring Boot en pipeline**

**Respuesta**:
```groovy
stage('Build') {
    steps {
        sh 'mvn clean package'
    }
}
```

### **12. Cómo desplegar a Azure App Services**

**Respuesta**: Azure CLI, Azure DevOps, plugin de Jenkins.

### **13. Diferencia entre App Service y contenedor en Azure**

**Respuesta**:
- **App Service**: PaaS, managed
- **Container**: Más control, Docker

### **14. Qué es Azure DevOps**

**Respuesta**: Plataforma CI/CD de Microsoft (Repos, Pipelines, Boards).

### **15. Qué es artifact repository y ejemplos**

**Respuesta**: Almacena builds (Nexus, Artifactory, Azure Artifacts).

### **16. Qué es Dockerfile y cómo crear imágenes**

**Respuesta**:
```dockerfile
FROM openjdk:21
COPY target/app.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### **17. Qué es container registry**

**Respuesta**: Almacena imágenes Docker (Docker Hub, Azure Container Registry).

### **18. Diferencia entre Kubernetes Deployment y StatefulSet**

**Respuesta**:
- **Deployment**: Stateless, pods intercambiables
- **StatefulSet**: Stateful, identidad persistente

### **19. Qué es Helm y charts en Kubernetes**

**Respuesta**: Package manager para Kubernetes. Charts: plantillas de recursos.

### **20. Qué es CI/CD seguro y buenas prácticas**

**Respuesta**: Secrets management, escaneo de vulnerabilidades, least privilege.

### **21. Qué es rolling update y rollback**

**Respuesta**:
- **Rolling update**: Actualiza pods gradualmente
- **Rollback**: Vuelve a versión anterior

### **22. Qué es blue/green deployment**

**Respuesta**: Dos ambientes, switch instantáneo.

### **23. Qué es canary deployment**

**Respuesta**: Deploy gradual a % de usuarios.

### **24. Qué es pipeline as code**

**Respuesta**: Pipeline definido en código (Jenkinsfile, .gitlab-ci.yml).

### **25. Cómo monitorear pipelines y alertas**

**Respuesta**: Jenkins plugins, Azure Monitor, notificaciones (email, Slack).

### **26. Qué es infrastructure as code (IaC) y ejemplos**

**Respuesta**: Infraestructura definida en código (Terraform, ARM templates).

### **27. Qué es Terraform y cómo se integra con Azure**

**Respuesta**: IaC multi-cloud. Provider de Azure para crear recursos.

### **28. Qué es secret management en pipelines**

**Respuesta**: Almacenar secrets seguros (Azure Key Vault, HashiCorp Vault).

### **29. Qué es cloud-native application**

**Respuesta**: Diseñada para cloud: microservicios, contenedores, CI/CD.

### **30. Diferencias entre IaaS, PaaS y SaaS**

**Respuesta**:
- **IaaS**: Infraestructura (VMs)
- **PaaS**: Plataforma (App Service)
- **SaaS**: Software (Office 365)

---

## 8. Seguridad y Buenas Prácticas

### **1. Qué es JWT y cómo funciona en autenticación**

**Respuesta**: Token firmado con header, payload, signature. Stateless.

### **2. Qué es OAuth2 y para qué se usa**

**Respuesta**: Protocolo de autorización, delega acceso sin compartir credenciales.

### **3. Diferencia entre autenticación y autorización**

**Respuesta**:
- **Autenticación**: Quién eres (login)
- **Autorización**: Qué puedes hacer (permisos)

### **4. Qué es SQL Injection y cómo prevenirlo**

**Respuesta**: Inyectar SQL malicioso. Prevención: prepared statements, ORM.

### **5. Qué es XSS y cómo mitigarlo**

**Respuesta**: Cross-Site Scripting. Inyectar scripts. Prevención: sanitizar input, CSP headers.

### **6. Qué es CSRF y cómo prevenirlo**

**Respuesta**: Cross-Site Request Forgery. Prevención: CSRF tokens, SameSite cookies.

### **7. Qué es CORS y cómo configurarlo en Spring**

**Respuesta**: Ver pregunta 28 de Spring.

### **8. Qué es hashing y diferencia con cifrado**

**Respuesta**:
- **Hashing**: Una vía, no reversible (passwords)
- **Cifrado**: Dos vías, reversible (datos sensibles)

### **9. Qué es SSL/TLS y por qué es importante**

**Respuesta**: Cifrado de comunicación HTTPS, previene man-in-the-middle.

### **10. Qué es OAuth2 Client Credentials vs Authorization Code**

**Respuesta**:
- **Client Credentials**: Machine-to-machine
- **Authorization Code**: Usuario autoriza, más seguro

### **11. Qué es OAuth2 Resource Server**

**Respuesta**: Servidor que valida tokens OAuth2.

### **12. Qué es refresh token y access token**

**Respuesta**:
- **Access token**: Corta duración, acceso a recursos
- **Refresh token**: Larga duración, renueva access token

### **13. Qué es password hashing y algoritmos recomendados**

**Respuesta**: BCrypt, Argon2, PBKDF2. Evitar MD5, SHA1.

### **14. Qué es seguridad en microservicios**

**Respuesta**: API Gateway, JWT, mTLS, service mesh, secrets management.

### **15. Qué es API key y cuándo usarlo**

**Respuesta**: Clave para identificar cliente. Usar para APIs públicas, no para usuarios.

### **16. Qué es rate limiting y cómo mejora seguridad**

**Respuesta**: Previene DDoS, brute force.

### **17. Qué es logging seguro**

**Respuesta**: No loggear passwords, tokens, datos personales.

### **18. Qué son roles y scopes en APIs REST**

**Respuesta**:
- **Roles**: Grupos de permisos (ADMIN, USER)
- **Scopes**: Permisos específicos (read:users, write:posts)

### **19. Qué es principle of least privilege**

**Respuesta**: Dar solo permisos necesarios.

### **20. Buenas prácticas de seguridad en backend**

**Respuesta**:
- Validar input
- Usar HTTPS
- Hashear passwords
- Actualizar dependencias
- Logging seguro
- Rate limiting
- CORS configurado
- Secrets en variables de entorno

---

## 📝 Notas Finales

- **Practica**: Implementa estos conceptos en proyectos reales
- **Profundiza**: Cada respuesta es un punto de partida
- **Actualízate**: Tecnologías evolucionan constantemente
- **Comunica**: Explica con claridad en entrevistas

**¡Éxito en tu entrevista!** 🚀
