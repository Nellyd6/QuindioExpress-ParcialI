package com.quindioexpress.consola;

/**
 * Opción del menú principal: número, descripción y acción a ejecutar.
 *
 * @param numero      número que el usuario escribe para elegirla.
 * @param descripcion texto que se muestra en el menú.
 * @param accion      acción que se ejecuta al seleccionarla.
 */
public record Opcion(int numero, String descripcion, Accion accion) {
}
