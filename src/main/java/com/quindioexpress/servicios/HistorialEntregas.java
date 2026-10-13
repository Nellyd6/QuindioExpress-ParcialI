package com.quindioexpress.servicios;

import com.quindioexpress.estructuras.Coleccion;
import com.quindioexpress.estructuras.ListaEnlazada;
import com.quindioexpress.modelo.Paquete;

/**
 * Gestiona el registro histórico de las entregas realizadas.
 *
 * <p>Usa exclusivamente la {@link ListaEnlazada} propia y expone el
 * historial como {@link Coleccion} para que el código externo lo recorra con
 * {@code for-each} sin conocer la estructura interna.</p>
 */
public class HistorialEntregas {

    private final Coleccion<Paquete> historial;

    public HistorialEntregas() {
        this.historial = new ListaEnlazada<>();
    }

    /** Agrega un paquete entregado al final, conservando el orden real. */
    public void registrarEntrega(Paquete paquete) {
        if (paquete != null) {
            historial.agregar(paquete);
        }
    }

    /** @return cantidad total de entregas registradas. */
    public int totalEntregas() {
        return historial.tamano();
    }

    /**
     * @param indice posición (base 0) en el historial.
     * @return el paquete entregado en esa posición.
     * @throws IndexOutOfBoundsException si la posición es inválida.
     */
    public Paquete obtenerEntrega(int indice) {
        return historial.obtener(indice);
    }

    /** @return {@code true} si no hay entregas registradas. */
    public boolean estaVacio() {
        return historial.estaVacia();
    }

    /** @return el historial como colección propia, recorrible con for-each. */
    public Coleccion<Paquete> getHistorial() {
        return historial;
    }
}
