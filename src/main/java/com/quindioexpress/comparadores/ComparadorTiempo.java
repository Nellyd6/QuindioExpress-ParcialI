package com.quindioexpress.comparadores;

import com.quindioexpress.modelo.Paquete;

import java.util.Comparator;

/**
 * Criterio de ordenamiento por <b>tiempo estimado de menor a mayor</b>.
 *
 * <p>Es un {@link Comparator} reutilizable que no altera el orden natural de
 * {@link Paquete}.</p>
 */
public class ComparadorTiempo implements Comparator<Paquete> {

    @Override
    public int compare(Paquete primero, Paquete segundo) {
        if (primero == segundo) {
            return 0;
        }
        if (primero == null) {
            return 1;
        }
        if (segundo == null) {
            return -1;
        }
        // Tiempo estimado de menor a mayor
        return Integer.compare(primero.getTiempoEstimado(), segundo.getTiempoEstimado());
    }
}
