# Documentación de Análisis y Diseño - Integrante 2

**Proyecto:** Sistema de Gestión de Operaciones QuindíoExpress
**Asignatura:** Estructuras de Datos

---

## Introducción y alcance

Este documento describe y justifica las decisiones de diseño tomadas por el **Integrante 2** en el uso del **Java Collections Framework (JCF)** para el sistema QuindíoExpress. El análisis está vinculado **directamente con el código del repositorio**, en particular con las siguientes clases:

| Clase | Responsabilidad |
| :--- | :--- |
| `com.quindioexpress.servicios.GestorColeccionesPaquetes` | Indexación y consultas sobre paquetes (HashMap, HashSet, TreeSet, agrupación por municipio). |
| `com.quindioexpress.servicios.GestorDespachoPrioritario` | Despacho por prioridad con `PriorityQueue` y reglas de desempate. |
| `com.quindioexpress.servicios.ServicioAsignacionRepartidor` | Registro y asignación de repartidores con `LinkedHashMap`. |
| `com.quindioexpress.servicios.CentroDistribucion` | Fachada de servicio; aloja la cola FIFO de atención (`ArrayDeque`). |
| `com.quindioexpress.modelo.Paquete` | Modelo de dominio; `Comparable` y `equals`/`hashCode` por código. |
| `com.quindioexpress.modelo.Repartidor` | Modelo de dominio; `equals`/`hashCode` por identificación. |
| `com.quindioexpress.util.GeneradorDatosIniciales` | Dataset inicial: **32 paquetes**, **6 municipios**, **8 repartidores**. |

Todas las decisiones se validan contra las reglas de negocio reales implementadas: peso `> 0`, prioridad en `[1, 5]`, tiempo estimado positivo, códigos e identificadores únicos, y coincidencia exacta de zona del repartidor con el destino del paquete.

---

## PARTE A.1: Selección de Colecciones del Java Collections Framework (JCF)

La siguiente tabla resume la decisión de estructura para cada necesidad operativa asignada al Integrante 2. Los detalles ampliados se desarrollan en las subsecciones posteriores.

| Necesidad | Estructura elegida | Operación principal | Complejidad esperada (Tiempo / Espacio) | Alternativa descartada | Justificación Técnica |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **1.** Conservar el orden exacto de llegada | `Queue<Paquete>` → `ArrayDeque<Paquete>` (atención FIFO) y `List<Paquete>` → `ArrayList` (registro de auditoría) | `offer()` (encolar al final) / `poll()` (desencolar al frente) | Tiempo: `offer` **O(1)** amortizado, `poll` **O(1)**; Espacio: **O(n)** | `PriorityQueue` (reordena, no respeta llegada) o `LinkedList` (overhead de nodos y peor localidad de caché) | `ArrayDeque` es una cola de doble extremo sobre un arreglo circular: no crea nodos por elemento, ofrece O(1) en ambos extremos y preserva el orden de encolado. |
| **2.** Búsqueda rápida por código | `HashMap<String, Paquete>` (`mapaPorCodigo`) | `get()` / `put()` | Tiempo: promedio **O(1)**, peor caso **O(n)**; Espacio: **O(n)** | `ArrayList` con búsqueda lineal **O(n)** o `TreeMap` **O(log n)** | La clave `String` tiene `hashCode()` bien distribuido; `Paquete` define `equals`/`hashCode` por `codigo`, garantizando unicidad y acceso directo por bucket. |
| **3.** Municipios sin duplicados | `HashSet<String>` (`municipiosUnicos`) | `add()` (y `contains()`) | Tiempo: promedio **O(1)**, peor caso **O(n)**; Espacio: **O(n)** | `ArrayList` (admite duplicados y `contains` es **O(n)**) | El `HashSet` descarta automáticamente destinos repetidos apoyándose en `equals`/`hashCode` de `String`, sin recorrer la colección. |
| **4.** Municipios ordenados sin duplicados | `TreeSet<String>` (`municipiosOrdenados`) | `add()` con orden natural por `compareTo` | Tiempo: **O(log n)** por inserción/consulta; Espacio: **O(n)** | `HashSet` + `Collections.sort()` por reporte (**O(n log n)** cada vez) | Mantiene el **orden alfabético en todo momento** (árbol rojo-negro); generar el reporte ordenado es simplemente iterar en **O(n)**, sin reordenar. |
| **5.** Despacho por prioridad y desempate | `PriorityQueue<Paquete>` con `Comparator` (`ComparadorDespacho`) | `offer()`/`add()` y `poll()`; `peek()` para inspección | Tiempo: **O(log n)** insertar/extraer, **O(1)** inspeccionar; Espacio: **O(n)** | `ArrayList` ordenada (inserción **O(n)**) o `TreeSet` (imposición de orden total incompatible con el criterio) | Un **heap binario** extrae siempre el mínimo según el comparador. El comparador aplica las tres reglas: prioridad ↓, tiempo ↑, código ↑. |

