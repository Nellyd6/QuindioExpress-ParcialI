package com.quindioexpress.consola;

import com.quindioexpress.excepciones.ReglaNegocioException;
import com.quindioexpress.modelo.Paquete;
import com.quindioexpress.servicios.CentroDistribucion;

import java.util.List;

/** Opciones del menú que ejercitan los algoritmos (recursivos, binaria, DyV). */
public class MenuAlgoritmos {

    private final CentroDistribucion centro;
    private final EntradaConsola entrada;

    public MenuAlgoritmos(CentroDistribucion centro, EntradaConsola entrada) {
        this.centro = centro;
        this.entrada = entrada;
    }

    /** Suma recursiva de los pesos de un municipio. */
    public void pesoTotal() {
        String municipio = entrada.leerTexto("\nMunicipio para sumar pesos: ");
        if (municipio == null) return;
        try {
            double total = centro.pesoTotalPorMunicipio(municipio);
            System.out.println("  Peso total de " + municipio + ": " + total + " kg");
        } catch (ReglaNegocioException e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    /** Conteo recursivo de paquetes con prioridad mayor o igual a un mínimo. */
    public void contarPrioridad() {
        int minima = entrada.leerEntero("\nPrioridad mínima: ");
        if (minima == Integer.MIN_VALUE) return;
        try {
            int cantidad = centro.contarPrioridadMinima(minima);
            System.out.println("  Paquetes con prioridad >= " + minima + ": " + cantidad);
        } catch (ReglaNegocioException e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    /** Búsqueda binaria de un código sobre una copia ordenada. */
    public void busquedaBinaria() {
        String codigo = entrada.leerTexto("\nCódigo a buscar (búsqueda binaria): ");
        if (codigo == null) return;
        try {
            List<Paquete> ordenada = centro.ordenarNatural();
            int posicion = centro.buscarBinariaPorCodigo(codigo);
            if (posicion >= 0) {
                System.out.println("  Encontrado en la posición " + posicion + ": " + ordenada.get(posicion));
            } else {
                System.out.println("  No se encontró el código " + codigo + ".");
            }
        } catch (ReglaNegocioException e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    /** Paquete de mayor peso mediante divide y vencerás. */
    public void mayorPeso() {
        try {
            Paquete masPesado = centro.paqueteMayorPeso();
            System.out.println("\n  Paquete de mayor peso: " + masPesado.getCodigo()
                    + " (" + masPesado.getPeso() + " kg, " + masPesado.getDestino() + ")");
        } catch (ReglaNegocioException e) {
            System.out.println("\n  Error: " + e.getMessage());
        }
    }
}
