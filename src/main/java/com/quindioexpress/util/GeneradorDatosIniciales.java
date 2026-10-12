package com.quindioexpress.util;

import com.quindioexpress.modelo.Paquete;
import com.quindioexpress.modelo.Repartidor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Construye y suministra los datos iniciales de prueba para el sistema de
 * gestión de QuindíoExpress.
 *
 * <p>Todos los datos cumplen estrictamente las reglas de negocio del dominio
 * (peso &gt; 0, prioridad entre 1 y 5, tiempo estimado entero positivo, códigos
 * e identificadores únicos). Antes de retornar, cada método ejecuta
 * validaciones preventivas que lanzan {@link IllegalStateException} si algún
 * dato violara dichas reglas, evitando propagar información inconsistente.</p>
 */
public final class GeneradorDatosIniciales {

    /** Mínimo de paquetes iniciales exigido por el enunciado. */
    private static final int MIN_PAQUETES = 30;

    /** Mínimo de municipios destino distintos exigido por el enunciado. */
    private static final int MIN_MUNICIPIOS = 5;

    /** Mínimo de repartidores iniciales exigido por el enunciado. */
    private static final int MIN_REPARTIDORES = 6;

    /** Constructor privado: clase utilitaria, no instanciable. */
    private GeneradorDatosIniciales() {
    }

    /**
     * Devuelve la lista de paquetes iniciales de prueba.
     *
     * <p>Características de los datos generados:</p>
     * <ul>
     *   <li>32 paquetes (supera el mínimo de 30).</li>
     *   <li>6 municipios destino diferentes (supera el mínimo de 5).</li>
     *   <li>Códigos no consecutivos y sin repetir (patrón {@code PQ###}).</li>
     *   <li>Pesos mayores a 0 kg, prioridades entre 1 y 5 y tiempos enteros
     *       positivos.</li>
     *   <li>Distribución desbalanceada por municipio para probar
     *       agrupamientos, e empates de prioridad/tiempo para ejercitar las
     *       reglas de desempate de la cola de prioridad.</li>
     * </ul>
     *
     * @return lista mutable con los paquetes iniciales.
     */
    public static List<Paquete> obtenerPaquetesIniciales() {
        List<Paquete> paquetes = new ArrayList<>();

        // --- Armenia (8 paquetes) ---
        paquetes.add(new Paquete("PQ731", "Armenia", 3.2, 5, 2));
        paquetes.add(new Paquete("PQ105", "Armenia", 1.8, 3, 4));
        paquetes.add(new Paquete("PQ942", "Armenia", 7.5, 2, 6));
        paquetes.add(new Paquete("PQ318", "Armenia", 0.9, 4, 3));
        paquetes.add(new Paquete("PQ557", "Armenia", 12.0, 5, 5));
        paquetes.add(new Paquete("PQ204", "Armenia", 2.4, 1, 8));
        paquetes.add(new Paquete("PQ863", "Armenia", 5.6, 3, 2));
        paquetes.add(new Paquete("PQ490", "Armenia", 4.1, 4, 3));

        // --- Calarcá (6 paquetes) ---
        paquetes.add(new Paquete("PQ276", "Calarcá", 6.3, 5, 2));
        paquetes.add(new Paquete("PQ619", "Calarcá", 2.2, 2, 5));
        paquetes.add(new Paquete("PQ884", "Calarcá", 9.8, 3, 4));
        paquetes.add(new Paquete("PQ037", "Calarcá", 1.1, 4, 3));
        paquetes.add(new Paquete("PQ752", "Calarcá", 3.7, 1, 7));
        paquetes.add(new Paquete("PQ468", "Calarcá", 5.0, 5, 6));

        // --- Salento (5 paquetes) ---
        paquetes.add(new Paquete("PQ523", "Salento", 2.9, 5, 2));
        paquetes.add(new Paquete("PQ190", "Salento", 4.4, 2, 6));
        paquetes.add(new Paquete("PQ807", "Salento", 8.1, 3, 5));
        paquetes.add(new Paquete("PQ361", "Salento", 1.6, 4, 4));
        paquetes.add(new Paquete("PQ945", "Salento", 6.7, 1, 9));

        // --- Filandia (5 paquetes) ---
        paquetes.add(new Paquete("PQ412", "Filandia", 3.3, 3, 4));
        paquetes.add(new Paquete("PQ678", "Filandia", 7.2, 5, 3));
        paquetes.add(new Paquete("PQ259", "Filandia", 0.7, 2, 5));
        paquetes.add(new Paquete("PQ736", "Filandia", 5.9, 4, 3));
        paquetes.add(new Paquete("PQ581", "Filandia", 2.05, 1, 6));

        // --- Quimbaya (4 paquetes) ---
        paquetes.add(new Paquete("PQ394", "Quimbaya", 4.8, 5, 4));
        paquetes.add(new Paquete("PQ820", "Quimbaya", 1.3, 3, 6));
        paquetes.add(new Paquete("PQ147", "Quimbaya", 6.1, 2, 7));
        paquetes.add(new Paquete("PQ605", "Quimbaya", 3.0, 4, 3));

        // --- Montenegro (4 paquetes) ---
        paquetes.add(new Paquete("PQ288", "Montenegro", 2.6, 4, 5));
        paquetes.add(new Paquete("PQ664", "Montenegro", 8.9, 5, 3));
        paquetes.add(new Paquete("PQ471", "Montenegro", 1.9, 2, 6));
        paquetes.add(new Paquete("PQ913", "Montenegro", 5.4, 3, 4));

        validarPaquetes(paquetes);
        return paquetes;
    }