### 1. Conservar el orden exacto de llegada — `Queue<Paquete>` (`ArrayDeque`)

**Operación dominante:** `offer(paquete)` para encolar y `poll()` para atender.

El enunciado exige dos cosas distintas que en el código se separan limpiamente:

- **Registro de auditoría** (orden de registro inmutable): `GestorColeccionesPaquetes.ordenLlegada` es un `ArrayList<Paquete>`.
- **Atención operativa FIFO** (pendientes por atender): `CentroDistribucion.colaLlegada` es un `Queue<Paquete>` respaldado por `ArrayDeque`.

```java
// CentroDistribucion (fachada): cola FIFO de atención
private final Queue<Paquete> colaLlegada;   // new ArrayDeque<>()

public boolean registrarNuevoPaquete(Paquete paquete) {
    if (!gestorColecciones.registrarPaquete(paquete)) return false;
    gestorDespacho.encolarPaquete(paquete);
    colaLlegada.offer(paquete);   // O(1) amortizado
    return true;
}

public Paquete atenderPorOrdenLlegada() {
    return colaLlegada.poll();     // O(1) — extrae el más antiguo
}
```

**Complejidad.** `ArrayDeque` es un arreglo circular que crece por duplicación; `offer` y `poll` operan en los extremos en **O(1)** (con O(1) amortizado en los redimensionamientos), y el acceso aleatorio por índice es **O(1)**. El espacio es **O(n)** en referencias.

**Sacrificio / alternativa descartada.** Se descartó `PriorityQueue` porque **reordena** según prioridad y rompe el FIFO. Se descartó `LinkedList` (que también implementa `Queue`) porque cada elemento implica un **nodo con dos punteros**, más consumo de memoria y peor localidad de caché; `ArrayDeque` evita ese overhead. `ArrayDeque` no admite `null`, lo cual es coherente con la validación de `registrarNuevoPaquete`.

### 2. Búsqueda rápida por código — `HashMap<String, Paquete>`

**Operación dominante:** `get(codigo)` y `put(codigo, paquete)`.

```java
// GestorColeccionesPaquetes
private final Map<String, Paquete> mapaPorCodigo;   // new HashMap<>()

public Paquete buscarPorCodigo(String codigo) {
    return mapaPorCodigo.get(codigo);               // O(1) promedio
}

public boolean registrarPaquete(Paquete paquete) {
    if (mapaPorCodigo.containsKey(paquete.getCodigo())) return false; // unicidad
    mapaPorCodigo.put(paquete.getCodigo(), paquete);
    ...
}
```

**Complejidad.** En promedio **O(1)**: se calcula `hashCode()` de la clave, se indexa el bucket (`hash & (n-1)`) y se compara con `equals`. El **peor caso es O(n)** si todas las claves colisionan en el mismo bucket (encadenamiento degenerado); con `String` y una función hash de calidad, esto es estadísticamente improbable.

