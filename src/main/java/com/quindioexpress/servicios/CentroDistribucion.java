package com.quindioexpress.servicios;

import com.quindioexpress.modelo.Paquete;
import com.quindioexpress.modelo.Repartidor;
import com.quindioexpress.util.GeneradorDatosIniciales;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;

/**
 * Fachada central (capa de servicio) del centro de distribución QuindíoExpress.
 */
public class CentroDistribucion {

    // Componentes delegados que manejan el almacenamiento real de datos
    private final GestorColeccionesPaquetes gestorColecciones;
    private final GestorDespachoPrioritario gestorDespacho;
    private final ServicioAsignacionRepartidor servicioRepartidores;
    private final HistorialEntregas historialEntregas;
    private final Queue<Paquete> colaLlegada;

    // Constructor que inicializa todos los subcomponentes del sistema
    public CentroDistribucion() {
        this.gestorColecciones = new GestorColeccionesPaquetes();
        this.gestorDespacho = new GestorDespachoPrioritario();
        this.servicioRepartidores = new ServicioAsignacionRepartidor();
        this.historialEntregas = new HistorialEntregas();
        this.colaLlegada = new ArrayDeque<>();
    }

    // Carga los datos simulados de paquetes y repartidores iniciales
    public void cargarDatosIniciales() {
        for (Repartidor repartidor : GeneradorDatosIniciales.obtenerRepartidoresIniciales()) {
            servicioRepartidores.registrarRepartidor(repartidor);
        }
        for (Paquete paquete : GeneradorDatosIniciales.obtenerPaquetesIniciales()) {
            registrarNuevoPaquete(paquete);
        }
    }

    // Registra un paquete nuevo sincronizándolo en todas las colecciones activas
    public boolean registrarNuevoPaquete(Paquete paquete) {
        if (paquete == null) {
            return false;
        }
        if (!gestorColecciones.registrarPaquete(paquete)) {
            return false;
        }
        gestorDespacho.encolarPaquete(paquete);
        colaLlegada.offer(paquete);
        return true;
    }

    // Remueve y retorna el siguiente paquete en orden de llegada estricto
    public Paquete atenderPorOrdenLlegada() {
        return colaLlegada.poll();
    }

    // Extrae el paquete prioritario de acuerdo a las reglas de desempate
    public Paquete despacharPorPrioridad() {
        return gestorDespacho.despacharSiguiente();
    }

    // Asigna un repartidor idóneo al paquete y lo traslada al historial final
    public boolean procesarAsignacionYEntrega(Paquete paquete) {
        if (paquete == null) {
            return false;
        }
        Repartidor repartidor = servicioRepartidores.buscarRepartidorDisponiblePara(paquete);
        if (repartidor == null) {
            return false;
        }
        if (!servicioRepartidores.realizarAsignacion(paquete, repartidor)) {
            return false;
        }
        historialEntregas.registrarEntrega(paquete);
        return true;
    }

    // Registra un nuevo conductor validando que no esté previamente ingresado
    public boolean registrarRepartidor(Repartidor repartidor) {
        return servicioRepartidores.registrarRepartidor(repartidor);
    }

    // Busca un repartidor libre asignado a la zona destino del paquete
    public Repartidor buscarRepartidorDisponiblePara(Paquete paquete) {
        return servicioRepartidores.buscarRepartidorDisponiblePara(paquete);
    }

    // Genera la vinculación operativa entre un paquete y un repartidor
    public boolean realizarAsignacion(Paquete paquete, Repartidor repartidor) {
        return servicioRepartidores.realizarAsignacion(paquete, repartidor);
    }

    // Retorna la lista con los conductores del centro
    public List<Repartidor> obtenerRepartidores() {
        return servicioRepartidores.obtenerRepartidores();
    }

    // Recupera un paquete por coincidencia directa de código en O(1)
    public Paquete buscarPorCodigo(String codigo) {
        return gestorColecciones.buscarPorCodigo(codigo);
    }

    // Retorna los nombres de municipios sin registrar duplicados
    public Set<String> obtenerMunicipiosUnicos() {
        return gestorColecciones.obtenerMunicipiosUnicos();
    }

    // Retorna las zonas ordenadas bajo criterio alfabético nativo
    public SortedSet<String> obtenerMunicipiosOrdenados() {
        return gestorColecciones.obtenerMunicipiosOrdenados();
    }

    // Filtra y retorna los paquetes asociados a una zona particular
    public Set<Paquete> obtenerPaquetesPorMunicipio(String municipio) {
        return gestorColecciones.obtenerPaquetesPorMunicipio(municipio);
    }

