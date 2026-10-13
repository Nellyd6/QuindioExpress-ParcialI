package com.quindioexpress.algoritmos;

import com.quindioexpress.excepciones.ReglaNegocioException;
import com.quindioexpress.modelo.Paquete;

import java.util.List;

/**
 * Búsqueda binaria sobre colecciones ordenadas por orden natural.
 *
 * <p><b>Precondición:</b> la lista debe estar ordenada ascendentemente. Se
 * verifica con {@link #estaOrdenada(List)} y se lanza
 * {@link ReglaNegocioException} si no se cumple.</p>
 *
 * <p>Complejidad temporal: O(log n). La lista debe ordenarse siempre sobre una
 * copia; la lista de registro nunca se reordena.</p>
 */
public final class BusquedaBinaria {

    /** Bandera de traza de ejecución. */
    public static boolean TRAZA = false;

    private BusquedaBinaria() {
    }

    /**
     * @return {@code true} si la lista está ordenada ascendentemente por orden natural.
     */
    public static <T extends Comparable<T>> boolean estaOrdenada(List<T> lista) {
        if (lista == null) {
            throw new ReglaNegocioException("La lista no puede ser nula.");
        }
        for (int i = 1; i < lista.size(); i++) {
            if (lista.get(i - 1).compareTo(lista.get(i)) > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Búsqueda binaria genérica.
     *
     * @param ordenada lista ordenada ascendentemente.
     * @param objetivo elemento a buscar (no nulo).
     * @return el índice del elemento, o {@code -1} si no existe.
     * @throws ReglaNegocioException si la lista es nula o no está ordenada.
     */
    public static <T extends Comparable<T>> int buscar(List<T> ordenada, T objetivo) {
        if (ordenada == null) {
            throw new ReglaNegocioException("La lista de búsqueda no puede ser nula.");
        }
        if (objetivo == null) {
            throw new ReglaNegocioException("El elemento a buscar no puede ser nulo.");
        }
        if (!estaOrdenada(ordenada)) {
            throw new ReglaNegocioException("La lista debe estar ordenada ascendentemente para la búsqueda binaria.");
        }
        return buscar(ordenada, objetivo, 0, ordenada.size() - 1);
    }

    private static <T extends Comparable<T>> int buscar(List<T> ordenada, T objetivo, int izquierda, int derecha) {
        if (izquierda > derecha) {
            if (TRAZA) {
                System.out.println("buscar -> no encontrado");
            }
            return -1;
        }
        int mitad = izquierda + (derecha - izquierda) / 2;
        int comparacion = ordenada.get(mitad).compareTo(objetivo);
        if (TRAZA) {
            System.out.println("buscar[" + izquierda + "," + derecha + "] mitad=" + mitad + " cmp=" + comparacion);
        }
        if (comparacion == 0) {
            return mitad;
        }
        if (comparacion > 0) {
            return buscar(ordenada, objetivo, izquierda, mitad - 1);
        }
        return buscar(ordenada, objetivo, mitad + 1, derecha);
    }

    /**
     * Variante por código sobre una lista de paquetes ordenada por código.
     *
     * @return el índice del paquete con ese código, o {@code -1} si no existe.
     */
    public static int buscarPorCodigo(List<Paquete> ordenada, String codigo) {
        if (ordenada == null) {
            throw new ReglaNegocioException("La lista de búsqueda no puede ser nula.");
        }
        if (codigo == null) {
            throw new ReglaNegocioException("El código a buscar no puede ser nulo.");
        }
        if (!estaOrdenada(ordenada)) {
            throw new ReglaNegocioException("La lista debe estar ordenada ascendentemente para la búsqueda binaria.");
        }
        return buscarPorCodigo(ordenada, codigo, 0, ordenada.size() - 1);
    }

    private static int buscarPorCodigo(List<Paquete> ordenada, String codigo, int izquierda, int derecha) {
        if (izquierda > derecha) {
            return -1;
        }
        int mitad = izquierda + (derecha - izquierda) / 2;
        int comparacion = ordenada.get(mitad).getCodigo().compareTo(codigo);
        if (comparacion == 0) {
            return mitad;
        }
        if (comparacion > 0) {
            return buscarPorCodigo(ordenada, codigo, izquierda, mitad - 1);
        }
        return buscarPorCodigo(ordenada, codigo, mitad + 1, derecha);
    }
}
