# QuindíoExpress — Arquitectura y Especificaciones del Código

**Alcance:** únicamente la **Parte B (Construcción)** del Parcial 1. La documentación (Partes A, C, D y E) queda fuera de este documento.
**Lenguaje:** Java 17+ (sin librerías externas).
**Temas permitidos:** Java, recursividad, pila de llamadas, búsqueda binaria, divide y vencerás, complejidad, Collections Framework (List, Set, Map, Queue, Deque, PriorityQueue), genéricos, Iterable/Iterator, lista simplemente enlazada propia, Comparable y Comparator.

---

## 1. Resumen del sistema

Aplicación de consola que gestiona una jornada de un centro de distribución: registro y consulta de paquetes y repartidores, atención por orden de llegada o por prioridad, agrupación por municipio, análisis recursivos y de divide y vencerás, búsqueda binaria, asignación a repartidores e historial de entregas (lista enlazada propia).

Los datos iniciales se definen directamente en Java (sin archivos ni base de datos).

---

## 2. Estructura del proyecto

```
src/
└── co/quindioexpress/
    ├── Main.java
    ├── modelo/
    │   ├── Paquete.java
    │   └── Repartidor.java
    ├── estructuras/
    │   ├── Coleccion.java            (interfaz genérica)
    │   └── ListaEnlazada.java        (lista simplemente enlazada propia, con Nodo e Iterator internos)
    ├── comparadores/
    │   ├── ComparadorPrioridad.java  (prioridad de mayor a menor)
    │   ├── ComparadorPeso.java       (peso de mayor a menor)
    │   ├── ComparadorTiempo.java     (tiempo estimado de menor a mayor)
    │   └── ComparadorDespacho.java   (prioridad desc → tiempo asc → código asc)
    ├── algoritmos/
    │   ├── AlgoritmosRecursivos.java
    │   ├── BusquedaBinaria.java
    │   └── DivideYVenceras.java
    ├── servicio/
    │   └── CentroDistribucion.java   (fachada con toda la lógica del negocio)
    ├── excepciones/
    │   └── ReglaNegocioException.java
    └── datos/
        └── DatosIniciales.java
README.md
```

### Responsabilidades por capa

| Capa | Responsabilidad | Depende de |
|---|---|---|
| `modelo` | Entidades con sus validaciones propias | — |
| `estructuras` | Estructura de datos propia y genérica | — |
| `comparadores` | Criterios de ordenamiento reutilizables | `modelo` |
| `algoritmos` | Algoritmos puros, sin estado (métodos estáticos genéricos o sobre `Paquete`) | `modelo`, `comparadores` |
| `servicio` | Reglas de negocio y coordinación de estructuras | todas las anteriores |
| `Main` | Interfaz de consola y demostración | `servicio`, `datos` |

---

## 3. Especificación de clases

### 3.1 `modelo.Paquete` — implementa `Comparable<Paquete>`

| Atributo | Tipo | Regla |
|---|---|---|
| `codigo` | `String` | Único en la jornada; no nulo ni vacío |
| `destino` | `String` | No nulo ni vacío (normalizar con `trim`) |
| `peso` | `double` | `> 0` (kg) |
| `prioridad` | `int` | Rango válido (ver nota 1); por defecto `3` |
| `tiempoEstimado` | `int` | Entero `> 0` (minutos) |

- **Constructores:** uno completo y otro sin prioridad (asigna `PRIORIDAD_POR_DEFECTO = 3`).
- **Constantes:** `PRIORIDAD_MIN`, `PRIORIDAD_MAX`, `PRIORIDAD_POR_DEFECTO`.
- **Inmutable:** atributos `final`, solo getters. Las validaciones lanzan `ReglaNegocioException` en el constructor.
- **`compareTo`:** orden natural = **código ascendente** (`this.codigo.compareTo(otro.codigo)`).
- **`equals` / `hashCode`:** basados **solo en `codigo`** (consistentes con `compareTo`).
- **`toString`:** formato legible, p. ej. `PQ731 | Armenia | 8.5 kg | P5 | 45 min`.

