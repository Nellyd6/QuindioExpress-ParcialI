package com.quindioexpress.estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ListaEnlazadaPropia <T> implements ILista<T> {

    private Nodo<T> cabeza;
    private int tamano;

    public ListaEnlazadaPropia() {
        this.cabeza = null;
        this.tamano = 0;
    }

    @Override
    public void agregar(T elemento) {
        Nodo<T> nuevoNodo = new Nodo<>(elemento);
        if (cabeza == null) {
            cabeza = nuevoNodo;
        } else {
            Nodo<T> actual = cabeza;
            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(nuevoNodo);
        }
        tamano++;
    }

    @Override
    public T obtener(int indice) {
        validarIndice(indice);
        Nodo<T> actual = cabeza;
        for (int i = 0; i < indice; i++) {
            actual = actual.getSiguiente();
        }
        return actual.getDato();
    }

    @Override
    public T eliminar(int indice) {
        validarIndice(indice);
        T datoEliminado;
        if (indice == 0) {
            datoEliminado = cabeza.getDato();
            cabeza = cabeza.getSiguiente();
        } else {
            Nodo<T> anterior = cabeza;
            for (int i = 0; i < indice - 1; i++) {
                anterior = anterior.getSiguiente();
            }
            datoEliminado = anterior.getSiguiente().getDato();
            anterior.setSiguiente(anterior.getSiguiente().getSiguiente());
        }
        tamano--;
        return datoEliminado;
    }

    @Override
    public int tamano() {
        return tamano;
    }

    @Override
    public boolean estaVacia() {
        return tamano == 0;
    }

    private void validarIndice(int indice) {
        if (indice < 0 || indice >= tamano) {
            throw new IndexOutOfBoundsException("Índice fuera de límites: " + indice + ", Tamaño: " + tamano);
        }
    }

    // --- Implementación de Iterable para usar bucles for-each ---
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
                T dato = actual.getDato();
                actual = actual.getSiguiente();
                return dato;
            }
        };
    }
}

