package com.quindioexpress.principal;

import com.quindioexpress.modelo.Paquete;
import com.quindioexpress.modelo.Repartidor;
import com.quindioexpress.servicios.CentroDistribucion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- SIMULACIÓN CENTRO DE DISTRIBUCIÓN QUINDÍO EXPRESS ---\n");

        CentroDistribucion centro = new CentroDistribucion();

        // Registrar Repartidores (zonas correspondientes a destinos)
        centro.registrarRepartidor(new Repartidor("REP-01", "Carlos Pérez", "Armenia"));
        centro.registrarRepartidor(new Repartidor("REP-02", "Ana Gómez", "Calarcá"));
        centro.registrarRepartidor(new Repartidor("REP-03", "Luis Rojas", "Salento"));

        // Registrar Paquetes con distintas prioridades y tiempos
        centro.registrarPaquete(new Paquete("PQ101", "Armenia", 3.2, 2, 1.5));
        centro.registrarPaquete(new Paquete("PQ102", "Calarcá", 1.5, 5, 0.8)); // Alta prioridad
        centro.registrarPaquete(new Paquete("PQ103", "Armenia", 5.0, 5, 1.2)); // Alta prioridad, menor tiempo
        centro.registrarPaquete(new Paquete("PQ104", "Salento", 2.0, 3, 2.0));

        //  Probar Búsqueda O(1) con HashMap
        System.out.println("Buscando paquete PQ102: " + centro.buscarPaquetePorCodigo("PQ102"));

        //  Probar TreeSet de destinos ordenados
        System.out.println("\nDestinos únicos registrados (ordenados alfabéticamente):");
        for (String destino : centro.getDestinosUnicos()) {
            System.out.println("- " + destino);
        }

        System.out.println("\nPruebas de algoritmos:");

        // Carga de datos para metodos recursivos y busquedas
        List<Paquete> paquetesArmenia = centro.getPaquetesPorMunicipio().get("Armenia");
        if (paquetesArmenia != null) {
            double pesoArmenia = centro.calcularPesoTotalRecursivo(paquetesArmenia, "Armenia", 0);
            System.out.println("Peso total en Armenia: " + pesoArmenia + " kg");
        }

        List<Paquete> listaCompletaContigua = new ArrayList<>();
        listaCompletaContigua.add(centro.buscarPaquetePorCodigo("PQ101"));
        listaCompletaContigua.add(centro.buscarPaquetePorCodigo("PQ102"));
        listaCompletaContigua.add(centro.buscarPaquetePorCodigo("PQ103"));
        listaCompletaContigua.add(centro.buscarPaquetePorCodigo("PQ104"));

        Collections.sort(listaCompletaContigua);

        int prioritarios = centro.contarPaquetesPorPrioridadRecursivo(listaCompletaContigua, 4, 0);
        System.out.println("Paquetes con prioridad mayor o igual a 4: " + prioritarios);

        int posBinaria = centro.busquedaBinariaPorCodigo(listaCompletaContigua, "PQ103", 0, listaCompletaContigua.size() - 1);
        System.out.println("Posicion de PQ103 en busqueda binaria: " + posBinaria);

        Paquete pesado = centro.encontrarMasPesadoDivideYVenceras(listaCompletaContigua, 0, listaCompletaContigua.size() - 1);
        System.out.println("Paquete con el mayor peso: " + pesado.getCodigo() + " (" + pesado.getPeso() + " kg)");

        // Probar PriorityQueue (Despacho en orden de prioridad)
        System.out.println("\nDespachando paquetes en orden de prioridad:");
        Paquete prox;
        while ((prox = centro.siguientePaqueteADespachar()) != null) {
            System.out.println("-> Despachando: " + prox.getCodigo() + " (Prioridad: " + prox.getPrioridad() + ", Tiempo: " + prox.getTiempoEstimado() + "h)");
        }
    }
}
