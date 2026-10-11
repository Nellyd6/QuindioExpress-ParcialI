package com.quindioexpress.principal;

import com.quindioexpress.modelo.Paquete;
import com.quindioexpress.servicios.HistorialEntregas;

public class Main {
    public static void main(String[] args) {
        System.out.println(" PRUEBA INICIAL QUINDÍO EXPRESS ");

        //Crear algunos paquetes de prueba
        Paquete p1 = new Paquete("PQ001", "Armenia", 2.5, 3, 1.5);
        Paquete p2 = new Paquete("PQ002", "Calarcá", 4.0, 1, 2.0);

        //Probar el Historial de Entregas con la lista propia
        HistorialEntregas historial = new HistorialEntregas();
        historial.registrarEntrega(p1);
        historial.registrarEntrega(p2);

        System.out.println("Total de entregas registradas: " + historial.totalEntregas());

        // 3. Probar el bucle for-each gracias a que la lista implementa Iterable
        System.out.println("\nListado de paquetes en el historial:");
        for (Paquete p : historial.getHistorial()) {
            System.out.println(p);
        }
    }
}