package com.quindioexpress.consola;

/**
 * Acción ejecutable asociada a una opción del menú.
 *
 * <p>Es una interfaz funcional: permite registrar cada opción mediante una
 * referencia a método o una expresión lambda.</p>
 */
@FunctionalInterface
public interface Accion {

    /** Ejecuta la acción de la opción. */
    void ejecutar();
}
