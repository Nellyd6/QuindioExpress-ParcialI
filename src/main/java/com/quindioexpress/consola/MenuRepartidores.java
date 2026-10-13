package com.quindioexpress.consola;

import com.quindioexpress.modelo.Paquete;
import com.quindioexpress.modelo.Repartidor;
import com.quindioexpress.servicios.CentroDistribucion;

import java.util.List;

/** Opciones del menú relacionadas con los repartidores y las entregas. */
public class MenuRepartidores {

    private final CentroDistribucion centro;
    private final EntradaConsola entrada;

    public MenuRepartidores(CentroDistribucion centro, EntradaConsola entrada) {
        this.centro = centro;
        this.entrada = entrada;
    }

    /** Lista todos los repartidores registrados. */
    public void ver() {
        List<Repartidor> repartidores = centro.obtenerRepartidores();
        System.out.println("\n-- Repartidores (" + repartidores.size() + ") --");
        for (Repartidor repartidor : repartidores) {
            System.out.println("  " + repartidor);
        }
    }

    /** Registra un repartidor nuevo leyendo sus datos por consola. */
    public void registrar() {
        System.out.println("\n-- Registrar repartidor --");
        String identificacion = entrada.leerTexto("Identificación: ");
        if (identificacion == null) return;
        String nombre = entrada.leerTexto("Nombre: ");
        if (nombre == null) return;
        String zona = entrada.leerTexto("Zona (municipio): ");
        if (zona == null) return;

        try {
            Repartidor repartidor = new Repartidor(identificacion, nombre, zona);
            boolean registrado = centro.registrarRepartidor(repartidor);
            if (registrado) {
                System.out.println("  Repartidor registrado: " + repartidor);
            } else {
                System.out.println("  No se registró (¿identificación duplicada?).");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("  Datos inválidos: " + e.getMessage());
        }
    }

    /** Asigna un repartidor disponible a un paquete y registra la entrega. */
    public void asignarYEntregar() {
        String codigo = entrada.leerTexto("\nCódigo del paquete a asignar y entregar: ");
        if (codigo == null) return;

        Paquete paquete = centro.buscarPorCodigo(codigo);
        if (paquete == null) {
            System.out.println("  No existe un paquete con código " + codigo + ".");
            return;
        }
        Repartidor repartidor = centro.buscarRepartidorDisponiblePara(paquete);
        if (repartidor == null) {
            System.out.println("  No hay repartidor disponible para el municipio " + paquete.getDestino() + ".");
            return;
        }
        boolean entregado = centro.procesarAsignacionYEntrega(paquete);
        if (entregado) {
            System.out.println("  Entregado " + paquete.getCodigo() + " -> " + repartidor.getNombre()
                    + " (" + repartidor.getZonaAsignada() + ").");
            System.out.println("  Total de entregas en el historial: " + centro.totalEntregas());
        } else {
            System.out.println("  No se pudo completar la asignación y entrega.");
        }
    }

    /** Recorre el historial de entregas con for-each. */
    public void verHistorial() {
        System.out.println("\n-- Historial de entregas (" + centro.totalEntregas() + ") --");
        for (Paquete entregado : centro.getHistorialEntregas().getHistorial()) {
            System.out.println("  " + entregado);
        }
    }
}
