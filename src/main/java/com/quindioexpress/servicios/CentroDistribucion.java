package com.quindioexpress.servicios;

import com.quindioexpress.algoritmos.AlgoritmosRecursivos;
import com.quindioexpress.algoritmos.BusquedaBinaria;
import com.quindioexpress.algoritmos.DivideYVenceras;
import com.quindioexpress.comparadores.ComparadorPeso;
import com.quindioexpress.comparadores.ComparadorPrioridad;
import com.quindioexpress.comparadores.ComparadorTiempo;
import com.quindioexpress.datos.DatosIniciales;
import com.quindioexpress.modelo.Paquete;
import com.quindioexpress.modelo.Repartidor;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;

/**
 * Fachada central (capa de servicio) del centro de distribución QuindíoExpress.
 *
 * <p>Es el único punto de entrada del sistema: coordina los gestores de
 * colecciones, de despacho, de repartidores y el historial, sin exponer sus
 * estructuras internas (RT-07).</p>
 */
public class CentroDistribucion {

    // Componentes delegados que manejan el almacenamiento real de datos
    private final GestorColeccionesPaquetes gestorColecciones;
    private final GestorDespachoPrioritario gestorDespacho;
    private final ServicioAsignacionRepartidor servicioRepartidores;
    private final HistorialEntregas historialEntregas;

    /** Cola FIFO de atención por orden de llegada (RE-06); dueño: esta clase. */
    private final Queue<Paquete> colaLlegada;

    /** Asignaciones vigentes: código de paquete -> repartidor ocupado. */
    private final Map<String, Repartidor> asignacionesVigentes;

    /** Constructor que inicializa todos los subcomponentes del sistema. */
    public CentroDistribucion() {
        this.gestorColecciones = new GestorColeccionesPaquetes();
        this.gestorDespacho = new GestorDespachoPrioritario();
        this.servicioRepartidores = new ServicioAsignacionRepartidor();
        this.historialEntregas = new HistorialEntregas();
        this.colaLlegada = new ArrayDeque<>();
        this.asignacionesVigentes = new HashMap<>();
    }

    /** Carga los datos simulados de paquetes y repartidores iniciales. */
    public void cargarDatosIniciales() {
        for (Repartidor repartidor : DatosIniciales.obtenerRepartidoresIniciales()) {
            servicioRepartidores.registrarRepartidor(repartidor);
        }
        for (Paquete paquete : DatosIniciales.obtenerPaquetesIniciales()) {
            registrarNuevoPaquete(paquete);
        }
    }

    /**
     * Registra un paquete nuevo sincronizándolo en todas las colecciones activas.
     *
     * @param paquete paquete a registrar.
     * @throws com.quindioexpress.excepciones.ReglaNegocioException si el código
     *         ya existe (RN-01) o el paquete es nulo.
     */
    public void registrarNuevoPaquete(Paquete paquete) {
        gestorColecciones.registrarPaquete(paquete);
        gestorDespacho.encolarPaquete(paquete);
        colaLlegada.offer(paquete);
    }

    /**
     * Remueve y retorna el siguiente paquete en orden de llegada estricto
     * (RF-13).
     *
     * @return el paquete más antiguo pendiente, o {@code null} si no hay.
     */
    public Paquete atenderPorOrdenLlegada() {
        return colaLlegada.poll();
    }

    /**
     * Extrae el paquete prioritario de acuerdo a las reglas de desempate
     * (RF-14).
     *
     * @return el siguiente paquete a despachar, o {@code null} si no hay.
     */
    public Paquete despacharPorPrioridad() {
        return gestorDespacho.despacharSiguiente();
    }