### 3.2 `modelo.Repartidor`

| Atributo | Tipo | Regla |
|---|---|---|
| `identificacion` | `String` | Única; no nulo ni vacío |
| `nombre` | `String` | No nulo ni vacío |
| `zona` | `String` | Municipio; no nulo ni vacío |
| `disponible` | `boolean` | Mutable (`setDisponible`) |

- `equals`/`hashCode` por `identificacion`.
- Un repartidor no disponible **no** puede recibir asignaciones.

### 3.3 `excepciones.ReglaNegocioException`

`RuntimeException` con mensaje descriptivo. Se usa para toda violación de reglas (duplicados, valores inválidos, asignación no permitida, precondición de búsqueda binaria, etc.).

---

## 4. Estructura propia (obligatoria)

### 4.1 `estructuras.Coleccion<T>` — interfaz genérica

```java
public interface Coleccion<T> extends Iterable<T> {
    void agregar(T elemento);
    boolean eliminar(T elemento);   // true si lo encontró y eliminó; false si no existe
    T obtener(int posicion);        // IndexOutOfBoundsException si la posición es inválida
    int tamano();
    boolean estaVacia();
}
```

### 4.2 `estructuras.ListaEnlazada<T>` — implementa `Coleccion<T>`

**Restricción crítica:** no usar internamente `ArrayList`, `LinkedList`, `Vector`, `Stack`, `Queue`, `Deque` ni ninguna colección de Java para almacenar elementos.

- **`Nodo<T>`:** clase interna `private static final` con `T valor` y `Nodo<T> siguiente`. No debe ser accesible desde fuera.
- **Atributos:** `Nodo<T> cabeza`, `Nodo<T> cola`, `int tamano`.
- **`agregar(T)`:** inserta al **final** (O(1) usando `cola`) para conservar el orden exacto de entrega.
- **`eliminar(T)`:** elimina la primera ocurrencia (por `equals`) y mantiene `cola` y `tamano` correctos. Devuelve `false` si no existe.
- **`obtener(int)`:** recorre hasta la posición; valida rango.
- **`tamano()` / `estaVacia()`:** O(1).
- **`iterator()`:** devuelve un `Iterator<T>` implementado como clase interna.
  - `hasNext()`: hay nodo pendiente.
  - `next()`: lanza `NoSuchElementException` si no hay más elementos.
  - `remove()`: no soportado (`UnsupportedOperationException`) o implementado, pero decidido y documentado.
- El código externo **nunca** accede a `cabeza` ni a los nodos: recorre solo con `for-each` o `iterator()`.

### 4.3 Casos límite que debe cubrir (y demostrar en `Main`)

| Caso | Comportamiento esperado |
|---|---|
| Lista vacía | `estaVacia() == true`, `tamano() == 0`, `obtener(0)` lanza excepción, `eliminar(x)` devuelve `false`, iterador sin elementos |
| Un solo elemento | Al eliminarlo, `cabeza` y `cola` quedan en `null` |
| Eliminar el primero | `cabeza` avanza; si queda vacía, `cola = null` |
| Eliminar uno intermedio | Se reenlaza `anterior.siguiente` |
| Eliminar el último | `cola` pasa al nodo anterior |
| Eliminar inexistente | Devuelve `false`, sin cambios |

---

## 5. Comparadores (`comparadores`)

Todos implementan `Comparator<Paquete>`, son clases reutilizables (no lambdas anónimas dispersas) y **no alteran** el orden natural de `Paquete`.

| Clase | Criterio |
|---|---|
| `ComparadorPrioridad` | Prioridad de **mayor a menor** |
| `ComparadorPeso` | Peso de **mayor a menor** |
| `ComparadorTiempo` | Tiempo estimado de **menor a mayor** |
| `ComparadorDespacho` | 1) mayor prioridad, 2) menor tiempo estimado, 3) código ascendente |

`ComparadorDespacho` es el que se usa en la `PriorityQueue`. Se recomienda implementarlo de forma que pueda **componerse** a partir de los anteriores (p. ej. `thenComparing`) para evidenciar la reutilización.

