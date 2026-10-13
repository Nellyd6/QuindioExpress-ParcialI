# QuindíoExpress — Parcial I (Parte B: Construcción)

Aplicación de consola en Java que gestiona una jornada de un centro de
distribución: registro y consulta de paquetes y repartidores, atención por orden
de llegada o por prioridad, agrupación por municipio, ordenamientos con
`Comparable`/`Comparator`, algoritmos recursivos, búsqueda binaria y divide y
vencerás, asignación a repartidores e historial de entregas sobre una lista
enlazada propia.

Los datos iniciales se definen directamente en Java (sin archivos ni base de
datos).

## Entorno necesario

- Java 17 o superior (probado con OpenJDK 22).
- Sin librerías externas.

## Cómo compilar y ejecutar

```bash
# Compilar (desde la raíz del proyecto)
javac -encoding UTF-8 -d out $(find src -name "*.java")

# Ejecutar
java -cp out com.quindioexpress.principal.Main
```

En Windows/PowerShell:

```powershell
$javac = "ruta\a\jdk\bin\javac.exe"
& $javac -encoding UTF-8 -d out (Get-ChildItem -Recurse -Filter *.java -Path src).FullName
& "ruta\a\jdk\bin\java.exe" -cp out com.quindioexpress.principal.Main
```

Al iniciar se cargan los datos iniciales y se muestra un menú interactivo. La
opción `1` ejecuta una demostración automática completa.

## Menú

```
 1. Demostración automática completa
 2. Listar paquetes en orden de llegada
 3. Registrar paquete nuevo
 4. Consultar paquete por código (HashMap O(1))
 5. Ver municipios (únicos y ordenados)
 6. Ver agrupación de paquetes por municipio
 7. Ver paquetes de un municipio
 8. Atender por orden de llegada (FIFO)
 9. Despachar por prioridad (PriorityQueue)
10. Ver repartidores
11. Registrar repartidor nuevo
12. Asignar y entregar paquete por código
13. Ver historial de entregas
14. Peso total de un municipio (recursivo)
15. Contar paquetes por prioridad mínima (recursivo)
16. Búsqueda binaria por código
17. Paquete de mayor peso (divide y vencerás)
18. Consultar repartidor por identificación (HashMap O(1))
19. Ver ordenamientos: natural, prioridad, peso y tiempo (Comparator)
20. Demostrar lista enlazada propia (casos límite)
21. Ver ejemplos exactos del enunciado
 0. Salir
```

## Funcionalidad implementada

### Registro y consulta
- Registro de paquetes con prioridad por defecto 3 cuando no se indica; se
  rechazan códigos duplicados y datos inválidos (peso, prioridad, tiempo y
  destino vacío).
- El orden exacto de registro se conserva: cada alta se agrega al final sin
  alterar las posiciones previas.
- Consulta de paquete por código y de repartidor por identificación en tiempo
  constante (O(1)).
- Municipios únicos sin repetir y municipios ordenados alfabéticamente.
- Agrupación por municipio: cada destino mantiene su conjunto de paquetes.

### Atención y despacho
- Atención FIFO por orden de llegada (primero en llegar, primero en atenderse).
- Despacho prioritario: mayor prioridad primero; desempates por tiempo estimado
  (menor primero) y luego por código.
- El orden de despacho esperado (`PQ105, PQ731, PQ942, PQ420`) queda verificado
  por la demostración automática y por la opción 21.

### Repartidores y entregas
- Registro de repartidores con identificación única.
- Asignación a un repartidor libre cuya zona coincide con el destino del
  paquete; los no disponibles quedan excluidos.
- Entrega: el paquete se retira de todas las estructuras de pendientes, se
  registra en el historial y el repartidor vuelve a quedar disponible.
- Historial de entregas en orden real de entrega, recorrible con `for-each` y
  construido sobre la lista enlazada propia.

### Ordenamientos
- Orden natural por código mediante `Comparable`.
- Criterios alternativos reutilizables mediante `Comparator`: prioridad (de
  mayor a menor), peso (de mayor a menor) y tiempo (de menor a mayor).
- Los ordenamientos se aplican siempre sobre copias: la lista de registro nunca
  se reordena.

### Algoritmos
- Recursivos sin ciclos: peso total de un municipio y conteo de paquetes con
  prioridad mínima, con traza de ejecución.
- Búsqueda binaria de un código sobre la copia ordenada, con traza.
- Divide y vencerás para hallar el paquete de mayor peso, con traza.

### Estructura propia
- `Coleccion<T>`: interfaz genérica (`Iterable`) que define el contrato.
- `ListaEnlazada<T>`: lista simplemente enlazada con nodo interno privado,
  construida sin usar las colecciones de Java.