    /**
     * Asigna un repartidor idóneo al paquete y, si la asignación es válida,
     * registra la entrega completa (RN-07, RN-08, RF-27, RF-28, RF-29, RF-26).
     *
     * @param paquete paquete a asignar y entregar.
     * @return {@code true} si se asignó y entregó; {@code false} si no había un
     *         repartidor válido disponible o la asignación fue rechazada.
     */
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
        asignacionesVigentes.put(paquete.getCodigo(), repartidor);
        registrarEntrega(paquete);
        return true;
    }

    /**
     * Registra la entrega de un paquete ya asignado: lo retira de todas las
     * estructuras de pendientes (RF-28, RP-08), lo agrega al historial (RF-29) y
     * libera al repartidor que lo tenía asignado (RF-26).
     *
     * @param paquete paquete entregado.
     * @return {@code true} si el paquete estaba registrado como pendiente.
     */
    public boolean registrarEntrega(Paquete paquete) {
        if (paquete == null) {
            return false;
        }
        boolean retirado = gestorColecciones.eliminarPaquete(paquete);
        colaLlegada.remove(paquete);
        gestorDespacho.eliminar(paquete);
        historialEntregas.registrarEntrega(paquete);

        Repartidor repartidor = asignacionesVigentes.remove(paquete.getCodigo());
        if (repartidor != null) {
            repartidor.setDisponible(true);
        }
        return retirado;
    }

    /**
     * Registra un nuevo repartidor validando que su identificación no exista
     * (RN-02).
     *
     * @throws com.quindioexpress.excepciones.ReglaNegocioException si la
     *         identificación ya existe o el repartidor es nulo.
     */
    public void registrarRepartidor(Repartidor repartidor) {
        servicioRepartidores.registrarRepartidor(repartidor);
    }

    /**
     * Busca un repartidor libre asignado a la zona destino del paquete (RN-08).
     *
     * @return el repartidor disponible, o {@code null} si no hay ninguno.
     */
    public Repartidor buscarRepartidorDisponiblePara(Paquete paquete) {
        return servicioRepartidores.buscarRepartidorDisponiblePara(paquete);
    }

    /**
     * Genera la vinculación operativa entre un paquete y un repartidor
     * (RN-07, RN-08).
     *
     * @return {@code true} si la asignación fue válida.
     */
    public boolean realizarAsignacion(Paquete paquete, Repartidor repartidor) {
        return servicioRepartidores.realizarAsignacion(paquete, repartidor);
    }

    /** @return la lista con los repartidores del centro. */
    public List<Repartidor> obtenerRepartidores() {
        return servicioRepartidores.obtenerRepartidores();
    }

    /**
     * Recupera un repartidor por su identificación en O(1) (RF-24).
     *
     * @return el repartidor encontrado, o {@code null} si no existe.
     */
    public Repartidor consultarRepartidor(String idRepartidor) {
        return servicioRepartidores.buscarPorId(idRepartidor);
    }

    /**
     * Recupera un paquete por coincidencia directa de código en O(1)
     * (RF-06, RF-08).
     *
     * @return el paquete encontrado, o {@code null} si no existe.
     */
    public Paquete buscarPorCodigo(String codigo) {
        return gestorColecciones.buscarPorCodigo(codigo);
    }

    /** @return los nombres de municipios sin duplicados (RF-09). */
    public Set<String> obtenerMunicipiosUnicos() {
        return gestorColecciones.obtenerMunicipiosUnicos();
    }

    /** @return las zonas ordenadas alfabéticamente y sin repetir (RF-10). */
    public SortedSet<String> obtenerMunicipiosOrdenados() {
        return gestorColecciones.obtenerMunicipiosOrdenados();
    }

    /** @return los paquetes asociados a un municipio (RF-11). */
    public Set<Paquete> obtenerPaquetesPorMunicipio(String municipio) {
        return gestorColecciones.obtenerPaquetesPorMunicipio(municipio);
    }

    /** @return la agrupación municipio -> conjunto de paquetes (RF-12). */
    public Map<String, Set<Paquete>> obtenerAgrupacionPorMunicipio() {
        return gestorColecciones.obtenerAgrupacionPorMunicipio();
    }

    /** @return la lista de paquetes en orden exacto de registro (RF-04). */
    public List<Paquete> obtenerOrdenLlegada() {
        return gestorColecciones.obtenerOrdenLlegada();
    }

    // ------------------------------------------------------------------
    // ORDENAMIENTOS: siempre se ordena una copia; la lista de registro
    // original nunca se reordena (RF-21, RA-12, prohibición #5).
    // ------------------------------------------------------------------

    /** Orden natural de Paquete (código ascendente) mediante Comparable (RF-17). */
    public List<Paquete> ordenarNatural() {
        List<Paquete> copia = new ArrayList<>(gestorColecciones.obtenerOrdenLlegada());
        Collections.sort(copia);
        return copia;
    }

    /** Criterio alternativo: prioridad de mayor a menor (RF-18). */
    public List<Paquete> ordenarPorPrioridad() {
        List<Paquete> copia = new ArrayList<>(gestorColecciones.obtenerOrdenLlegada());
        copia.sort(new ComparadorPrioridad());
        return copia;
    }

    /** Criterio alternativo: peso de mayor a menor (RF-19). */
    public List<Paquete> ordenarPorPeso() {
        List<Paquete> copia = new ArrayList<>(gestorColecciones.obtenerOrdenLlegada());
        copia.sort(new ComparadorPeso());
        return copia;
    }

    /** Criterio alternativo: tiempo estimado de menor a mayor (RF-20). */
    public List<Paquete> ordenarPorTiempo() {
        List<Paquete> copia = new ArrayList<>(gestorColecciones.obtenerOrdenLlegada());
        copia.sort(new ComparadorTiempo());
        return copia;
    }

    // ------------------------------------------------------------------
    // ALGORITMOS (delegan en el paquete independiente `algoritmos`, RT-08).
    // ------------------------------------------------------------------

    /**
     * Suma recursiva de los pesos de un municipio sobre los paquetes pendientes
     * (RF-32, RA-01).
     */
    public double pesoTotalPorMunicipio(String municipio) {
        return AlgoritmosRecursivos.pesoTotalPorDestino(gestorColecciones.obtenerOrdenLlegada(), municipio);
    }

    /**
     * Conteo recursivo de paquetes con prioridad mayor o igual a un mínimo
     * (RF-33, RA-02).
     */
    public int contarPrioridadMinima(int prioridadMinima) {
        return AlgoritmosRecursivos.contarPrioridadMinima(gestorColecciones.obtenerOrdenLlegada(), prioridadMinima);
    }

    /**
     * Búsqueda binaria de un código sobre una copia ordenada por código
     * (RF-34, RA-08, RA-12).
     *
     * @return la posición en la copia ordenada, o {@code -1} si no existe.
     */
    public int buscarBinariaPorCodigo(String codigo) {
        return BusquedaBinaria.buscarPorCodigo(ordenarNatural(), codigo);
    }

    /**
     * Paquete de mayor peso de los pendientes mediante divide y vencerás
     * (RF-35, RA-14).
     *
     * @throws com.quindioexpress.excepciones.ReglaNegocioException si no hay paquetes.
     */
    public Paquete paqueteMayorPeso() {
        return DivideYVenceras.paqueteMayorPeso(gestorColecciones.obtenerOrdenLlegada());
    }

    /**
     * Activa o desactiva la traza de ejecución de todos los algoritmos
     * (RP-06, RA-06).
     */
    public void setTraza(boolean activa) {
        AlgoritmosRecursivos.TRAZA = activa;
        BusquedaBinaria.TRAZA = activa;
        DivideYVenceras.TRAZA = activa;
    }

    /** @return cuántos paquetes quedan pendientes de despacho prioritario. */
    public int cantidadPendientesDespacho() {
        return gestorDespacho.cantidadPendientes();
    }

    /** @return la cantidad total de paquetes pendientes registrados. */
    public int totalPaquetesRegistrados() {
        return gestorColecciones.totalPaquetesRegistrados();
    }

    /** @return la cantidad de repartidores registrados. */
    public int totalRepartidores() {
        return servicioRepartidores.totalRepartidores();
    }

    /** @return la dimensión actual del historial de entregas. */
    public int totalEntregas() {
        return historialEntregas.totalEntregas();
    }

    /** @return el historial basado en la lista propia. */
    public HistorialEntregas getHistorialEntregas() {
        return historialEntregas;
    }
}
