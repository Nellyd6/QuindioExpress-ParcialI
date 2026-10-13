package com.quindioexpress.comparadores;

import com.quindioexpress.modelo.Paquete;

import java.util.Comparator;

/**
 * Criterio de ordenamiento por <b>prioridad de mayor a menor</b>.
 *
 * <p>Es un {@link Comparator} reutilizable: no altera el orden natural de
 * {@link Paquete} (definido por código) y puede combinarse con otros criterios
 * mediante {@code thenComparing}.</p>
 */
public class ComparadorPrioridad implements Comparator<Paquete> {

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
        // Prioridad de mayor a menor (5 -> 1)
        return Integer.compare(segundo.getPrioridad(), primero.getPrioridad());
    }
}