    // Agrupa y mapea los conjuntos de paquetes organizados por municipio
    public Map<String, Set<Paquete>> obtenerAgrupacionPorMunicipio() {
        return gestorColecciones.obtenerAgrupacionPorMunicipio();
    }

    // Obtiene el registro de auditoría lineal del flujo de paquetes
    public List<Paquete> obtenerOrdenLlegada() {
        return gestorColecciones.obtenerOrdenLlegada();
    }

    // Cuenta cuántos elementos restan dentro de la cola prioritaria
    public int cantidadPendientesDespacho() {
        return gestorDespacho.cantidadPendientes();
    }

    // Retorna la cantidad total de registros indexados
    public int totalPaquetesRegistrados() {
        return gestorColecciones.totalPaquetesRegistrados();
    }

    // Retorna la cantidad de conductores ingresados en el sistema
    public int totalRepartidores() {
        return servicioRepartidores.totalRepartidores();
    }

    // Retorna la dimensión actual de la lista enlazada a mano
    public int totalEntregas() {
        return historialEntregas.totalEntregas();
    }

    // Obtiene la instancia del historial basada en la lista propia
    public HistorialEntregas getHistorialEntregas() {
        return historialEntregas;
    }

    // Suma pesos de un municipio de forma recursiva sin usar ciclos
    public double calcularPesoTotalRecursivo(List<Paquete> lista, String municipio, int indice) {
        // Caso Base: Detener el avance si alcanzamos el limite de la coleccion
        if (indice == lista.size()) {
            return 0.0;
        }
        Paquete paqueteActual = lista.get(indice);
        // Validar si el destino coincide ignorando diferencias de escritura
        if (paqueteActual.getDestino().equalsIgnoreCase(municipio)) {
            return paqueteActual.getPeso() + calcularPesoTotalRecursivo(lista, municipio, indice + 1);
        } else {
            return calcularPesoTotalRecursivo(lista, municipio, indice + 1);
        }
    }

    // Cuenta paquetes con prioridad mayor o igual de forma recursiva
    public int contarPaquetesPorPrioridadRecursivo(List<Paquete> lista, int prioridadMinima, int indice) {
        // Caso Base: Parar cuando ya no queden casillas por verificar
        if (indice == lista.size()) {
            return 0;
        }
        Paquete paqueteActual = lista.get(indice);
        // Sumar uno al retorno acumulado si iguala o supera el limite
        if (paqueteActual.getPrioridad() >= prioridadMinima) {
            return 1 + contarPaquetesPorPrioridadRecursivo(lista, prioridadMinima, indice + 1);
        } else {
            return contarPaquetesPorPrioridadRecursivo(lista, prioridadMinima, indice + 1);
        }
    }

    // Busqueda binaria operando sobre una particion de datos ordenados
    public int busquedaBinariaPorCodigo(List<Paquete> lista, String codigoBuscado, int izquierda, int derecha) {
        // Caso Base: Los indices cruzados indican que el codigo no existe
        if (izquierda > derecha) {
            return -1;
        }
        int mitad = (izquierda + derecha) / 2;
        Paquete paqueteMitad = lista.get(mitad);
        int resultadoComparacion = paqueteMitad.getCodigo().compareTo(codigoBuscado);

        // Caso Base: Encontrado en el punto medio exacto
        if (resultadoComparacion == 0) {
            return mitad;
        }
        // Descartar mitades evaluando alfabeticamente el resultado
        if (resultadoComparacion > 0) {
            return busquedaBinariaPorCodigo(lista, codigoBuscado, izquierda, mitad - 1);
        } else {
            return busquedaBinariaPorCodigo(lista, codigoBuscado, mitad + 1, derecha);
        }
    }

    // Divide y venceras para encontrar el paquete con mayor peso
    public Paquete encontrarMasPesadoDivideYVenceras(List<Paquete> lista, int inicio, int fin) {
        // Caso Base: Subproblema de un solo elemento es el mas pesado de si mismo
        if (inicio == fin) {
            return lista.get(inicio);
        }
        int mitad = (inicio + fin) / 2;
        // Dividir el arreglo y conquistar obteniendo los dos maximos parciales
        Paquete maxIzquierdo = encontrarMasPesadoDivideYVenceras(lista, inicio, mitad);
        Paquete maxDerecho = encontrarMasPesadoDivideYVenceras(lista, mitad + 1, fin);

        // Combinar comparando pesos para retornar el mayor de ambos
        if (maxIzquierdo.getPeso() >= maxDerecho.getPeso()) {
            return maxIzquierdo;
        } else {
            return maxDerecho;
        }
    }
}
