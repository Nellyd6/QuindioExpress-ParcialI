package com.quindioexpress.servicios;

import com.quindioexpress.comparadores.ComparadorDespacho;
import com.quindioexpress.modelo.Paquete;

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
     * Elimina un paquete concreto de la cola prioritaria.
     *
     * <p>Se usa cuando un paquete ya fue entregado y no debe seguir siendo un
     * despacho pendiente.</p>
     *
     * @param paquete paquete a eliminar.
     * @return {@code true} si estaba en la cola y se eliminó; {@code false} en caso contrario.
     */
    public boolean eliminar(Paquete paquete) {
        return paquete != null && colaPrioritaria.remove(paquete);
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
}
