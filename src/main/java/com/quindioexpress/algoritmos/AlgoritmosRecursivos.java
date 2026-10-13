package com.quindioexpress.algoritmos;

import com.quindioexpress.excepciones.ReglaNegocioException;
import com.quindioexpress.modelo.Paquete;

import java.util.List;

/**
 * Algoritmos recursivos puros, independientes del estado del sistema.
 *
 * <p><b>Prohibido</b> el uso de {@code for}, {@code while}, {@code do-while} y
 * streams en los métodos recursivos. La comparación de destino es
 * {@code equalsIgnoreCase} de forma uniforme.</p>
 *
 * <p>Con {@link #TRAZA} en {@code true} se imprime cada llamada (con sangría
 * según la profundidad), el caso base y el valor retornado, para poder construir
 * la tabla de la pila de llamadas.</p>
 */
public final class AlgoritmosRecursivos {

    /** Bandera de traza de ejecución. */
    public static boolean TRAZA = false;

    private AlgoritmosRecursivos() {
    }

    /**
     * Sobrecarga de entrada: inicia la recursión en el índice 0.
     *
     * @return la suma de los pesos de los paquetes cuyo destino coincide.
     */
    public static double pesoTotalPorDestino(List<Paquete> paquetes, String destino) {
        return pesoTotalPorDestino(paquetes, destino, 0);
    }

    /**
     * Suma recursiva de los pesos de los paquetes de un destino.
     *
     * @param paquetes lista de paquetes (no nula).
     * @param destino  municipio a sumar.
     * @param indice   posición actual en la recursión.
     * @return el peso total del destino indicado.
     */
    public static double pesoTotalPorDestino(List<Paquete> paquetes, String destino, int indice) {
        if (paquetes == null) {
            throw new ReglaNegocioException("La lista de paquetes no puede ser nula.");
        }
        if (destino == null) {
            throw new ReglaNegocioException("El destino no puede ser nulo.");
        }
        String sangria = "  ".repeat(Math.max(0, indice));

        // Caso base: se recorrió toda la lista
        if (indice == paquetes.size()) {
            if (TRAZA) {
                System.out.println(sangria + "pesoTotalPorDestino(indice=" + indice + ") -> caso base: 0.0");
            }
            return 0.0;
        }

        Paquete actual = paquetes.get(indice);
        if (TRAZA) {
            System.out.println(sangria + "pesoTotalPorDestino(indice=" + indice + ", destino=" + destino
                    + ") paquete=" + actual.getCodigo());
        }

        // Caso recursivo: avanza hacia el caso base
        double resultado;
        if (actual.getDestino().equalsIgnoreCase(destino)) {
            resultado = actual.getPeso() + pesoTotalPorDestino(paquetes, destino, indice + 1);
        } else {
            resultado = pesoTotalPorDestino(paquetes, destino, indice + 1);
        }

        if (TRAZA) {
            System.out.println(sangria + "retorna " + resultado);
        }
        return resultado;
    }

    /**
     * Sobrecarga de entrada: inicia la recursión en el índice 0.
     *
     * @return la cantidad de paquetes con prioridad mayor o igual a {@code minima}.
     */
    public static int contarPrioridadMinima(List<Paquete> paquetes, int minima) {
        return contarPrioridadMinima(paquetes, minima, 0);
    }

    /**
     * Conteo recursivo de paquetes con prioridad mayor o igual a un mínimo.
     *
     * @param paquetes lista de paquetes (no nula).
     * @param minima   prioridad mínima a contar.
     * @param indice   posición actual en la recursión.
     * @return la cantidad de paquetes que cumplen la condición.
     */
    public static int contarPrioridadMinima(List<Paquete> paquetes, int minima, int indice) {
        if (paquetes == null) {
            throw new ReglaNegocioException("La lista de paquetes no puede ser nula.");
        }
        String sangria = "  ".repeat(Math.max(0, indice));

        // Caso base: se recorrió toda la lista
        if (indice == paquetes.size()) {
            if (TRAZA) {
                System.out.println(sangria + "contarPrioridadMinima(indice=" + indice + ") -> caso base: 0");
            }
            return 0;
        }

        Paquete actual = paquetes.get(indice);
        if (TRAZA) {
            System.out.println(sangria + "contarPrioridadMinima(indice=" + indice + ", minima=" + minima
                    + ") paquete=" + actual.getCodigo() + " prioridad=" + actual.getPrioridad());
        }

        // Caso recursivo: avanza hacia el caso base
        int resultado;
        if (actual.getPrioridad() >= minima) {
            resultado = 1 + contarPrioridadMinima(paquetes, minima, indice + 1);
        } else {
            resultado = contarPrioridadMinima(paquetes, minima, indice + 1);
        }

        if (TRAZA) {
            System.out.println(sangria + "retorna " + resultado);
        }
        return resultado;
    }
}
