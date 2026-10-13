package com.quindioexpress.consola;

import com.quindioexpress.excepciones.ReglaNegocioException;
import com.quindioexpress.modelo.Paquete;
import com.quindioexpress.servicios.CentroDistribucion;

import java.util.List;

/** Opciones del menú relacionadas con los paquetes. */
public class MenuPaquetes {

    private final CentroDistribucion centro;
    private final EntradaConsola entrada;

    public MenuPaquetes(CentroDistribucion centro, EntradaConsola entrada) {
        this.centro = centro;
        this.entrada = entrada;
    }

    /** Lista los paquetes en el orden exacto de llegada. */
    public void listar() {
        List<Paquete> pendientes = centro.obtenerOrdenLlegada();
        System.out.println("\n-- Paquetes en orden de llegada (" + pendientes.size() + ") --");
        if (pendientes.isEmpty()) {
            System.out.println("  (sin paquetes)");
            return;
        }
        for (Paquete paquete : pendientes) {
            System.out.println("  " + paquete);
        }
    }

    /** Registra un paquete nuevo leyendo sus datos por consola. */
    public void registrar() {
        System.out.println("\n-- Registrar paquete --");
        String codigo = entrada.leerTexto("Código: ");
        if (codigo == null) return;
        String destino = entrada.leerTexto("Destino (municipio): ");
        if (destino == null) return;
        double peso = entrada.leerDouble("Peso (kg): ");
        if (Double.isNaN(peso)) return;
        int prioridad = entrada.leerEntero("Prioridad (1-" + Paquete.PRIORIDAD_MAX + ", vacío = "
                + Paquete.PRIORIDAD_POR_DEFECTO + "): ");
        if (prioridad == Integer.MIN_VALUE) return;
        int tiempo = entrada.leerEntero("Tiempo estimado (min): ");
        if (tiempo == Integer.MIN_VALUE) return;

        try {
            Paquete paquete = new Paquete(codigo, destino, peso, prioridad, tiempo);
            centro.registrarNuevoPaquete(paquete);
            System.out.println("  Paquete registrado: " + paquete);
        } catch (ReglaNegocioException e) {
            System.out.println("  Registro rechazado: " + e.getMessage());
        }
    }

    /** Consulta un paquete por su código (HashMap O(1)). */
    public void consultar() {
        String codigo = entrada.leerTexto("\nCódigo a consultar: ");
        if (codigo == null) return;
        Paquete paquete = centro.buscarPorCodigo(codigo);
        if (paquete == null) {
            System.out.println("  No existe un paquete con código " + codigo + ".");
        } else {
            System.out.println("  Encontrado: " + paquete);
        }
    }
}