---

## 6. Algoritmos (`algoritmos`)

### 6.1 `AlgoritmosRecursivos` — **prohibido usar `for`, `while`, `do-while` ni streams**

| Método | Firma sugerida | Caso base | Caso recursivo |
|---|---|---|---|
| Peso total por municipio | `double pesoTotalPorDestino(List<Paquete> paquetes, String destino, int indice)` | `indice == paquetes.size()` → `0.0` | `(coincide ? peso : 0) + pesoTotalPorDestino(..., indice + 1)` |
| Contar prioridad mínima | `int contarPrioridadMinima(List<Paquete> paquetes, int minima, int indice)` | `indice == paquetes.size()` → `0` | `(prioridad >= minima ? 1 : 0) + contarPrioridadMinima(..., indice + 1)` |

- Comparación de destino sin sensibilidad a mayúsculas/tildes inconsistentes (decidir una política y aplicarla de forma uniforme; recomendado `equalsIgnoreCase`).
- Cada método público debe tener una sobrecarga de entrada que inicie con `indice = 0`.
- **Modo traza opcional:** una bandera estática (`TRAZA`) que imprima cada llamada con sangría según profundidad, parámetros recibidos y valor retornado. Facilita construir la tabla de pila de llamadas de la Parte C.

### 6.2 `BusquedaBinaria`

```java
public static <T extends Comparable<T>> int buscar(List<T> ordenada, T objetivo){}
```

- **Precondición:** la lista debe estar ordenada ascendentemente por orden natural. Se recomienda un método auxiliar `estaOrdenada(List<T>)` y lanzar `ReglaNegocioException` si no se cumple (o documentar claramente que la responsabilidad es del llamador).
- Devuelve el **índice** o `-1` si no existe.
- Puede ser iterativa o recursiva; documentar la elección. Complejidad esperada: O(log n) tiempo, O(1) espacio (iterativa) u O(log n) (recursiva).
- Variante útil: `buscarPorCodigo(List<Paquete> ordenada, String codigo)`.
- El servicio debe generar la lista ordenada por código con una **copia** (`new ArrayList<>(pendientes)` + `Collections.sort`) sin alterar la lista original de registro.

### 6.3 `DivideYVenceras`

```java
public static Paquete paqueteMayorPeso(List<Paquete> paquetes, int inicio, int fin){}
```

- **Caso base:** `inicio == fin` → devuelve `paquetes.get(inicio)`.
- **División:** `medio = inicio + (fin - inicio) / 2`.
- **Subproblema izquierdo:** `paqueteMayorPeso(paquetes, inicio, medio)`.
- **Subproblema derecho:** `paqueteMayorPeso(paquetes, medio + 1, fin)`.
- **Combinación:** devuelve el de mayor peso (definir criterio de empate, p. ej. el de la izquierda).
- **Resultado final:** el paquete de mayor peso del conjunto completo.
- Lista vacía o nula → `ReglaNegocioException`.
- Con modo traza, debe poder verse el árbol de divisiones (izquierda/derecha/combinación).

> No basta con que el método sea recursivo: debe dividir en mitades y **combinar** resultados.

---

## 7. Servicio principal — `servicio.CentroDistribucion`

### 7.1 Estructuras internas (selección de colecciones)

