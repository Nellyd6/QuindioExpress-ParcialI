package com.quindioexpress.servicios;
import com.quindioexpress.estructuras.ListaEnlazadaPropia;
import com.quindioexpress.modelo.Paquete;

/**
 * Gestiona el registro histórico de las entregas realizadas utilizando
 * la lista enlazada propia (sin colecciones nativas de Java).
 */
public class HistorialEntregas {
    private ListaEnlazadaPropia<Paquete> historial;

    public HistorialEntregas() {
        this.historial = new ListaEnlazadaPropia<>();
    }

    /**
     * Registra un paquete entregado al final de la lista.
     */
    public void registrarEntrega(Paquete paquete) {
        if (paquete != null) {
            historial.agregar(paquete);
        }
    }

    /**
     * Muestra o retorna el total de entregas realizadas.
     */
    public int totalEntregas() {
        return historial.tamano();
    }

    /**
     * Permite consultar un paquete entregado según su posición en el historial.
     */
    public Paquete obtenerEntrega(int indice) {
        return historial.obtener(indice);
    }

    /**
     * Valida si el historial está vacío.
     */
    public boolean estaVacio() {
        return historial.estaVacia();
    }

    /**
     * Permite recorrer el historial usando la interfaz Iterable.
     */
    public ListaEnlazadaPropia<Paquete> getHistorial() {
        return historial;
    }
}
