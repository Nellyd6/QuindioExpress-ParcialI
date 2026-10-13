package com.quindioexpress.consola;

import com.quindioexpress.modelo.Paquete;
import com.quindioexpress.servicios.CentroDistribucion;

import java.util.ArrayList;
import java.util.Collections;
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
        double total = centro.calcularPesoTotalRecursivo(centro.obtenerOrdenLlegada(), municipio, 0);
        System.out.println("  Peso total de " + municipio + ": " + total + " kg");
    }

    /** Conteo recursivo de paquetes con prioridad mayor o igual a un mínimo. */
    public void contarPrioridad() {
        int minima = entrada.leerEntero("\nPrioridad mínima: ");
        if (minima == Integer.MIN_VALUE) return;
        int cantidad = centro.contarPaquetesPorPrioridadRecursivo(centro.obtenerOrdenLlegada(), minima, 0);
        System.out.println("  Paquetes con prioridad >= " + minima + ": " + cantidad);
    }

    /** Búsqueda binaria de un código sobre una copia ordenada. */
    public void busquedaBinaria() {
        String codigo = entrada.leerTexto("\nCódigo a buscar (búsqueda binaria): ");
        if (codigo == null) return;
        List<Paquete> ordenada = new ArrayList<>(centro.obtenerOrdenLlegada());
        Collections.sort(ordenada);
        int posicion = centro.busquedaBinariaPorCodigo(ordenada, codigo, 0, ordenada.size() - 1);
        if (posicion >= 0) {
            System.out.println("  Encontrado en la posición " + posicion + ": " + ordenada.get(posicion));
        } else {
            System.out.println("  No se encontró el código " + codigo + ".");
        }
    }

    /** Paquete de mayor peso mediante divide y vencerás. */
    public void mayorPeso() {
        List<Paquete> paquetes = centro.obtenerOrdenLlegada();
        if (paquetes.isEmpty()) {
            System.out.println("  No hay paquetes registrados.");
            return;
        }
        Paquete masPesado = centro.encontrarMasPesadoDivideYVenceras(paquetes, 0, paquetes.size() - 1);
        System.out.println("\n  Paquete de mayor peso: " + masPesado.getCodigo()
                + " (" + masPesado.getPeso() + " kg, " + masPesado.getDestino() + ")");
    }
}