| Necesidad | Estructura | Operación principal | Complejidad esperada |
|---|---|---|---|
| Pendientes en orden de registro (auditoría) | `List<Paquete>` → `ArrayList` | `add` al final / recorrido | O(1) amortizado / O(n) |
| Consulta por código y validación de duplicados | `Map<String, Paquete>` → `HashMap` | `get` / `containsKey` | O(1) esperado |
| Municipios sin repetir | `Set<String>` → `HashSet` | `add` / `contains` | O(1) esperado |
| Municipios ordenados alfabéticamente sin repetir | `TreeSet<String>` | `add` / recorrido ordenado | O(log n) / O(n) |
| Paquetes por municipio | `Map<String, Set<Paquete>>` → `HashMap` con `LinkedHashSet` | `computeIfAbsent(...).add` | O(1) esperado |
| Atención por orden de llegada (FIFO) | `Queue<Paquete>` → `ArrayDeque` | `offer` / `poll` | O(1) |
| Despacho prioritario | `PriorityQueue<Paquete>` con `ComparadorDespacho` | `offer` / `poll` | O(log n) |
| Historial de entregas | `ListaEnlazada<Paquete>` (propia) | `agregar` al final | O(1) |
| Repartidores | `Map<String, Repartidor>` → `HashMap` (por identificación) | `get` / `containsKey` | O(1) esperado |
| Asignaciones vigentes | `Map<String, Repartidor>` (código de paquete → repartidor) | `put` / `remove` | O(1) esperado |

> Los municipios ordenados pueden derivarse de un `TreeSet` mantenido en paralelo o construirse bajo demanda con `new TreeSet<>(destinos)`. Elegir una opción y justificarla.

### 7.2 Operaciones públicas

**Paquetes**

| Método | Descripción |
|---|---|
| `void registrarPaquete(Paquete p)` | Valida código único; agrega a todas las estructuras (lista, mapa, destinos, agrupación, cola FIFO, cola de prioridad) |
| `Paquete consultarPaquete(String codigo)` | Devuelve el paquete o `null`/`Optional` si no existe |
| `boolean existePaquete(String codigo)` | Consulta eficiente por código |
| `List<Paquete> listarPendientesEnOrdenDeRegistro()` | Copia en orden de llegada |
| `Set<String> municipiosConPaquetes()` | Sin repetidos |
| `SortedSet<String>` / `List<String> municipiosOrdenados()` | Orden alfabético sin repetidos |
| `Set<Paquete> paquetesPorMunicipio(String destino)` | Paquetes de un municipio |
| `Map<String, Set<Paquete>> agrupacionPorMunicipio()` | Vista de solo lectura de la relación completa |

**Atención y despacho**

| Método | Descripción |
|---|---|
| `Paquete atenderPorLlegada()` | `poll` de la cola FIFO |
| `Paquete siguienteDespachoPrioritario()` | `poll` de la `PriorityQueue` (se puede invocar repetidamente) |
| `Paquete verSiguientePrioritario()` | `peek` sin extraer |

Atender un paquete con cualquiera de las dos modalidades **no** lo marca como entregado; solo indica cuál es el siguiente a procesar.

**Ordenamientos temporales (sobre copias)**

| Método | Criterio |
|---|---|
| `List<Paquete> ordenarNatural()` | Código ascendente (`Comparable`) |
| `List<Paquete> ordenarPorPrioridad()` | `ComparadorPrioridad` |
| `List<Paquete> ordenarPorPeso()` | `ComparadorPeso` |
| `List<Paquete> ordenarPorTiempo()` | `ComparadorTiempo` |

Siempre se ordena una **copia**; la lista de registro nunca se reordena.

**Análisis (delegan a `algoritmos`)**

| Método | Algoritmo |
|---|---|
| `double pesoTotalPorMunicipio(String destino)` | Recursivo |
| `int contarPrioridadMinima(int minima)` | Recursivo |
| `Paquete buscarBinariaPorCodigo(String codigo)` | Búsqueda binaria sobre copia ordenada por código |
| `Paquete paqueteMayorPeso()` | Divide y vencerás |

**Repartidores y asignación**

| Método | Descripción |
|---|---|
| `void registrarRepartidor(Repartidor r)` | Valida identificación única |
| `Repartidor consultarRepartidor(String id)` | Consulta |
| `List<Repartidor> listarRepartidores()` | Listado |
| `void asignarPaquete(String codigoPaquete, String idRepartidor)` | Valida que el repartidor esté **disponible** y que su **zona == destino** del paquete; marca el repartidor como no disponible y guarda la asignación |
| `Repartidor asignarAutomaticamente(String codigoPaquete)` | Busca un repartidor **disponible** de la zona del destino; error claro si no hay ninguno |

**Entregas e historial**