**Sacrificio / alternativa descartada.** Frente a un `ArrayList` (búsqueda **O(n)**) se sacrifica **memoria**: el `HashMap` mantiene un arreglo de buckets y un objeto `Node<K,V>` por entrada. Frente a un `TreeMap` (**O(log n)**, y ordenado por clave) se sacrifica el orden de las claves, que aquí no se necesita para la búsqueda. La unicidad se apoya en que `Paquete` define:

```java
// Paquete: identidad basada en el código
@Override public boolean equals(Object o) { ... return Objects.equals(codigo, paquete.codigo); }
@Override public int hashCode() { return Objects.hash(codigo); }
```

### 3. Municipios sin duplicados — `HashSet<String>`

**Operación dominante:** `add(destino)`.

```java
// GestorColeccionesPaquetes
private final Set<String> municipiosUnicos;   // new HashSet<>()
...
municipiosUnicos.add(paquete.getDestino());   // ignora repetidos en O(1) promedio
```

**Complejidad.** `add` es **O(1)** promedio y **O(n)** en el peor caso. El espacio es **O(k)** donde *k* es el número de municipios distintos (en el dataset, 6: `Armenia`, `Calarcá`, `Filandia`, `Montenegro`, `Quimbaya`, `Salento`).

**Sacrificio / alternativa descartada.** Se descartó `ArrayList`, que **permitiría duplicados** y exigiría `contains` (**O(n)**) antes de cada inserción. El `HashSet` delega la unicidad a `equals`/`hashCode` de `String`, sin recorridos. El sacrificio es no conservar ningún orden — irrelevante para esta necesidad, cubierta por la estructura siguiente.

### 4. Municipios ordenados sin duplicados — `TreeSet<String>`

**Operación dominante:** `add(destino)` con orden natural.

```java
// GestorColeccionesPaquetes
private final SortedSet<String> municipiosOrdenados;   // new TreeSet<>()
...
municipiosOrdenados.add(paquete.getDestino());         // inserta en orden alfabético
```

**Complejidad.** Cada inserción/consulta es **O(log n)** (árbol rojo-negro autobalanceado). Generar el reporte ordenado es iterar el `TreeSet` en **O(n)** ya ordenado.

**Sacrificio / alternativa descartada.** Frente a `HashSet`, se sacrifica el **O(1)** de inserción a cambio del **O(log n)** que mantiene el orden. Frente a "ordenar un `ArrayList` cada vez con `Collections.sort()`" se evita el **O(n log n)** por reporte. El resultado real observado en `Main` es `[Armenia, Calarcá, Filandia, Montenegro, Quimbaya, Salento]`.

### 5. Despacho por prioridad y desempate — `PriorityQueue<Paquete>`

**Operación dominante:** `offer(paquete)` y `poll()`.

```java
// GestorDespachoPrioritario
private final PriorityQueue<Paquete> colaPrioritaria;  // new PriorityQueue<>(new ComparadorDespacho())

public void encolarPaquete(Paquete paquete) { if (paquete != null) colaPrioritaria.add(paquete); } // O(log n)
public Paquete despacharSiguiente() { return colaPrioritaria.poll(); }                              // O(log n)
public Paquete verSiguiente() { return colaPrioritaria.peek(); }                                    // O(1)
public int cantidadPendientes() { return colaPrioritaria.size(); }
public boolean estaVacia() { return colaPrioritaria.isEmpty(); }
```

**Reglas de desempate** implementadas en el comparador anidado `ComparadorDespacho`:

```java
@Override public int compare(Paquete p1, Paquete p2) {
    int prioridad = Integer.compare(p2.getPrioridad(), p1.getPrioridad()); // 1) mayor prioridad primero
    if (prioridad != 0) return prioridad;
    int tiempo = Double.compare(p1.getTiempoEstimado(), p2.getTiempoEstimado()); // 2) menor tiempo primero
    if (tiempo != 0) return tiempo;
    return p1.getCodigo().compareTo(p2.getCodigo()); // 3) orden alfabético por código
}
```

**Complejidad.** Insertar y extraer es **O(log n)** (reorganización del heap); inspeccionar con `peek` es **O(1)**. El espacio es **O(n)**.

