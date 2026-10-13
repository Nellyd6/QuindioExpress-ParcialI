package com.quindioexpress.comparadores;

import com.quindioexpress.modelo.Paquete;

import java.util.Comparator;

/**
 * Criterio de ordenamiento del despacho prioritario. Aplica, en este orden, las
 * reglas de desempate del negocio:
 *
 * <ol>
 *   <li>Mayor prioridad primero ({@link ComparadorPrioridad}).</li>
 *   <li>Si empatan, menor tiempo estimado primero ({@link ComparadorTiempo}).</li>
 *   <li>Si persiste el empate, código ascendente (orden natural de
 *       {@link Paquete}).</li>
 * </ol>
 *
 * <p>Se construye <b>componiendo</b> los comparadores anteriores con
 * {@code thenComparing}, de modo que evidencia la reutilización de los criterios
 * y no altera el orden natural del modelo.</p>
 */
public class ComparadorDespacho implements Comparator<Paquete> {

    /** Comparador compuesto reutilizando los criterios individuales. */
    private static final Comparator<Paquete> CRITERIO_COMPUESTO =
            new ComparadorPrioridad()
                    .thenComparing(new ComparadorTiempo())
                    .thenComparing(Paquete::getCodigo);

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
        return CRITERIO_COMPUESTO.compare(primero, segundo);
    }
}
