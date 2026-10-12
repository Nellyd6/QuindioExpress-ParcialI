package com.quindioexpress.servicios;

import com.quindioexpress.modelo.Paquete;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Componente para el procesamiento de paquetes prioritarios utilizando una
 * {@link PriorityQueue} del Java Collections Framework.
 *
 * <p>El orden de despacho queda definido por {@link ComparadorDespacho}, que
 * aplica estrictamente las siguientes reglas de desempate, en este orden:</p>
 * <ol>
 *   <li>Mayor prioridad primero (orden descendente: 5 a 1).</li>
 *   <li>Si empatan en prioridad, menor tiempo estimado primero (ascendente).</li>
 *   <li>Si persiste el empate, orden alfabético ascendente por código.</li>
 * </ol>
 */
public class GestorDespachoPrioritario {

    /** Cola interna de despacho prioritario. */
    private final PriorityQueue<Paquete> colaPrioritaria;

    public GestorDespachoPrioritario() {
        this.colaPrioritaria = new PriorityQueue<>(new ComparadorDespacho());
    }

    /**
     * Inserta un paquete en la cola prioritaria.
     *
     * @param paquete paquete a encolar; si es {@code null} no tiene efecto.
     */
    public void encolarPaquete(Paquete paquete) {
        if (paquete != null) {
            colaPrioritaria.add(paquete);
        }
    }

    /**
     * Remueve y retorna el paquete de mayor prioridad según las reglas.
     *
     * @return el siguiente paquete a despachar, o {@code null} si la cola está vacía.
     */
    public Paquete despacharSiguiente() {
        return colaPrioritaria.poll();
    }

    /**
     * Inspecciona el siguiente paquete a despachar sin removerlo.
     *
     * @return el siguiente paquete a despachar, o {@code null} si la cola está vacía.
     */
    public Paquete verSiguiente() {
        return colaPrioritaria.peek();
    }

    /**
     * @return el tamaño actual de la cola (paquetes pendientes de despacho).
     */
    public int cantidadPendientes() {
        return colaPrioritaria.size();
    }

    /**
     * @return {@code true} si no hay paquetes pendientes de despacho prioritario.
     */
    public boolean estaVacia() {
        return colaPrioritaria.isEmpty();
    }

    /**
     * {@link Comparator} personalizado que implementa las reglas de negocio del
     * despacho prioritario de QuindíoExpress.
     */
    public static class ComparadorDespacho implements Comparator<Paquete> {

        @Override
        public int compare(Paquete p1, Paquete p2) {
            if (p1 == p2) {
                return 0;
            }
            if (p1 == null) {
                return 1;
            }
            if (p2 == null) {
                return -1;
            }

            // Regla 1: mayor prioridad primero (orden descendente)
            int comparacionPrioridad = Integer.compare(p2.getPrioridad(), p1.getPrioridad());
            if (comparacionPrioridad != 0) {
                return comparacionPrioridad;
            }

            // Regla 2: menor tiempo estimado primero (orden ascendente)
            int comparacionTiempo = Double.compare(p1.getTiempoEstimado(), p2.getTiempoEstimado());
            if (comparacionTiempo != 0) {
                return comparacionTiempo;
            }

            // Regla 3: orden alfabético ascendente por código
            return p1.getCodigo().compareTo(p2.getCodigo());
        }
    }
}