**Sacrificio / alternativa descartada.** Un `ArrayList` ordenado implicaría inserción **O(n)** (desplazar elementos) o extracción por búsqueda del máximo en **O(n)**. Un `TreeSet` impondría un orden total único y no permitiría expresar de forma natural el criterio tripartito sobre campos mutables; además `Paquete implements Comparable<Paquete>` ya define un orden por `codigo`, que **no** es el orden de despacho. La `PriorityQueue` con `Comparator` explícito desacopla el criterio de despacho del orden natural del modelo.

### Colecciones complementarias (agrupación y repartidores)

Aunque las cinco necesidades anteriores son las principales, el Integrante 2 usa dos colecciones adicionales relevantes:

| Necesidad | Estructura elegida | Operación principal | Complejidad | Alternativa descartada | Justificación |
| :--- | :--- | :--- | :--- | :--- | :--- |
| Agrupar paquetes por municipio | `Map<String, Set<Paquete>>` con valores `LinkedHashSet` | `computeIfAbsent(k, ..).add(p)` | **O(1)** promedio por paquete; Espacio **O(n)** | `Map<String, List<Paquete>>` | El `LinkedHashSet` evita duplicados y conserva el orden de inserción dentro de cada municipio. |
| Registro y asignación de repartidores | `LinkedHashMap<String, Repartidor>` | `put()` / `get(id)` / iterar `values()` | `put`/`get` **O(1)** promedio; iteración **O(m)** | `HashMap` | Conserva el **orden de registro** para que `buscarRepartidorDisponiblePara` sea determinista. |

```java
// GestorColeccionesPaquetes: agrupación por municipio
paquetesPorMunicipio
    .computeIfAbsent(paquete.getDestino(), k -> new LinkedHashSet<>())
    .add(paquete);

// ServicioAsignacionRepartidor: registro indexado por id
private final Map<String, Repartidor> repartidoresPorId; // new LinkedHashMap<>()
public Repartidor buscarPorId(String idRepartidor) { return repartidoresPorId.get(idRepartidor); } // O(1)
public Repartidor buscarRepartidorDisponiblePara(Paquete paquete) {
    for (Repartidor r : repartidoresPorId.values()) { // O(m), m = nº repartidores
        if (r.isDisponible() && r.getZonaAsignada().equals(paquete.getDestino())) return r;
    }
    return null;
}
```

> **Nota sobre la identidad del modelo.** `Repartidor` define `equals`/`hashCode` por `idRepartidor` (no por zona), por lo que el índice `LinkedHashMap` garantiza unicidad de identificadores, mientras que la coincidencia de zona se resuelve por comparación explícita en `buscarRepartidorDisponiblePara`.

---

## PARTE A.2: Análisis de Tiempo frente a Memoria (Escenario de Crecimiento a 500.000 Paquetes)

**Escenario:** la operación deja de manejar los 30 paquetes iniciales y crece hasta **500.000 paquetes**. La operación **más frecuente** es *buscar un paquete por su código*. Se comparan las dos alternativas naturales: `ArrayList<Paquete>` (búsqueda lineal) vs. `HashMap<String, Paquete>` (indexación por hash).

### 1. Tiempo esperado de búsqueda

#### `ArrayList<Paquete>` — búsqueda lineal **O(n)**

Un `ArrayList` no indexa por contenido. Para encontrar un paquete por código hay que **recorrer la lista** comparando el código de cada elemento:

```java
// Búsqueda lineal hipotética sobre ArrayList (NO es la implementación actual)
public Paquete buscarPorCodigo(String codigo) {
    for (Paquete p : lista) {                  // hasta n iteraciones
        if (p.getCodigo().equals(codigo)) return p;
    }
    return null;
}
```

- **Caso promedio:** n / 2 = **250.000 comparaciones** de `String`.
- **Peor caso (elemento ausente o último):** **500.000 comparaciones**, cada una un `String.equals` con verificación de longitud y comparación de caracteres.
- Si la aplicación realiza, por ejemplo, 1.000 consultas por lote, el costo total escala a **5 × 10⁸** comparaciones.

