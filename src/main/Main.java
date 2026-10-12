package main;

import model.Paquete;

public class Main {

    public static void main(String[] args) {
        // 1. Arreglo de pruebas con los 3 paquetes simulados
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
    }

    /**
     * Método recursivo para calcular el peso total de los paquetes de un municipio.
     */
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

    /**
     * Método recursivo para contar los paquetes que igualan o superan una prioridad dada.
     * Cumple con la restricción estricta de NO utilizar ciclos for ni while.
     */
    public static int contarPaquetesPorPrioridadRecursivo(Paquete[] arreglo, int prioridadMinima, int indice) {
        // CASO BASE: Si el índice llega al final del arreglo, se detiene el conteo
        if (indice == arreglo.length) {
            return 0;
        }

        Paquete paqueteActual = arreglo[indice];

        // CASO RECURSIVO: Validar la prioridad del elemento actual
        if (paqueteActual.getPrioridad() >= prioridadMinima) {
            // Si cumple, sumamos 1 y continuamos con el resto del arreglo
            return 1 + contarPaquetesPorPrioridadRecursivo(arreglo, prioridadMinima, indice + 1);
        } else {
            // Si no cumple, sumamos 0 y continuamos con el resto del arreglo
            return contarPaquetesPorPrioridadRecursivo(arreglo, prioridadMinima, indice + 1);
        }
    }
}