| Método | Descripción |
|---|---|
| `void registrarEntrega(String codigoPaquete)` | Quita el paquete de **todas** las estructuras de pendientes, lo agrega al final del historial propio y libera al repartidor asignado (si lo había) |
| `Coleccion<Paquete> historialEntregas()` | Devuelve la lista propia para recorrerla con for-each |

### 7.3 Consistencia de estructuras

Un paquete puede estar a la vez en lista, mapa, agrupación, cola FIFO y cola de prioridad. Al **entregar** (o al ser retirado), debe eliminarse de todas. Costos a tener presentes:

- `ArrayList.remove(Object)` y `PriorityQueue.remove(Object)`: O(n).
- `ArrayDeque.remove(Object)`: O(n).

Es aceptable para el alcance del parcial, pero debe poder justificarse. Si se usa extracción por `poll` en alguna cola, el paquete debe retirarse también de las demás estructuras para evitar estados inconsistentes.

---

## 8. Reglas de negocio y validaciones

| # | Regla | Dónde se valida |
|---|---|---|
| 1 | Código de paquete único | `CentroDistribucion.registrarPaquete` (`containsKey`) |
| 2 | Identificación de repartidor única | `CentroDistribucion.registrarRepartidor` |
| 3 | Peso `> 0` | Constructor de `Paquete` |
| 4 | Prioridad en el rango válido (nota 1) | Constructor de `Paquete` |
| 5 | Tiempo estimado `> 0` | Constructor de `Paquete` |
| 6 | Destino no nulo ni vacío | Constructor de `Paquete` |
| 7 | Repartidor no disponible no recibe asignación | `asignarPaquete` |
| 8 | Zona del repartidor == destino del paquete | `asignarPaquete` |
| 9 | Atención por llegada respeta el orden de registro | `Queue` FIFO |
| 10 | Despacho prioritario: prioridad → tiempo → código | `ComparadorDespacho` |
| 11 | Historial conserva el orden real de entrega | `ListaEnlazada.agregar` al final |
| 12 | Historial usa la lista propia | Atributo `ListaEnlazada<Paquete>` |
| 13 | Colección propia genérica e `Iterable` | `Coleccion<T> extends Iterable<T>` |
| 14 | Operaciones recursivas sin ciclos | `AlgoritmosRecursivos` |
| 15 | Divide y vencerás con etapas visibles | `DivideYVenceras` |

**Notas sobre ambigüedades del enunciado**

1. **Rango de prioridad:** el enunciado dice `0–5` en la descripción del atributo y `1–5` en las reglas. Se recomienda definir `PRIORIDAD_MIN` y `PRIORIDAD_MAX` como constantes (lo más seguro es `1–5`, que es la regla explícita y la que aparece en el ejemplo) y confirmar con el docente.
2. **Asignación vs. entrega:** el enunciado no define cuándo un repartidor vuelve a estar disponible. Se propone: al asignar queda no disponible y al registrar la entrega vuelve a estar disponible.
3. **Ejemplo de peso total (14.0 kg para Armenia):** corresponde al subconjunto de 4 paquetes del ejemplo; con los 30 paquetes completos el resultado será otro.

---

## 9. Datos iniciales (`datos.DatosIniciales`)

Mínimos exigidos: **30 paquetes**, **≥ 5 destinos**, **≥ 6 repartidores**, prioridades y pesos variados, códigos no consecutivos y varios paquetes por municipio.

### 9.1 Paquetes sugeridos (en este orden de llegada)