#### `HashMap<String, Paquete>` — búsqueda **O(1)** promedio

El `HashMap` traduce la clave a un índice de bucket mediante el hash y accede **directamente**:

```java
// Implementación real en GestorColeccionesPaquetes
public Paquete buscarPorCodigo(String codigo) {
    return mapaPorCodigo.get(codigo);          // O(1) promedio
}
```

Proceso interno de `get(codigo)`:
1. `hashCode()` de la clave. En `String`, el hash se **calcula una sola vez y se cachea** en el campo interno `hash`, por lo que las búsquedas repetidas no recalculan.
2. Perturbación del hash y cálculo del índice: `(n - 1) & hash`.
3. Comparación `equals` con el primer `Node` de ese bucket (normalmente **una sola** comparación).

Resultado: **1 comparación típica** frente a **250.000–500.000** del `ArrayList`. La diferencia es de **cinco órdenes de magnitud** en número de comparaciones.

### 2. Análisis de memoria adicional (overhead)

A continuación se estima el consumo **adicional** (los objetos `Paquete` y sus `String` existen en ambos casos y se comparten). Estimaciones conceptuales bajo **compressed oops** (referencias de 4 bytes, JVM de 64 bits con heap < 32 GB):

| Componente | `ArrayList<Paquete>` | `HashMap<String, Paquete>` |
| :--- | :--- | :--- |
| Arreglo de referencias | `Object[500000]` ≈ **2,0 MB** (500.000 × 4 B) + cabecera | Arreglo de buckets ≈ **4,0 MB** (1.048.576 × 4 B) |
| Objetos nodo por entrada | No aplica | `Node<K,V> × 500.000` ≈ **16 MB** (cabecera + `hash` int + 3 referencias) |
| Estructura por elemento (encadenamiento / metadatos) | No aplica | Referencia `next` incluida en el `Node` |
| Referencias a claves/valores | 1 por elemento (el `Paquete`) | 1 key + 1 value por `Node` (la key reutiliza el `String` ya contenido en `Paquete`) |
| **Overhead adicional total aprox.** | **≈ 2–4 MB** | **≈ 20–28 MB** |

**Dimensionamiento del `HashMap`.** Con factor de carga por defecto **0,75**, el umbral de redimensionamiento es `capacidad × 0,75`. Para almacenar 500.000 entradas sin exceder el umbral se requiere una capacidad de al menos `500.000 / 0,75 ≈ 666.667`, y como `HashMap` usa potencias de dos, la tabla se estabiliza en **1.048.576 = 2²⁰** buckets.

**Costo de rehash.** La tabla nace en 16 y **duplica** su capacidad al superar el umbral, es decir unas **16 redimensiones** hasta llegar a 2²⁰. Cada rehash reubica las entradas existentes; el costo total agregado es **O(n)** amortizado, pero puede provocar **pausas** puntuales. Puede mitigarse con `new HashMap<>(capacidadInicialEstimada)`.

**Conclusión de memoria.** El `HashMap` cuesta en torno a **20–28 MB extra** frente al `ArrayList` para 500.000 registros: un overhead de un orden de magnitud sobre el arreglo de referencias, pero **absolutamente marginal** en términos absolutos (decenas de MB) frente a la enorme ganancia temporal.

### 3. Matriz de ventajas y desventajas