- Los casos límite están demostrados en la opción 20 (o en `DemoListaPropia`):
  lista vacía, un solo elemento, eliminar primero/intermedio/último/inexistente,
  posición inválida e iterador sin elementos.

## Estructura del proyecto

```
src/main/java/com/quindioexpress/
├── principal/
│   └── Main.java                      Punto de entrada (carga datos e inicia el menú)
├── consola/                           Interfaz de consola (menú separado por dominio)
│   ├── Accion.java                    Interfaz funcional de las opciones
│   ├── Opcion.java                    record (número, descripción, acción)
│   ├── EntradaConsola.java            Lectura y validación de entrada
│   ├── MenuPrincipal.java             Registra las opciones y ejecuta el bucle
│   ├── MenuPaquetes.java              Listar / registrar / consultar paquetes
│   ├── MenuMunicipios.java            Municipios, agrupación y paquetes por municipio
│   ├── MenuDespacho.java              Atención FIFO y despacho por prioridad
│   ├── MenuRepartidores.java          Repartidores, asignación y historial
│   ├── MenuAlgoritmos.java            Recursivos, búsqueda binaria y divide y vencerás
│   ├── MenuOrdenamientos.java         Orden natural y criterios alternativos
│   ├── DemoAutomatica.java            Demostración automática completa
│   ├── DemoListaPropia.java           Casos límite de la lista propia
│   └── DemoEjemplosExactos.java       Ejemplos exactos del enunciado con traza
├── modelo/
│   ├── Paquete.java                   Entidad inmutable; Comparable por código
│   └── Repartidor.java                Entidad (solo cambia la disponibilidad)
├── estructuras/                       Estructura propia genérica
│   ├── Coleccion.java                 Interfaz genérica (Iterable): qué se hace
│   └── ListaEnlazada.java             Lista simplemente enlazada: cómo se hace
├── comparadores/                      Criterios de ordenamiento (Comparator)
│   ├── ComparadorPrioridad.java       Prioridad de mayor a menor
│   ├── ComparadorPeso.java            Peso de mayor a menor
│   ├── ComparadorTiempo.java          Tiempo de menor a mayor
│   └── ComparadorDespacho.java        prioridad → tiempo → código (compuesto)
├── algoritmos/                        Algoritmos independientes del estado (static)
│   ├── AlgoritmosRecursivos.java      pesoTotalPorDestino, contarPrioridadMinima
│   ├── BusquedaBinaria.java           Búsqueda binaria genérica y por código
│   └── DivideYVenceras.java           Paquete de mayor peso (con traza)
├── excepciones/
│   └── ReglaNegocioException.java     Excepción propia de reglas de negocio
├── datos/
│   └── DatosIniciales.java            32 paquetes, 6 municipios, 8 repartidores
└── servicios/                         Lógica de negocio
    ├── CentroDistribucion.java        Fachada central (único punto de entrada)
    ├── GestorColeccionesPaquetes.java List/Set/Map de paquetes
    ├── GestorDespachoPrioritario.java PriorityQueue + ComparadorDespacho
    ├── ServicioAsignacionRepartidor.java  Repartidores y asignaciones
    └── HistorialEntregas.java         Historial con la lista enlazada propia
```

## Estructuras de datos utilizadas

| Necesidad | Estructura |
|---|---|
| Orden de registro | `List<Paquete>` → `ArrayList` |
| Consulta por código / duplicados | `Map<String, Paquete>` → `HashMap` |
| Municipios sin repetir | `Set<String>` → `HashSet` |
| Municipios ordenados | `TreeSet<String>` |
| Paquetes por municipio | `Map<String, Set<Paquete>>` (`LinkedHashSet`) |
| Atención por orden de llegada (FIFO) | `Queue<Paquete>` → `ArrayDeque` |
| Despacho prioritario | `PriorityQueue<Paquete>` + `ComparadorDespacho` |
| Repartidores | `Map<String, Repartidor>` → `LinkedHashMap` |
| Historial de entregas | `ListaEnlazada<Paquete>` (propia, sin colecciones de Java) |

## Ejemplos exactos verificados

La opción `21` (y la demostración automática) comprueba:

- Despacho: `PQ105, PQ731, PQ942, PQ420`.
- Peso recursivo de un subconjunto de Armenia: `14.0 kg`.
- Conteo recursivo con prioridad ≥ 4 sobre `5, 2, 4, 1, 5, 3, 4`: `4`.
- Búsqueda binaria de `PQ731`: posición `5`.
- Divide y vencerás sobre `4.5, 12.0, 3.2, 18.7, 7.4, 5.1`: `18.7 kg`.