| # | Código | Destino | Peso (kg) | Prior. | Tiempo (min) |
|---|---|---|---|---|---|
| 1 | PQ731 | Armenia | 8.5 | 5 | 45 |
| 2 | PQ105 | Salento | 3.2 | 5 | 25 |
| 3 | PQ942 | Armenia | 12.0 | 4 | 20 |
| 4 | PQ318 | Calarcá | 5.1 | 2 | 35 |
| 5 | PQ567 | Salento | 7.4 | 3 | 40 |
| 6 | PQ829 | Armenia | 4.5 | 3 | 30 |
| 7 | PQ420 | Montenegro | 18.7 | 3 | 30 |
| 8 | PQ101 | Armenia | 4.5 | 1 | 15 |
| 9 | PQ203 | Salento | 3.0 | 4 | 50 |
| 10 | PQ415 | Armenia | 7.2 | 5 | 20 |
| 11 | PQ730 | Armenia | 2.3 | 2 | 25 |
| 12 | PQ214 | Quimbaya | 6.8 | 3 | 55 |
| 13 | PQ421 | Calarcá | 9.3 | 5 | 25 |
| 14 | PQ842 | Circasia | 1.5 | 4 | 40 |
| 15 | PQ156 | Montenegro | 14.2 | 1 | 60 |
| 16 | PQ377 | Quimbaya | 2.8 | 5 | 25 |
| 17 | PQ493 | Filandia | 10.0 | 2 | 70 |
| 18 | PQ584 | Calarcá | 3.9 | 4 | 20 |
| 19 | PQ612 | Circasia | 5.6 | 3 | 45 |
| 20 | PQ659 | La Tebaida | 11.4 | 5 | 30 |
| 21 | PQ703 | Filandia | 0.8 | 3 | 65 |
| 22 | PQ768 | Armenia | 6.1 | 4 | 20 |
| 23 | PQ815 | La Tebaida | 15.5 | 2 | 50 |
| 24 | PQ877 | Montenegro | 4.0 | 5 | 35 |
| 25 | PQ901 | Quimbaya | 13.3 | 4 | 20 |
| 26 | PQ936 | Calarcá | 2.1 | 3 | 30 |
| 27 | PQ968 | Salento | 9.9 | 1 | 45 |
| 28 | PQ974 | Circasia | 7.7 | 2 | 35 |
| 29 | PQ988 | Filandia | 5.0 | 4 | 55 |
| 30 | PQ995 | La Tebaida | 3.6 | 3 | 30 |

Aspectos que ya cubre este conjunto:

- **Empates para probar el desempate por tiempo y código:** PQ584, PQ768, PQ901 y PQ942 comparten prioridad 4 y tiempo 20 → orden esperado `PQ584, PQ768, PQ901, PQ942`. PQ415 (P5, 20 min) precede a PQ105, PQ377 y PQ421 (P5, 25 min).
- **Mayor peso:** PQ420 (18.7 kg), coincide con el ejemplo del enunciado.
- **Destinos:** 8 municipios (Armenia, Salento, Calarcá, Montenegro, Quimbaya, Circasia, Filandia, La Tebaida).

### 9.2 Repartidores sugeridos (7)

| Identificación | Nombre | Zona | Disponible |
|---|---|---|---|
| R001 | (nombre) | Armenia | Sí |
| R002 | (nombre) | Armenia | **No** |
| R003 | (nombre) | Salento | Sí |
| R004 | (nombre) | Calarcá | Sí |
| R005 | (nombre) | Montenegro | Sí |
| R006 | (nombre) | Quimbaya | Sí |
| R007 | (nombre) | Circasia | Sí |

Deliberadamente **no hay repartidor para Filandia ni La Tebaida** y hay uno no disponible, para poder demostrar los casos de error de asignación.

---

## 10. Interfaz y demostración (`Main`)

`Main` debe permitir demostrar **cada** operación implementada. Se recomienda:

1. **Demostración automática** (ejecuta todo el recorrido sin intervención), y
2. **Menú de consola opcional** con las operaciones principales.

### 10.1 Guion de demostración mínimo

