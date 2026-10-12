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
 *
 * <p>Unifica los módulos del Integrante 2 en una única puerta de entrada,
 * coordinando los subcomponentes especializados:</p>
 * <ul>
 *   <li>{@link GestorColeccionesPaquetes}: consultas rápidas e indexación.</li>
 *   <li>{@link GestorDespachoPrioritario}: despacho por prioridad.</li>
 *   <li>{@link ServicioAsignacionRepartidor}: registro y asignación de repartidores.</li>
 * </ul>
 * <p>Además integra el módulo del Integrante 1 ({@link HistorialEntregas}) para
 * dejar constancia de las entregas procesadas.</p>
 *
 * <p>No reimplementa lógica: solo coordina los subcomponentes y expone las
 * operaciones de la capa de servicio.</p>
 */
public class CentroDistribucion {

    private final GestorColeccionesPaquetes gestorColecciones;
    private final GestorDespachoPrioritario gestorDespacho;
    private final ServicioAsignacionRepartidor servicioRepartidores;
    private final HistorialEntregas historialEntregas;

    /**
     * Cola FIFO de paquetes pendientes de atención por orden de llegada.
     * Es una estructura de procesamiento independiente del registro de
     * auditoría (el {@code ordenLlegada} del gestor de colecciones).
     */
    private final Queue<Paquete> colaLlegada;

    public CentroDistribucion() {
        this.gestorColecciones = new GestorColeccionesPaquetes();
        this.gestorDespacho = new GestorDespachoPrioritario();
        this.servicioRepartidores = new ServicioAsignacionRepartidor();
        this.historialEntregas = new HistorialEntregas();
        this.colaLlegada = new ArrayDeque<>();
    }

    /**
     * Carga los datos iniciales de prueba (paquetes y repartidores) desde
     * {@link GeneradorDatosIniciales}.
     */
    public void cargarDatosIniciales() {
        for (Repartidor repartidor : GeneradorDatosIniciales.obtenerRepartidoresIniciales()) {
            servicioRepartidores.registrarRepartidor(repartidor);
        }
        for (Paquete paquete : GeneradorDatosIniciales.obtenerPaquetesIniciales()) {
            registrarNuevoPaquete(paquete);
        }
    }

    /**
     * Registra un paquete en las colecciones de consulta rápida, en la cola
     * FIFO de atención por orden de llegada y en la cola prioritaria de despacho.
     *
     * @param paquete paquete a registrar.
     * @return {@code true} si se registró; {@code false} si es nulo o si ya
     *         existe un paquete con el mismo código.
     */
    public boolean registrarNuevoPaquete(Paquete paquete) {
        if (paquete == null) {
            return false;
        }
        if (!gestorColecciones.registrarPaquete(paquete)) {
            return false; // código duplicado: no se encola
        }
        gestorDespacho.encolarPaquete(paquete);
        colaLlegada.offer(paquete);
        return true;
    }

    /**
     * Atiende el siguiente paquete respetando el orden de llegada (FIFO),
     * extrayéndolo de la {@link Queue} de atención.
     *
     * <p>Atender un paquete no lo marca como entregado y no altera el registro
     * de auditoría del gestor de colecciones; solo lo retira de la cola de
     * pendientes por orden de llegada.</p>
     *
     * @return el siguiente paquete en orden de llegada, o {@code null} si ya no
     *         quedan paquetes por atender.
     */
    public Paquete atenderPorOrdenLlegada() {
        return colaLlegada.poll();
    }

    /**
     * Despacha el paquete de mayor prioridad según las reglas de la cola
     * prioritaria (prioridad ↓, tiempo ↑, código ↑).
     *
     * @return el siguiente paquete a despachar, o {@code null} si no hay pendientes.
     */
    public Paquete despacharPorPrioridad() {
        return gestorDespacho.despacharSiguiente();
    }

    /**
     * Procesa la asignación y entrega de un paquete: busca un repartidor idóneo,
     * realiza la asignación y, si tiene éxito, registra el paquete en el
     * historial de entregas (módulo del Integrante 1).
     *
     * @param paquete paquete a procesar.
     * @return {@code true} si se encontró repartidor y la asignación fue válida;
     *         {@code false} en caso contrario.
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
        historialEntregas.registrarEntrega(paquete);
        return true;
    }

    // --- Repartidores ---

    /**
     * Registra un repartidor delegando en el servicio de asignación.
     *
     * @return {@code true} si se registró; {@code false} si es nulo o ya existe.
     */
    public boolean registrarRepartidor(Repartidor repartidor) {
        return servicioRepartidores.registrarRepartidor(repartidor);
    }

    /**
     * Busca un repartidor disponible cuya zona coincida con el destino del paquete.
     */
    public Repartidor buscarRepartidorDisponiblePara(Paquete paquete) {
        return servicioRepartidores.buscarRepartidorDisponiblePara(paquete);
    }

    /**
     * Asigna un paquete a un repartidor validando disponibilidad y zona.
     */
    public boolean realizarAsignacion(Paquete paquete, Repartidor repartidor) {
        return servicioRepartidores.realizarAsignacion(paquete, repartidor);
    }

    /**
     * @return lista (en orden de registro) con los repartidores registrados.
     */
    public List<Repartidor> obtenerRepartidores() {
        return servicioRepartidores.obtenerRepartidores();
    }

    // --- Operaciones de consulta ---

    public Paquete buscarPorCodigo(String codigo) {
        return gestorColecciones.buscarPorCodigo(codigo);
    }

    public Set<String> obtenerMunicipiosUnicos() {
        return gestorColecciones.obtenerMunicipiosUnicos();
    }

    public SortedSet<String> obtenerMunicipiosOrdenados() {
        return gestorColecciones.obtenerMunicipiosOrdenados();
    }

    public Set<Paquete> obtenerPaquetesPorMunicipio(String municipio) {
        return gestorColecciones.obtenerPaquetesPorMunicipio(municipio);
    }

    public Map<String, Set<Paquete>> obtenerAgrupacionPorMunicipio() {
        return gestorColecciones.obtenerAgrupacionPorMunicipio();
    }

    public List<Paquete> obtenerOrdenLlegada() {
        return gestorColecciones.obtenerOrdenLlegada();
    }

    public int cantidadPendientesDespacho() {
        return gestorDespacho.cantidadPendientes();
    }

    public int totalPaquetesRegistrados() {
        return gestorColecciones.totalPaquetesRegistrados();
    }

    public int totalRepartidores() {
        return servicioRepartidores.totalRepartidores();
    }

    public int totalEntregas() {
        return historialEntregas.totalEntregas();
    }

    public HistorialEntregas getHistorialEntregas() {
        return historialEntregas;
    }
}
