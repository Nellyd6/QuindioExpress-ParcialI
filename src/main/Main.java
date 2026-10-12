package main;

import model.Paquete;

public class Main {

    public static void main(String[] args) {
        // Inicializamos el arreglo con espacio para 3 paquetes (corregido aqui con corchetes)
        Paquete[] paquetes = new Paquete[3];

        // Llenamos las casillas del arreglo con los paquetes ordenados por codigo
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
        // Buscamos en todo el arreglo, desde el inicio (0) hasta el final (tamano - 1)
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

    // Metodo 3: Busqueda Binaria (partiendo a la mitad sin usar for o while)
    public static int busquedaBinariaPorCodigo(Paquete[] arreglo, String codigoBuscado, int izquierda, int derecha) {
        // Caso Base 1: Si los indices se cruzan, el codigo no esta en el arreglo
        if (izquierda > derecha) {
            return -1;
        }

        // Sacamos el punto medio de la seccion actual
        int mitad = (izquierda + derecha) / 2;
        Paquete paqueteMitad = arreglo[mitad];

        // Comparamos el codigo de la mitad con el que buscamos
        int resultadoComparacion = paqueteMitad.getCodigo().compareTo(codigoBuscado);

        // Caso Base 2: Lo encontramos directo en la mitad
        if (resultadoComparacion == 0) {
            return mitad;
        }

        // Si el de la mitad es mayor, nos movemos a buscar en la izquierda
        if (resultadoComparacion > 0) {
            return busquedaBinariaPorCodigo(arreglo, codigoBuscado, izquierda, mitad - 1);
        }
        // Si el de la mitad es menor, nos movemos a buscar en la derecha
        else {
            return busquedaBinariaPorCodigo(arreglo, codigoBuscado, mitad + 1, derecha);
        }
    }
}

