# QuindíoExpress — Parcial I (Parte B: Construcción)

Aplicación de consola en Java que gestiona una jornada de un centro de
distribución: registro y consulta de paquetes y repartidores, atención por orden
de llegada o por prioridad, agrupación por municipio, ordenamientos con
`Comparable`/`Comparator`, algoritmos recursivos, búsqueda binaria y divide y
vencerás, asignación a repartidores e historial de entregas sobre una lista
enlazada propia.

Los datos iniciales se definen directamente en Java (sin archivos ni base de
datos).

## Requisitos

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
│   ├── DemoListaPropia.java           Casos límite de la lista propia (RE-24 a RE-31)
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
│   └── DivideYVenceras.java            Paquete de mayor peso (con traza)
├── excepciones/
│   └── ReglaNegocioException.java      Excepción propia de reglas de negocio
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

## Cobertura de requisitos (Parte B)

### Funcionales (RF)

- [x] RF-01/02/03 Registro de paquete con prioridad por defecto 3 y rechazo por código duplicado
- [x] RF-04/05 Orden exacto de registro conservado (altas sin alterar posiciones)
- [x] RF-06/07/08 Consulta por código en O(1) sin recorrer todos los paquetes
- [x] RF-09/10 Municipios únicos (`HashSet`) y ordenados (`TreeSet`)
- [x] RF-11/12 Relación municipio → conjunto de paquetes
- [x] RF-13/14/15 Atención FIFO y despacho prioritario repetible
- [x] RF-16 Ejemplo de despacho verificado (PQ105, PQ731, PQ942, PQ420)
- [x] RF-17 Orden natural por código (`Comparable`)
- [x] RF-18/19/20 Orden por prioridad, peso y tiempo (`Comparator`)
- [x] RF-21/22 Criterios alternativos reutilizables sobre copias
- [x] RF-23/24 Registro y consulta de repartidores
- [x] RF-25/26 Asignación según reglas y repartidor no disponible excluido
- [x] RF-27/28/29 Entrega: retira de pendientes y agrega al historial
- [x] RF-30/31 Historial ordenado y recorrible con `for-each`
- [x] RF-32 a RF-35 Recursivos, búsqueda binaria y divide y vencerás

### Reglas de negocio (RN)

- [x] RN-01/02 Duplicados de código/identificación → `ReglaNegocioException`
- [x] RN-03/04/05/06 Validaciones de peso, prioridad, tiempo y destino
- [x] RN-07/08 Asignación rechazada si no disponible o zona distinta
- [x] RN-09 a RN-16 Orden de llegada, desempates, historial con lista propia genérica e `Iterator`, recursividad y DyV

### Estructuras (RE)

- [x] RE-01 a RE-08 JCF justificado (List, Map, Set, TreeSet, Queue, PriorityQueue)
- [x] RE-09 a RE-11 Interfaz genérica `Coleccion<T>` (qué) separada de `ListaEnlazada<T>` (cómo)
- [x] RE-12 a RE-23 Lista simplemente enlazada genérica, nodo privado, `Iterable`/`Iterator`
- [x] RE-24 a RE-31 Casos límite demostrados en la opción 20 (o `DemoListaPropia`)
- [x] RE-32 a RE-39 `Comparable` coherente y comparadores reutilizables (incluido el de despacho)

### Algoritmos (RA)

- [x] RA-01 a RA-07 Recursivos sin ciclos, con caso base, avance y traza
- [x] RA-08 a RA-13 Búsqueda binaria sobre copia ordenada con precondición verificada
- [x] RA-14 a RA-22 Divide y vencerás con caso base, división, subproblemas, combinación y manejo de vacío/nulo

### Datos, ejecución, técnicos y repositorio

- [x] RD-01 a RD-09 30+ paquetes, 5+ destinos, 6+ repartidores, variedad y empates
- [x] RD-10 Repartidor no disponible y municipio sin repartidor (Filandia)
- [x] RP-01 a RP-09 Compila y ejecuta; demo por secciones con casos de éxito y error; trazas; casos límite de la lista; entrega sin pendientes; menú repetible
- [x] RT-01 a RT-14 Solo temas del curso, declaración por interfaz, `Paquete` inmutable, excepción propia, paquetes por responsabilidad, dueño único por estructura, algoritmos independientes, convenciones y Javadoc
- [x] RR-01/02 `src/` con todo el código y `README.md`

## Ejemplos exactos verificados

La opción `21` (y la demostración automática) comprueba:

- Despacho: `PQ105, PQ731, PQ942, PQ420`.
- Peso recursivo de un subconjunto de Armenia: `14.0 kg`.
- Conteo recursivo con prioridad ≥ 4 sobre `5, 2, 4, 1, 5, 3, 4`: `4`.
- Búsqueda binaria de `PQ731`: posición `5`.
- Divide y vencerás sobre `4.5, 12.0, 3.2, 18.7, 7.4, 5.1`: `18.7 kg`.