    /**
     * Devuelve la lista de repartidores iniciales de prueba.
     *
     * <p>Características de los datos generados:</p>
     * <ul>
     *   <li>8 repartidores con identificadores únicos (supera el mínimo de 6).</li>
     *   <li>Zonas asignadas correspondientes a los municipios destino usados
     *       por los paquetes.</li>
     *   <li>Estados de disponibilidad variados (algunos disponibles y otros no).</li>
     * </ul>
     *
     * @return lista mutable con los repartidores iniciales.
     */
    public static List<Repartidor> obtenerRepartidoresIniciales() {
        List<Repartidor> repartidores = new ArrayList<>();

        repartidores.add(new Repartidor("REP-01", "Carlos Pérez", "Armenia"));
        repartidores.add(new Repartidor("REP-02", "Ana Gómez", "Calarcá"));
        repartidores.add(new Repartidor("REP-03", "Luis Rojas", "Salento"));
        repartidores.add(new Repartidor("REP-04", "Marta Díaz", "Filandia"));
        repartidores.add(new Repartidor("REP-05", "Jorge Ramírez", "Quimbaya"));
        repartidores.add(new Repartidor("REP-06", "Sofía Herrera", "Montenegro"));
        repartidores.add(new Repartidor("REP-07", "Andrés Castro", "Armenia"));
        repartidores.add(new Repartidor("REP-08", "Paula Ortiz", "Calarcá"));

        // Estados de disponibilidad variados: el constructor inicializa a true,
        // por lo que marcamos explícitamente algunos como no disponibles.
        repartidores.get(1).setDisponible(false); // REP-02
        repartidores.get(4).setDisponible(false); // REP-05
        repartidores.get(6).setDisponible(false); // REP-07

        validarRepartidores(repartidores);
        return repartidores;
    }

