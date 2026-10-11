package main;

import model.Paquete;

public class Main {

    public static void main(String[] args) {
        // Inicialización de datos simulados para habilitar el entorno de desarrollo
        Paquete[] paquetes = new Paquete[3];
        paquetes[0] = new Paquete("PQ01", "Armenia", 12.5, 5);
        paquetes[1] = new Paquete("PQ02", "Calarcá", 8.2, 3);
        paquetes[2] = new Paquete("PQ03", "Armenia", 20.0, 4);

        System.out.println("Entorno de pruebas de QuindíoExpress listo para el Integrante 3.");
    }
}