1. Cargar datos iniciales.
2. Intentar registrar un paquete con código duplicado → error esperado.
3. Intentar registrar paquetes inválidos (peso `≤ 0`, prioridad fuera de rango, destino vacío, tiempo `≤ 0`) → errores esperados.
4. Mostrar pendientes en orden de registro.
5. Consultar un paquete existente y uno inexistente por código.
6. Mostrar municipios (sin repetidos) y municipios ordenados alfabéticamente.
7. Mostrar la agrupación municipio → paquetes.
8. Atender 4 paquetes por orden de llegada.
9. Despachar repetidamente por prioridad hasta mostrar los desempates.
10. Mostrar ordenamientos: natural, por prioridad, por peso y por tiempo (y confirmar que el orden natural no cambia).
11. Peso total de un municipio (recursivo) y conteo por prioridad mínima (recursivo), con traza.
12. Búsqueda binaria sobre la lista ordenada por código (encontrado / no encontrado / precondición incumplida).
13. Paquete de mayor peso por divide y vencerás, con traza.
14. Asignaciones: exitosa, repartidor no disponible, zona incorrecta, municipio sin repartidor.
15. Registrar varias entregas y recorrer el historial con `for-each`.
16. Pruebas de la lista propia: vacía, un elemento, eliminar primero / intermedio / último / inexistente, obtener por posición e iterador.

---

## 11. Convenciones de código

| Aspecto | Convención |
|---|---|
| Paquetes | minúsculas: `co.quindioexpress.modelo` |
| Clases e interfaces | `PascalCase` |
| Métodos y variables | `camelCase` |
| Constantes | `MAYUSCULAS_CON_GUION_BAJO` |
| Idioma | Identificadores y comentarios en español, de forma consistente |
| Declaraciones | Tipos por interfaz (`List`, `Map`, `Queue`), no por implementación |
| Javadoc | En clases públicas y métodos con precondiciones o complejidad relevante |
| Excepciones | `ReglaNegocioException` para reglas; no capturar excepciones para ocultarlas |
| Inmutabilidad | `Paquete` inmutable; `Repartidor` solo mutable en `disponible` |
| Complejidad | Documentar en comentario la complejidad de los algoritmos clave |

---

## 12. Lista de verificación contra el enunciado (Parte B)

- [ ] Clases para representar los elementos del problema
- [ ] Registro y consulta de paquetes
- [ ] Registro y consulta de repartidores
- [ ] Validación de códigos e identificaciones únicas
- [ ] Orden de llegada conservado
- [ ] Consulta eficiente por código (`HashMap`)
- [ ] Municipios sin repetir (`Set`)
- [ ] Municipios ordenados (`TreeSet`)
- [ ] Agrupación de paquetes por destino (`Map<String, Set<Paquete>>`)
- [ ] Atención por orden de llegada (`Queue`)
- [ ] Procesamiento por prioridad (`PriorityQueue`)
- [ ] Orden natural con `Comparable`
- [ ] Criterios alternativos con `Comparator`
- [ ] Uso justificado de List, Set, Map, Queue/Deque, PriorityQueue
- [ ] Clase genérica (`ListaEnlazada<T>`)
- [ ] Interfaz genérica (`Coleccion<T>`)
- [ ] Lista simplemente enlazada propia (sin colecciones de Java por dentro)
- [ ] `Iterable` e `Iterator` implementados y usados
- [ ] Historial de entregas con la estructura propia
- [ ] Algoritmos recursivos sin ciclos (peso total por municipio; conteo por prioridad mínima)
- [ ] Búsqueda binaria con precondición de orden
- [ ] Divide y vencerás con etapas identificables (mayor peso)
- [ ] Asignación a repartidores según las reglas
- [ ] 30+ paquetes, 5+ destinos, 6+ repartidores, códigos no consecutivos
- [ ] Demostración de todas las operaciones en `Main`
- [ ] Repositorio con `src/` y `README.md`; aportes visibles de todos los integrantes

---

## 13. Prohibiciones (resumen)

- No reemplazar la lista enlazada propia por `ArrayList`, `LinkedList`, `Vector`, `Stack`, `Queue` o `Deque`.
- No usar `for`, `while` ni `do-while` en los métodos que deben ser recursivos (ni streams como atajo).
- No aplicar búsqueda binaria sobre datos sin ordenar.
- No exponer nodos ni referencias internas de la lista enlazada.
- No usar archivos, bases de datos ni temas fuera del alcance del curso (por ejemplo, árboles propios, grafos o concurrencia).
