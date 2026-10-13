package com.quindioexpress.consola;

import com.quindioexpress.modelo.Paquete;
import com.quindioexpress.servicios.CentroDistribucion;

/** Opciones del menú para la atención y el despacho de paquetes. */
public class MenuDespacho {

    private final CentroDistribucion centro;
    private final EntradaConsola entrada;

    public MenuDespacho(CentroDistribucion centro, EntradaConsola entrada) {
        this.centro = centro;
        this.entrada = entrada;
    }

    /** Atiende paquetes por orden de llegada (cola FIFO). */
    public void atenderFifo() {
        int cantidad = entrada.leerEntero("\n¿Cuántos paquetes atender por orden de llegada? ");
        if (cantidad == Integer.MIN_VALUE) return;
        if (cantidad <= 0) {
            System.out.println("  La cantidad debe ser mayor que 0.");
            return;
        }
        System.out.println("-- Atención FIFO --");
        int atendidos = 0;
        for (int i = 0; i < cantidad; i++) {
            Paquete paquete = centro.atenderPorOrdenLlegada();
            if (paquete == null) break;
            atendidos++;
            System.out.println("  -> " + paquete.getCodigo() + " (" + paquete.getDestino() + ")");
        }
        if (atendidos == 0) {
            System.out.println("  La cola de llegada está vacía.");
        }
    }

    /** Despacha paquetes según la cola de prioridad. */
    public void despacharPrioridad() {
        int cantidad = entrada.leerEntero("\n¿Cuántos paquetes despachar por prioridad? ");
        if (cantidad == Integer.MIN_VALUE) return;
        if (cantidad <= 0) {
            System.out.println("  La cantidad debe ser mayor que 0.");
            return;
        }
        System.out.println("-- Despacho por prioridad --");
        int despachados = 0;
        for (int i = 0; i < cantidad; i++) {
            Paquete paquete = centro.despacharPorPrioridad();
            if (paquete == null) break;
            despachados++;
            System.out.println("  -> " + paquete.getCodigo()
                    + " (Prioridad: " + paquete.getPrioridad()
                    + ", Tiempo: " + paquete.getTiempoEstimado() + ")");
        }
        if (despachados == 0) {
            System.out.println("  La cola prioritaria está vacía.");
        }
        System.out.println("  Pendientes de despacho: " + centro.cantidadPendientesDespacho());
    }
}
