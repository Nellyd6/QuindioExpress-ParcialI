package com.quindioexpress.consola;

import com.quindioexpress.estructuras.ListaEnlazada;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Demuestra la estructura propia {@link ListaEnlazada} en todos sus casos
 * límite (RP-07, RE-24 a RE-31).
 *
 * <p>Se usa {@code Integer} como tipo de elemento para evidenciar que la
 * colección es genérica y no depende de {@code Paquete} (RE-15).</p>
 */
public class DemoListaPropia {

    /** Ejecuta todos los casos límite e imprime el resultado de cada uno. */
    public void ejecutar() {
        System.out.println("\n=== DEMOSTRACIÓN DE LA LISTA ENLAZADA PROPIA (genérica) ===");
        listaVacia();
        unSoloElemento();
        eliminarPrimero();
        eliminarIntermedio();
        eliminarUltimo();
        eliminarInexistente();
        posicionInvalida();
        iteradorSinElementos();
        System.out.println("=== FIN DE LA DEMOSTRACIÓN DE LA LISTA PROPIA ===");
    }

    /** RE-24: lista vacía. */
    private void listaVacia() {
        System.out.println("\n1) Lista vacía (RE-24)");
        ListaEnlazada<Integer> lista = new ListaEnlazada<>();
        System.out.println("   - estaVacia() -> " + lista.estaVacia());
        System.out.println("   - tamano() -> " + lista.tamano());
        System.out.println("   - eliminar(10) -> " + lista.eliminar(10));
        try {
            lista.obtener(0);
            System.out.println("   - obtener(0) -> NO lanzó excepción (incorrecto)");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("   - obtener(0) -> IndexOutOfBoundsException controlada");
        }
    }

    /** RE-25: un solo elemento. */
    private void unSoloElemento() {
        System.out.println("\n2) Un solo elemento (RE-25)");
        ListaEnlazada<Integer> lista = new ListaEnlazada<>();
        lista.agregar(42);
        System.out.println("   - tras agregar(42): tamano=" + lista.tamano() + ", obtener(0)=" + lista.obtener(0));
        System.out.println("   - eliminar(42) -> " + lista.eliminar(42));
        System.out.println("   - estado final: estaVacia=" + lista.estaVacia() + ", tamano=" + lista.tamano());
    }

    /** RE-26: eliminar el primero. */
    private void eliminarPrimero() {
        System.out.println("\n3) Eliminar el primero (RE-26)");
        ListaEnlazada<Integer> lista = construir(1, 2, 3);
        System.out.println("   - antes: " + contenido(lista));
        System.out.println("   - eliminar(1) -> " + lista.eliminar(1));
        System.out.println("   - después: " + contenido(lista));
    }

    /** RE-27: eliminar uno intermedio. */
    private void eliminarIntermedio() {
        System.out.println("\n4) Eliminar uno intermedio (RE-27)");
        ListaEnlazada<Integer> lista = construir(1, 2, 3);
        System.out.println("   - antes: " + contenido(lista));
        System.out.println("   - eliminar(2) -> " + lista.eliminar(2));
        System.out.println("   - después: " + contenido(lista));
    }

    /** RE-28: eliminar el último. */
    private void eliminarUltimo() {
        System.out.println("\n5) Eliminar el último (RE-28)");
        ListaEnlazada<Integer> lista = construir(1, 2, 3);
        System.out.println("   - antes: " + contenido(lista));
        System.out.println("   - eliminar(3) -> " + lista.eliminar(3));
        System.out.println("   - después: " + contenido(lista));
        // Se vuelve a agregar para verificar que la referencia a la cola quedó bien
        lista.agregar(9);
        System.out.println("   - tras agregar(9): " + contenido(lista));
    }

    /** RE-29: eliminar uno inexistente. */
    private void eliminarInexistente() {
        System.out.println("\n6) Eliminar uno inexistente (RE-29)");
        ListaEnlazada<Integer> lista = construir(1, 2, 3);
        int tamanoAntes = lista.tamano();
        boolean resultado = lista.eliminar(99);
        System.out.println("   - eliminar(99) -> " + resultado
                + "; tamaño antes=" + tamanoAntes + ", después=" + lista.tamano());
    }

    /** RE-30: posición inválida en obtener. */
    private void posicionInvalida() {
        System.out.println("\n7) Posición inválida en obtener (RE-30)");
        ListaEnlazada<Integer> lista = construir(1, 2, 3);
        try {
            lista.obtener(5);
            System.out.println("   - obtener(5) -> NO lanzó excepción (incorrecto)");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("   - obtener(5) -> IndexOutOfBoundsException controlada: " + e.getMessage());
        }
    }

    /** RE-31: Iterator.next() sin elementos. */
    private void iteradorSinElementos() {
        System.out.println("\n8) Iterator.next() sin elementos (RE-31)");
        ListaEnlazada<Integer> lista = new ListaEnlazada<>();
        Iterator<Integer> iterador = lista.iterator();
        System.out.println("   - hasNext() -> " + iterador.hasNext());
        try {
            iterador.next();
            System.out.println("   - next() -> NO lanzó excepción (incorrecto)");
        } catch (NoSuchElementException e) {
            System.out.println("   - next() -> NoSuchElementException controlada");
        }
    }

    /** Construye una lista con los elementos indicados, en ese orden. */
    private ListaEnlazada<Integer> construir(int... valores) {
        ListaEnlazada<Integer> lista = new ListaEnlazada<>();
        for (int valor : valores) {
            lista.agregar(valor);
        }
        return lista;
    }

    /** Compone una cadena con el contenido de la lista usando el iterador. */
    private String contenido(ListaEnlazada<Integer> lista) {
        StringBuilder constructor = new StringBuilder("[");
        boolean primero = true;
        for (Integer valor : lista) {
            if (!primero) {
                constructor.append(", ");
            }
            constructor.append(valor);
            primero = false;
        }
        return constructor.append("]").toString();
    }
}
