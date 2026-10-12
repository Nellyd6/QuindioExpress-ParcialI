package main;

import model.Paquete;

public class Main {

    public static void main(String[] args) {
        // OJO: Los paquetes DEBEN estar ordenados por codigo para que la busqueda binaria funcione
        Paquete[] paquetes = new Paquete
        paquetes = new Paquete("PQ01", "Armenia", 12.5, 5);
        paquetes = new Paquete("PQ02", "Calarcá", 8.2, 3);
        paquetes = new Paquete("PQ03", "Armenia", 20.0, 4);

        // --- PRUEBA METODO RECURSIVO 1 ---
        String municipioBuscado = "Armenia";
        double pesoTotal = calcularPesoTotalRecursivo(paquetes, municipioBuscado, 0);
        System.out.println("El peso total de los paquetes para " + municipioBuscado + " es: " + pesoTotal + " kg");

        // --- PRUEBA METODO RECURSIVO 2 ---
        int prioridadLimite = 4;
        int cantidadPrioritarios = contarPaquetesPorPrioridadRecursivo(paquetes, prioridadLimite, 0);
        System.out.println("Cantidad de paquetes con prioridad >= " + prioridadLimite + " es: " + cantidadPrioritarios);

        // --- PRUEBA METODO 3: BUSQUEDA BINARIA ---
        String codigoABuscar = "PQ02";
        // Buscamos en todo el arreglo, desde la posicion 0 hasta la ultima (tamano - 1)
        int posicionEncontrada = busquedaBinariaPorCodigo(paquetes, codigoABuscar, 0, paquetes.length - 1);

        if (posicionEncontrada != -1) {
            System.out.println("¡Paquete encontrado! Esta en la posicion: " + posicionEncontrada +
                    " y va para " + paquetes[posicionEncontrada].getDestino());
        } else {
            System.out.println("El paquete con codigo " + codigoABuscar + " no existe.");
        }
    }

    // Metodo recursivo 1: Sumar pesos
    public static double calcularPesoTotalRecursivo(Paquete[] arreglo, String municipio, int indice) {
        if (indice == arreglo.length) {
            return 0.0;
        }
        Paquete paqueteActual = arreglo[indice];
        if (paqueteActual.getDestino().equalsIgnoreCase(municipio)) {
            return paqueteActual.getPeso() + calcularPesoTotalRecursivo(arreglo, municipio, indice + 1);
        } else {
            return calcularPesoTotalRecursivo(arreglo, municipio, indice + 1);
        }
    }

    // Metodo recursivo 2: Contar por prioridad
    public static int contarPaquetesPorPrioridadRecursivo(Paquete[] arreglo, int prioridadMinima, int indice) {
        if (indice == arreglo.length) {
            return 0;
        }
        Paquete paqueteActual = arreglo[indice];
        if (paqueteActual.getPrioridad() >= prioridadMinima) {
            return 1 + contarPaquetesPorPrioridadRecursivo(arreglo, prioridadMinima, indice + 1);
        } else {
            return contarPaquetesPorPrioridadRecursivo(arreglo, prioridadMinima, indice + 1);
        }
    }

    /**
     * Metodo para buscar un paquete tirando de matematicas (partiendo a la mitad).
     * Tampoco usa ciclos for ni while, se va llamando sola.
     */
    public static int busquedaBinariaPorCodigo(Paquete[] arreglo, String codigoBuscado, int izquierda, int derecha) {
        // CASO BASE 1: Si el puntero izquierdo pasa al derecho, el paquete no existe en la lista
        if (izquierda > derecha) {
            return -1;
        }

        // Encontramos el punto medio del arreglo para partir el problema
        int mitad = (izquierda + derecha) / 2;
        Paquete paqueteMitad = arreglo[mitad];

        // Comparamos el codigo del medio con el que estamos buscando
        // compareTo devuelve 0 si los textos son exactamente iguales
        int resultadoComparacion = paqueteMitad.getCodigo().compareTo(codigoBuscado);

        // CASO BASE 2: ¡Tuvimos suerte y el de la mitad era el que buscabamos!
        if (resultadoComparacion == 0) {
            return mitad;
        }

        // CASO RECURSIVO: Si el de la mitad es mayor, buscamos solo en la mitad de la IZQUIERDA
        if (resultadoComparacion > 0) {
            return busquedaBinariaPorCodigo(arreglo, codigoBuscado, izquierda, mitad - 1);
        }
        // Si el de la mitad es menor, buscamos solo en la mitad de la DERECHA
        else {
            return busquedaBinariaPorCodigo(arreglo, codigoBuscado, mitad + 1, derecha);
        }
    }
}
