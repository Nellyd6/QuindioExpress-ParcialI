package com.quindioexpress.algoritmos;

import com.quindioexpress.excepciones.ReglaNegocioException;
import com.quindioexpress.modelo.Paquete;

import java.util.List;

/**
 * Cálculo del paquete de mayor peso mediante la estrategia divide y vencerás.
 *
 * <p>Etapas identificables: <b>caso base</b> (un solo elemento), <b>división</b>
 * en mitades, <b>subproblema izquierdo</b>, <b>subproblema derecho</b> y
 * <b>combinación</b> de ambos resultados. En caso de empate se conserva el de la
 * izquierda.</p>
 *
 * <p>Complejidad temporal: O(n). Con {@link #TRAZA} se imprime el árbol de
 * divisiones y combinaciones.</p>
 */
public final class DivideYVenceras {

    /** Bandera de traza de ejecución. */
    public static boolean TRAZA = false;

    private DivideYVenceras() {
    }

    /**
     * Sobrecarga de entrada: analiza toda la lista.
     *
     * @throws ReglaNegocioException si la lista es nula o está vacía.
     */
    public static Paquete paqueteMayorPeso(List<Paquete> paquetes) {
        if (paquetes == null || paquetes.isEmpty()) {
            throw new ReglaNegocioException(
                    "No se puede calcular el paquete de mayor peso de una colección vacía o nula.");
        }
        return paqueteMayorPeso(paquetes, 0, paquetes.size() - 1, 0);
    }

    /**
     * Determina el paquete de mayor peso del subarreglo {@code [inicio, fin]}.
     *
     * @param paquetes lista de paquetes (no nula ni vacía).
     * @param inicio   índice inicial (inclusive).
     * @param fin      índice final (inclusive).
     * @return el paquete de mayor peso del rango.
     */
    public static Paquete paqueteMayorPeso(List<Paquete> paquetes, int inicio, int fin) {
        if (paquetes == null || paquetes.isEmpty()) {
            throw new ReglaNegocioException(
                    "No se puede calcular el paquete de mayor peso de una colección vacía o nula.");
        }
        return paqueteMayorPeso(paquetes, inicio, fin, 0);
    }

    private static Paquete paqueteMayorPeso(List<Paquete> paquetes, int inicio, int fin, int profundidad) {
        String sangria = "  ".repeat(Math.max(0, profundidad));

        // Caso base: subproblema de un solo elemento
        if (inicio == fin) {
            Paquete unico = paquetes.get(inicio);
            if (TRAZA) {
                System.out.println(sangria + "caso base [" + inicio + "]: " + unico.getCodigo()
                        + " (" + unico.getPeso() + " kg)");
            }
            return unico;
        }

        // División del problema en dos mitades
        int medio = inicio + (fin - inicio) / 2;
        if (TRAZA) {
            System.out.println(sangria + "dividir [" + inicio + "," + fin + "] en ["
                    + inicio + "," + medio + "] y [" + (medio + 1) + "," + fin + "]");
        }

        // Subproblema izquierdo y subproblema derecho
        Paquete izquierdo = paqueteMayorPeso(paquetes, inicio, medio, profundidad + 1);
        Paquete derecho = paqueteMayorPeso(paquetes, medio + 1, fin, profundidad + 1);

        // Combinación de resultados (empate: izquierda)
        Paquete mayor = izquierdo.getPeso() >= derecho.getPeso() ? izquierdo : derecho;
        if (TRAZA) {
            System.out.println(sangria + "combinar [" + inicio + "," + fin + "]: " + mayor.getCodigo()
                    + " (" + mayor.getPeso() + " kg)");
        }
        return mayor;
    }
}
