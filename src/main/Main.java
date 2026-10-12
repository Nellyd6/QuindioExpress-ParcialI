package main;

import model.Paquete;

public class Main {

    public static void main(String[] args) {
        // Arreglo de pruebas obligatorio ordenado por codigo para los metodos
        Paquete[] paquetes = new Paquete[3];
        paquetes[0] = new Paquete("PQ01", "Armenia", 12.5, 5);
        paquetes[1] = new Paquete("PQ02", "Calarcá", 8.2, 3);
        paquetes[2] = new Paquete("PQ03", "Armenia", 20.0, 4);

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
        int posicionEncontrada = busquedaBinariaPorCodigo(paquetes, codigoABuscar, 0, paquetes.length - 1);

        if (posicionEncontrada != -1) {
            System.out.println("¡Paquete encontrado! Esta en la posicion: " + posicionEncontrada +
                    " y va para " + paquetes[posicionEncontrada].getDestino());
        } else {
            System.out.println("El paquete con codigo " + codigoABuscar + " no existe.");
        }

        // --- PRUEBA METODO 4: DIVIDE Y VENCERAS ---
        // Buscamos el paquete mas pesado recorriendo desde la posicion 0 hasta la ultima
        Paquete paqueteMasPesado = encontrarMasPesadoDivideYVenceras(paquetes, 0, paquetes.length - 1);
        System.out.println("El paquete mas pesado de todos es: " + paqueteMasPesado.getCodigo() +
                " con un peso de: " + paqueteMasPesado.getPeso() + " kg");
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

    // Metodo 3: Busqueda Binaria
    public static int busquedaBinariaPorCodigo(Paquete[] arreglo, String codigoBuscado, int izquierda, int derecha) {
        if (izquierda > derecha) {
            return -1;
        }
        int mitad = (izquierda + derecha) / 2;
        Paquete paqueteMitad = arreglo[mitad];
        int resultadoComparacion = paqueteMitad.getCodigo().compareTo(codigoBuscado);

        if (resultadoComparacion == 0) {
            return mitad;
        }
        if (resultadoComparacion > 0) {
            return busquedaBinariaPorCodigo(arreglo, codigoBuscado, izquierda, mitad - 1);
        } else {
            return busquedaBinariaPorCodigo(arreglo, codigoBuscado, mitad + 1, derecha);
        }
    }

    /**
     * Metodo 4: Divide y Venceras.
     * Parte el arreglo a la mitad de forma sucesiva para encontrar el objeto con mayor peso.
     */
    public static Paquete encontrarMasPesadoDivideYVenceras(Paquete[] arreglo, int inicio, int fin) {
        // CASO BASE: Si el sub-arreglo tiene un solo elemento, ese elemento es el mas pesado
        if (inicio == fin) {
            return arreglo[inicio];
        }

        // Dividimos el problema encontrando la mitad de la sección actual
        int mitad = (inicio + fin) / 2;

        //Buscamos recursivamente el campeon del lado izquierdo y del derecho
        Paquete maxIzquierdo = encontrarMasPesadoDivideYVenceras(arreglo, inicio, mitad);
        Paquete maxDerecho = encontrarMasPesadoDivideYVenceras(arreglo, mitad + 1, fin);

        // Comparamos los dos campeones y retornamos el que gane en peso
        if (maxIzquierdo.getPeso() >= maxDerecho.getPeso()) {
            return maxIzquierdo;
        } else {
            return maxDerecho;
        }
    }
}
