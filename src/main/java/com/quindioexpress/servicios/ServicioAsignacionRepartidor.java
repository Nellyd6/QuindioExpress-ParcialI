package com.quindioexpress.servicios;

import com.quindioexpress.modelo.Paquete;
import com.quindioexpress.modelo.Repartidor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lógica de negocio para emparejar paquetes listos para despacho con los
 * repartidores disponibles.
 *
 * <p>Mantiene un registro de repartidores en un {@link Map} indexado por el
 * identificador del repartidor, lo que permite validar la unicidad de IDs y
 * resolver búsquedas por identificación en tiempo O(1).</p>
 *
 * <p><b>Reglas de negocio validadas:</b></p>
 * <ul>
 *   <li>No pueden existir dos repartidores con la misma identificación.</li>
 *   <li>Un repartidor no disponible ({@code disponible == false}) no puede
 *       recibir asignaciones.</li>
 *   <li>La zona principal del repartidor debe coincidir exactamente con el
 *       municipio destino del paquete asignado.</li>
 * </ul>
 */
public class ServicioAsignacionRepartidor {

    /**
     * Registro de repartidores indexado por identificación. Se usa
     * {@link LinkedHashMap} para conservar el orden de registro y así garantizar
     * un resultado determinista al buscar un repartidor disponible.
     */
    private final Map<String, Repartidor> repartidoresPorId;

    public ServicioAsignacionRepartidor() {
        this.repartidoresPorId = new LinkedHashMap<>();
    }

    /**
     * Registra un repartidor validando que su identificación sea única.
     *
     * @param repartidor repartidor a registrar.
     * @return {@code true} si se registró; {@code false} si es nulo o si ya
     *         existe un repartidor con la misma identificación.
     */
    public boolean registrarRepartidor(Repartidor repartidor) {
        if (repartidor == null) {
            return false;
        }
        if (repartidoresPorId.containsKey(repartidor.getIdRepartidor())) {
            return false;
        }
        repartidoresPorId.put(repartidor.getIdRepartidor(), repartidor);
        return true;
    }

    /**
     * Busca un repartidor disponible cuya zona coincida con el destino del
     * paquete.
     *
     * @param paquete paquete para el que se busca repartidor.
     * @return el primer repartidor disponible de la zona correspondiente, o
     *         {@code null} si no hay ninguno.
     */
    public Repartidor buscarRepartidorDisponiblePara(Paquete paquete) {
        if (paquete == null) {
            return null;
        }
        for (Repartidor repartidor : repartidoresPorId.values()) {
            if (repartidor.isDisponible()
                    && repartidor.getZonaAsignada().equals(paquete.getDestino())) {
                return repartidor;
            }
        }
        return null;
    }

    /**
     * Realiza la asignación de un paquete a un repartidor validando las reglas
     * de negocio.
     *
     * <p>La asignación es válida si el repartidor está registrado, se encuentra
     * disponible y su zona coincide exactamente con el destino del paquete. En
     * caso exitoso, el repartidor pasa a estado no disponible.</p>
     *
     * @param paquete    paquete a asignar.
     * @param repartidor repartidor candidato.
     * @return {@code true} si la asignación fue exitosa; {@code false} si se
     *         incumple alguna regla.
     */
    public boolean realizarAsignacion(Paquete paquete, Repartidor repartidor) {
        if (paquete == null || repartidor == null) {
            return false;
        }

        // El repartidor debe estar registrado en el servicio
        Repartidor registrado = repartidoresPorId.get(repartidor.getIdRepartidor());
        if (registrado == null || registrado != repartidor) {
            return false;
        }

        // Regla: no puede recibir asignaciones si no está disponible
        if (!repartidor.isDisponible()) {
            return false;
        }

        // Regla: la zona principal debe coincidir con el destino del paquete
        if (!repartidor.getZonaAsignada().equals(paquete.getDestino())) {
            return false;
        }

        // Asignación válida: el repartidor queda ocupado
        repartidor.setDisponible(false);
        return true;
    }

    /**
     * Busca un repartidor registrado por su identificación en tiempo O(1).
     *
     * @param idRepartidor identificación a buscar.
     * @return el repartidor encontrado, o {@code null} si no existe.
     */
    public Repartidor buscarPorId(String idRepartidor) {
        return repartidoresPorId.get(idRepartidor);
    }

    /**
     * @return lista (en orden de registro) con los repartidores registrados.
     */
    public List<Repartidor> obtenerRepartidores() {
        return new ArrayList<>(repartidoresPorId.values());
    }

    /**
     * @return cantidad de repartidores registrados.
     */
    public int totalRepartidores() {
        return repartidoresPorId.size();
    }
}