| Criterio | `ArrayList<Paquete>` | `HashMap<String, Paquete>` |
| :--- | :--- | :--- |
| **Inserción** | `add()` al final: **O(1)** amortizado | `put()`: **O(1)** promedio; rehash ocasional **O(n)** amortizado |
| **Búsqueda por código** | **O(n)** (lineal) | **O(1)** promedio / **O(n)** peor caso (colisiones degeneradas) |
| **Acceso por posición** | **O(1)** (`get(i)`) | No soportado (no es posicional) |
| **Orden de inserción** | **Preservado** | **No garantizado** (`HashMap`); requiere `LinkedHashMap` para conservarlo |
| **Rehash / redimensionamiento** | No aplica | Duplicación de capacidad; costo agregado **O(n)**, posibles pausas |
| **Consumo de memoria** | **Bajo** (solo el arreglo de referencias) | **Mayor** (buckets + factor de carga + objetos `Node`) |
| **Manejo de colisiones** | No aplica | Encadenamiento; degenera a **O(n)** con hash malo |
| **Iteración** | **O(n)** en orden de inserción | **O(n + capacidad)** en orden no garantizado |
| **Unicidad de claves** | Debe validarse manualmente | **Garantizada** por `Map` (una clave, un valor) |

### 4. Decisión final y justificación argumentada

**Se selecciona `HashMap<String, Paquete>`** como estructura de indexación principal, exactamente como está implementado en `GestorColeccionesPaquetes.mapaPorCodigo` y expuesto al resto del sistema a través de `CentroDistribucion.buscarPorCodigo`.

Argumentos:

1. **La operación dominante es la búsqueda por código.** Reducir su costo de **O(n)** a **O(1)** promedio transforma cada consulta de hasta 500.000 comparaciones a prácticamente una. Dado que `.md` y el código documentan la búsqueda por código como la operación más frecuente, optimizarla es la decisión de mayor impacto.
2. **El costo en memoria está plenamente justificado.** El overhead adicional de ≈ 20–28 MB para 500.000 paquetes es despreciable frente a la ganancia de tiempo; la relación costo/beneficio favorece claramente al `HashMap`.
3. **No se sacrifica el orden de llegada.** El diseño mantiene **en paralelo** el `ArrayList` `ordenLlegada` (auditoría FIFO) y la `Queue` `colaLlegada` (`ArrayDeque`, atención FIFO). El `HashMap` se usa **solo** para el índice por código, de modo que se obtiene lo mejor de cada estructura sin renunciar al orden cuando se necesita.
4. **La unicidad viene gratis.** El `Map` garantiza una única entrada por clave; la validación `mapaPorCodigo.containsKey(...)` antes de `put` refuerza la regla de negocio de códigos únicos y define la semántica de rechazo de duplicados.

---

## Anexo: resumen de complejidades de la implementación

| Estructura (campo real) | Clase JCF | Operación clave | Complejidad | Ubicación en el código |
| :--- | :--- | :--- | :--- | :--- |
| `ordenLlegada` | `ArrayList` | `add` | **O(1)** amortizado | `GestorColeccionesPaquetes` |
| `colaLlegada` | `ArrayDeque` (`Queue`) | `offer` / `poll` | **O(1)** | `CentroDistribucion` |
| `mapaPorCodigo` | `HashMap` | `get` / `put` | **O(1)** promedio | `GestorColeccionesPaquetes` |
| `municipiosUnicos` | `HashSet` | `add` | **O(1)** promedio | `GestorColeccionesPaquetes` |
| `municipiosOrdenados` | `TreeSet` | `add` | **O(log n)** | `GestorColeccionesPaquetes` |
| `paquetesPorMunicipio` | `HashMap` + `LinkedHashSet` | `computeIfAbsent` / `add` | **O(1)** promedio | `GestorColeccionesPaquetes` |
| `colaPrioritaria` | `PriorityQueue` + `Comparator` | `add` / `poll` | **O(log n)** | `GestorDespachoPrioritario` |
| `repartidoresPorId` | `LinkedHashMap` | `put` / `get` | **O(1)** promedio | `ServicioAsignacionRepartidor` |

**Verificación empírica.** La clase `com.quindioexpress.principal.Main` demuestra estos comportamientos sobre el dataset real (`GeneradorDatosIniciales`: 32 paquetes y 8 repartidores): consulta O(1) por código, municipios únicos y ordenados, agrupación por municipio, atención FIFO, despacho por prioridad con verificación del orden `[PQ105, PQ731, PQ942, PQ420]` y asignación/entrega de repartidores.
