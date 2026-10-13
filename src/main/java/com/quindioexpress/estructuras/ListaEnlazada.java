package com.quindioexpress.estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Lista simplemente enlazada propia y genérica.
 *
 * <p><b>Restricción:</b> no usa internamente ninguna colección de Java
 * ({@code ArrayList}, {@code LinkedList}, {@code Vector}, {@code Stack},
 * {@code Queue}, {@code Deque}, etc.) para almacenar elementos.</p>
 *
 * <p>El nodo ({@link Nodo}) es una clase <b>interna privada</b>: el código
 * externo nunca accede a la cabeza ni a las referencias de los nodos; solo
 * recorre la lista con {@code for-each} o {@link #iterator()}.</p>
 *
 * @param <T> tipo de los elementos almacenados.
 */
public class ListaEnlazada<T> implements Coleccion<T> {

    /** Nodo interno de la lista. No accesible desde fuera de la clase. */
    private static final class Nodo<T> {
        private final T valor;
        private Nodo<T> siguiente;

        private Nodo(T valor) {
            this.valor = valor;
            this.siguiente = null;
        }
    }

    private Nodo<T> cabeza;
    private Nodo<T> cola;
    private int tamano;

    public ListaEnlazada() {
        this.cabeza = null;
        this.cola = null;
        this.tamano = 0;
    }

    /**
     * Agrega un elemento al final conservando el orden exacto de inserción.
     * Complejidad: O(1).
     */
    @Override
    public void agregar(T elemento) {
        Nodo<T> nuevo = new Nodo<>(elemento);
        if (cabeza == null) {
            cabeza = nuevo;
            cola = nuevo;
        } else {
            cola.siguiente = nuevo;
            cola = nuevo;
        }
        tamano++;
    }

    /**
     * Elimina la primera ocurrencia del elemento (según {@code equals}) y
     * mantiene cabeza, cola y tamaño coherentes. Complejidad: O(n).
     */
    @Override
    public boolean eliminar(T elemento) {
        Nodo<T> anterior = null;
        Nodo<T> actual = cabeza;
        while (actual != null) {
            if (coincide(actual.valor, elemento)) {
                if (anterior == null) {
                    cabeza = actual.siguiente;
                } else {
                    anterior.siguiente = actual.siguiente;
                }
                if (actual == cola) {
                    cola = anterior;
                }
                tamano--;
                return true;
            }
            anterior = actual;
            actual = actual.siguiente;
        }
        return false;
    }

    private boolean coincide(T almacenado, T buscado) {
        return almacenado == null ? buscado == null : almacenado.equals(buscado);
    }

    /**
     * Obtiene el elemento en la posición indicada. Complejidad: O(n).
     *
     * @throws IndexOutOfBoundsException si la posición es inválida.
     */
    @Override
    public T obtener(int posicion) {
        validarPosicion(posicion);
        Nodo<T> actual = cabeza;
        for (int i = 0; i < posicion; i++) {
            actual = actual.siguiente;
        }
        return actual.valor;
    }

    @Override
    public int tamano() {
        return tamano;
    }

    @Override
    public boolean estaVacia() {
        return tamano == 0;
    }

    private void validarPosicion(int posicion) {
        if (posicion < 0 || posicion >= tamano) {
            throw new IndexOutOfBoundsException(
                    "Posición fuera de límites: " + posicion + ", tamaño: " + tamano);
        }
    }

    /** @return un iterador sobre los elementos, en orden de inserción. */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Nodo<T> actual = cabeza;

            @Override
            public boolean hasNext() {
                return actual != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T valor = actual.valor;
                actual = actual.siguiente;
                return valor;
            }
        };
    }
}