    /**
     * Validaciones preventivas sobre la lista de paquetes generada.
     *
     * @param paquetes lista a validar.
     * @throws IllegalStateException si algún dato viola las reglas de negocio.
     */
    private static void validarPaquetes(List<Paquete> paquetes) {
        if (paquetes == null) {
            throw new IllegalStateException("La lista de paquetes iniciales no puede ser nula.");
        }
        if (paquetes.size() < MIN_PAQUETES) {
            throw new IllegalStateException(
                    "Se requieren al menos " + MIN_PAQUETES + " paquetes iniciales, pero se generaron "
                            + paquetes.size() + ".");
        }

        Set<String> codigos = new HashSet<>();
        Set<String> municipios = new HashSet<>();

        for (Paquete paquete : paquetes) {
            if (paquete == null) {
                throw new IllegalStateException("La lista de paquetes contiene un elemento nulo.");
            }
            if (paquete.getCodigo() == null || paquete.getCodigo().trim().isEmpty()) {
                throw new IllegalStateException("Paquete con código vacío.");
            }
            if (!codigos.add(paquete.getCodigo())) {
                throw new IllegalStateException("Código de paquete duplicado: " + paquete.getCodigo() + ".");
            }
            if (paquete.getDestino() == null || paquete.getDestino().trim().isEmpty()) {
                throw new IllegalStateException("Paquete " + paquete.getCodigo() + " sin destino.");
            }
            if (paquete.getPeso() <= 0) {
                throw new IllegalStateException("Paquete " + paquete.getCodigo() + " con peso no válido.");
            }
            if (paquete.getPrioridad() < 1 || paquete.getPrioridad() > 5) {
                throw new IllegalStateException("Paquete " + paquete.getCodigo() + " con prioridad fuera de rango.");
            }
            if (paquete.getTiempoEstimado() <= 0) {
                throw new IllegalStateException("Paquete " + paquete.getCodigo() + " con tiempo estimado no válido.");
            }
            municipios.add(paquete.getDestino());
        }

        if (municipios.size() < MIN_MUNICIPIOS) {
            throw new IllegalStateException(
                    "Se requieren al menos " + MIN_MUNICIPIOS + " municipios destino distintos, pero se generaron "
                            + municipios.size() + ".");
        }
    }

    /**
     * Validaciones preventivas sobre la lista de repartidores generada.
     *
     * @param repartidores lista a validar.
     * @throws IllegalStateException si algún dato viola las reglas de negocio.
     */
    private static void validarRepartidores(List<Repartidor> repartidores) {
        if (repartidores == null) {
            throw new IllegalStateException("La lista de repartidores iniciales no puede ser nula.");
        }
        if (repartidores.size() < MIN_REPARTIDORES) {
            throw new IllegalStateException(
                    "Se requieren al menos " + MIN_REPARTIDORES + " repartidores iniciales, pero se generaron "
                            + repartidores.size() + ".");
        }

        Set<String> ids = new HashSet<>();
        boolean hayDisponibles = false;
        boolean hayNoDisponibles = false;

        for (Repartidor repartidor : repartidores) {
            if (repartidor == null) {
                throw new IllegalStateException("La lista de repartidores contiene un elemento nulo.");
            }
            if (repartidor.getIdRepartidor() == null || repartidor.getIdRepartidor().trim().isEmpty()) {
                throw new IllegalStateException("Repartidor con identificador vacío.");
            }
            if (!ids.add(repartidor.getIdRepartidor())) {
                throw new IllegalStateException(
                        "Identificador de repartidor duplicado: " + repartidor.getIdRepartidor() + ".");
            }
            if (repartidor.getZonaAsignada() == null || repartidor.getZonaAsignada().trim().isEmpty()) {
                throw new IllegalStateException("Repartidor " + repartidor.getIdRepartidor() + " sin zona asignada.");
            }
            if (repartidor.isDisponible()) {
                hayDisponibles = true;
            } else {
                hayNoDisponibles = true;
            }
        }

        if (!hayDisponibles || !hayNoDisponibles) {
            throw new IllegalStateException(
                    "Los repartidores deben tener estados de disponibilidad variados (disponibles y no disponibles).");
        }
    }
}
