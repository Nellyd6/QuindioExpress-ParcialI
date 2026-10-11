package com.quindioexpress.estructuras;

    public interface ILista<T> extends Iterable<T> {

        //Agrega un nuevo elemento al final de la lista.

        void agregar(T elemento);

        /**
         * Obtiene el elemento ubicado en una posición específica.
         * @param indice Índice del elemento (comienza en 0).
         * @return El elemento encontrado.
         */
        T obtener(int indice);

        /**
         * Elimina un elemento de la lista por su posición o valor.
         * @param indice Índice del elemento a eliminar.
         * @return El elemento eliminado.
         */
        T eliminar(int indice);

        /**
         * Retorna la cantidad actual de elementos en la lista.
         */
        int tamano();

        /**
         * Valida si la lista se encuentra vacía.
         */
        boolean estaVacia();
    }

