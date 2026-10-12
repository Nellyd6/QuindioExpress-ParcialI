package main;

import model.Paquete;

public class Main {

    public static void main(String[] args) {
        //  arreglo de pruebas con los 3 paquetes simulados
        Paquete[] paquetes = new Paquete[3];
        paquetes[0] = new Paquete("PQ01", "Armenia", 12.5, 5);
        paquetes[1] = new Paquete("PQ02", "Calarcá", 8.2, 3);
        paquetes[2] = new Paquete("PQ03", "Armenia", 20.0, 4);

        String municipioBuscado = "Armenia";

        //  Invocación del método recursivo
        double pesoTotal = calcularPesoTotalRecursivo(paquetes, municipioBuscado, 0);

        // 3. resultado final en la consola
        System.out.println("El peso total de los paquetes para " + municipioBuscado + " es: " + pesoTotal + " kg");
    }


     // calcular el peso total de los paquetes de un municipio.

    public static double calcularPesoTotalRecursivo(Paquete[] arreglo, String municipio, int indice) {
        // CASO BASE: Si el índice llega al final del arreglo, se detiene y retorna 0
        if (indice == arreglo.length) {
            return 0.0;
        }

        // Obtener el paquete de la posición actual en memoria
        Paquete paqueteActual = arreglo[indice];

        // CASO RECURSIVO: Evaluar el destino del paquete actual
        if (paqueteActual.getDestino().equalsIgnoreCase(municipio)) {
            // Si coincide, sumamos su peso y llamamos recursivamente para el resto del arreglo
            return paqueteActual.getPeso() + calcularPesoTotalRecursivo(arreglo, municipio, indice + 1);
        } else {
            // Si no coincide, ignoramos su peso y pasamos al siguiente índice
            return calcularPesoTotalRecursivo(arreglo, municipio, indice + 1);
        }
    }
}

