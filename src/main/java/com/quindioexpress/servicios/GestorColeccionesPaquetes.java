package com.quindioexpress.servicios;

import com.quindioexpress.excepciones.ReglaNegocioException;
import com.quindioexpress.modelo.Paquete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Organiza los paquetes registrados en el centro de distribución utilizando las
 * estructuras del Java Collections Framework (JCF) más adecuadas para responder
 * a las consultas frecuentes de la operación.
 *
 * <ul>
 *   <li>{@code ordenLlegada}: {@link List} que conserva exactamente el orden de
 *       registro.</li>
 *   <li>{@code mapaPorCodigo}: {@link HashMap} para búsquedas O(1) por código.</li>
 *   <li>{@code municipiosUnicos}: {@link HashSet} con los destinos sin repetir.</li>
 *   <li>{@code municipiosOrdenados}: {@link TreeSet} con los destinos ordenados
 *       alfabéticamente y sin repetir.</li>
 *   <li>{@code paquetesPorMunicipio}: {@link Map} que agrupa los paquetes por
 *       destino.</li>
 * </ul>
 *
 * <p>Es el <b>único dueño</b> de estas estructuras.</p>
 */
public class GestorColeccionesPaquetes {

    /** Conserva exactamente el orden de registro inicial. */
    private final List<Paquete> ordenLlegada;

    /** Permite búsquedas por código en tiempo O(1). */
    private final Map<String, Paquete> mapaPorCodigo;

    /** Almacena los destinos sin repetidos. */
    private final Set<String> municipiosUnicos;

    /** Almacena los destinos ordenados alfabéticamente y sin repetidos. */
    private final SortedSet<String> municipiosOrdenados;

    /** Agrupa los paquetes por cada municipio de destino. */
    private final Map<String, Set<Paquete>> paquetesPorMunicipio;

    public GestorColeccionesPaquetes() {
        this.ordenLlegada = new ArrayList<>();
        this.mapaPorCodigo = new HashMap<>();
        this.municipiosUnicos = new HashSet<>();
        this.municipiosOrdenados = new TreeSet<>();
        this.paquetesPorMunicipio = new HashMap<>();
    }

    /**
     * Registra un paquete en todas las colecciones del gestor.
     *
     * <p>Valida que el código no exista previamente. Si ya existe, la
     * operación no tiene efecto sobre las estructuras y lanza
     * {@link ReglaNegocioException} con un mensaje claro.</p>
     *
     * @param paquete paquete a registrar.
     * @throws ReglaNegocioException si el paquete es nulo o si ya existe un
     *         paquete con el mismo código.
     */
    public void registrarPaquete(Paquete paquete) {
        if (paquete == null) {
            throw new ReglaNegocioException("No se puede registrar un paquete nulo.");
        }
        if (mapaPorCodigo.containsKey(paquete.getCodigo())) {
            throw new ReglaNegocioException(
                    "Ya existe un paquete registrado con el código " + paquete.getCodigo() + ".");
        }

        // Conserva el orden de llegada
        ordenLlegada.add(paquete);

        // Índice de búsqueda O(1)
        mapaPorCodigo.put(paquete.getCodigo(), paquete);

        // Municipios sin repetidos (HashSet y TreeSet)
        municipiosUnicos.add(paquete.getDestino());
        municipiosOrdenados.add(paquete.getDestino());

        // Agrupación por municipio (sin duplicados, conservando orden de inserción)
        paquetesPorMunicipio
                .computeIfAbsent(paquete.getDestino(), k -> new LinkedHashSet<>())
                .add(paquete);
    }

    /**
     * Elimina un paquete de <b>todas</b> las estructuras de pendientes.
     *
     * <p>Si el municipio del paquete queda sin paquetes, se retira también de los
     * conjuntos de municipios para mantener la coherencia.</p>
     *
     * @param paquete paquete a eliminar.
     * @return {@code true} si el paquete estaba registrado y se eliminó;
     *         {@code false} en caso contrario.
     */
    public boolean eliminarPaquete(Paquete paquete) {
        if (paquete == null) {
            return false;
        }
        if (mapaPorCodigo.remove(paquete.getCodigo()) == null) {
            return false;
        }

        ordenLlegada.remove(paquete);

        Set<Paquete> delMunicipio = paquetesPorMunicipio.get(paquete.getDestino());
        if (delMunicipio != null) {
            delMunicipio.remove(paquete);
            if (delMunicipio.isEmpty()) {
                paquetesPorMunicipio.remove(paquete.getDestino());
                municipiosUnicos.remove(paquete.getDestino());
                municipiosOrdenados.remove(paquete.getDestino());
            }
        }
        return true;
    }

    /**
     * Busca un paquete por su código en tiempo O(1).
     *
     * @param codigo código del paquete a buscar.
     * @return el paquete encontrado, o {@code null} si no existe.
     */
    public Paquete buscarPorCodigo(String codigo) {
        return mapaPorCodigo.get(codigo);
    }

    /**
     * Devuelve la colección de municipios sin duplicados.
     *
     * @return conjunto de municipios únicos (sin orden garantizado).
     */
    public Set<String> obtenerMunicipiosUnicos() {
        return municipiosUnicos;
    }

    /**
     * Devuelve los municipios ordenados alfabéticamente y sin duplicados.
     *
     * @return conjunto ordenado de municipios.
     */
    public SortedSet<String> obtenerMunicipiosOrdenados() {
        return municipiosOrdenados;
    }

    /**
     * Retorna el conjunto de paquetes asociados a un municipio.
     *
     * @param municipio municipio de destino consultado.
     * @return conjunto de paquetes del municipio; conjunto vacío si no hay ninguno.
     */
    public Set<Paquete> obtenerPaquetesPorMunicipio(String municipio) {
        Set<Paquete> paquetes = paquetesPorMunicipio.get(municipio);
        return paquetes != null ? paquetes : new LinkedHashSet<>();
    }

    /**
     * Devuelve los paquetes en el orden exacto en que fueron registrados.
     *
     * @return lista de paquetes en orden de llegada.
     */
    public List<Paquete> obtenerOrdenLlegada() {
        return ordenLlegada;
    }

    /**
     * Devuelve la agrupación completa de paquetes por municipio de destino.
     *
     * @return mapa cuyas claves son municipios y cuyos valores son los conjuntos
     *         de paquetes asociados.
     */
    public Map<String, Set<Paquete>> obtenerAgrupacionPorMunicipio() {
        return paquetesPorMunicipio;
    }

    /**
     * @return cantidad total de paquetes registrados.
     */
    public int totalPaquetesRegistrados() {
        return ordenLlegada.size();
    }
}
