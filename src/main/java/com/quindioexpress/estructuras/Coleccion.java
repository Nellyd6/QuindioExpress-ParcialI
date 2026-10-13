package com.quindioexpress.estructuras;

/**
 * Interfaz genérica que define <b>qué</b> hace una colección propia.
 *
 * <p>La implementación concreta ({@link ListaEnlazada}) define <b>cómo</b> lo
 * hace. Al extender {@link Iterable}, cualquier colección propia puede
 * recorrerse con un bucle {@code for-each} sin exponer su estructura interna.</p>
 *
 * @param <T> tipo de los elementos almacenados.
 */
public interface Coleccion<T> extends Iterable<T> {

    /** Agrega un elemento al final de la colección. */
    void agregar(T elemento);

    /**
     * Elimina la primera ocurrencia del elemento (según {@code equals}).
     *
     * @return {@code true} si se encontró y eliminó; {@code false} si no existía.
     */
    boolean eliminar(T elemento);

    /**
     * Obtiene el elemento en la posición indicada (base 0).
     *
     * @throws IndexOutOfBoundsException si la posición es inválida.
     */
    T obtener(int posicion);

    /** @return la cantidad de elementos almacenados. */
    int tamano();

    /** @return {@code true} si la colección no tiene elementos. */
    boolean estaVacia();
}
