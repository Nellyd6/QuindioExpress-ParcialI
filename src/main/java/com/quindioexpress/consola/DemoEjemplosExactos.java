package com.quindioexpress.consola;

import com.quindioexpress.algoritmos.AlgoritmosRecursivos;
import com.quindioexpress.algoritmos.BusquedaBinaria;
import com.quindioexpress.algoritmos.DivideYVenceras;
import com.quindioexpress.modelo.Paquete;
import com.quindioexpress.servicios.GestorDespachoPrioritario;

import java.util.ArrayList;
import java.util.List;

/**
 * Demuestra con datos reducidos los ejemplos exactos del enunciado: el orden de
 * despacho, la recursividad (peso y conteo), la búsqueda binaria y el divide y
 * vencerás. Incluye la traza de ejecución.
 */
public class DemoEjemplosExactos {

    /** Ejecuta todos los ejemplos e imprime los resultados. */
    public void ejecutar() {
        System.out.println("\n=== EJEMPLOS EXACTOS DEL ENUNCIADO ===");
        ejemploDespacho();
        ejemploPesoRecursivo();
        ejemploConteoRecursivo();
        ejemploBusquedaBinaria();
        ejemploMayorPeso();
        System.out.println("=== FIN DE LOS EJEMPLOS EXACTOS ===");
    }

    /** Orden de despacho esperado PQ105, PQ731, PQ942, PQ420. */
    private void ejemploDespacho() {
        System.out.println("\n1) Orden de despacho");
        GestorDespachoPrioritario gestor = new GestorDespachoPrioritario();
        gestor.encolarPaquete(new Paquete("PQ420", "Armenia", 1.0, 3, 30));
        gestor.encolarPaquete(new Paquete("PQ731", "Armenia", 1.0, 5, 45));
        gestor.encolarPaquete(new Paquete("PQ105", "Armenia", 1.0, 5, 25));
        gestor.encolarPaquete(new Paquete("PQ942", "Armenia", 1.0, 4, 20));

        List<String> obtenido = new ArrayList<>();
        while (!gestor.estaVacia()) {
            obtenido.add(gestor.despacharSiguiente().getCodigo());
        }
        List<String> esperado = new ArrayList<>(List.of("PQ105", "PQ731", "PQ942", "PQ420"));
        System.out.println("   - Esperado: " + esperado);
        System.out.println("   - Obtenido: " + obtenido);
        System.out.println("   - Resultado: " + (obtenido.equals(esperado) ? "OK" : "FALLO"));
    }

    /** Subconjunto de Armenia que suma 14.0 kg, con traza de ejecución. */
    private void ejemploPesoRecursivo() {
        System.out.println("\n2) Peso total recursivo de Armenia = 14.0 kg");
        List<Paquete> subconjunto = new ArrayList<>();
        subconjunto.add(new Paquete("PQ731", "Armenia", 3.2, 5, 2));
        subconjunto.add(new Paquete("PQ942", "Armenia", 7.5, 2, 6));
        subconjunto.add(new Paquete("PQ318", "Armenia", 0.9, 4, 3));
        subconjunto.add(new Paquete("PQ204", "Armenia", 2.4, 1, 8));

        AlgoritmosRecursivos.TRAZA = true;
        double total = AlgoritmosRecursivos.pesoTotalPorDestino(subconjunto, "Armenia");
        AlgoritmosRecursivos.TRAZA = false;

        System.out.println("   - Peso total: " + total + " kg (esperado 14.0)");
        System.out.println("   - Resultado: " + (Math.abs(total - 14.0) < 1e-9 ? "OK" : "FALLO"));
    }

    /** Prioridades (5,2,4,1,5,3,4): 4 paquetes con prioridad >= 4. */
    private void ejemploConteoRecursivo() {
        System.out.println("\n3) Conteo recursivo con prioridad >= 4");
        int[] prioridades = {5, 2, 4, 1, 5, 3, 4};
        List<Paquete> lista = new ArrayList<>();
        for (int i = 0; i < prioridades.length; i++) {
            lista.add(new Paquete("PQ" + (100 + i), "Armenia", 1.0, prioridades[i], 5));
        }
        int cantidad = AlgoritmosRecursivos.contarPrioridadMinima(lista, 4);
        System.out.println("   - Prioridades: 5, 2, 4, 1, 5, 3, 4");
        System.out.println("   - Paquetes con prioridad >= 4: " + cantidad + " (esperado 4)");
        System.out.println("   - Resultado: " + (cantidad == 4 ? "OK" : "FALLO"));
    }

    /** Buscar PQ731 en 8 códigos ordenados: posición 5. */
    private void ejemploBusquedaBinaria() {
        System.out.println("\n4) Búsqueda binaria de PQ731");
        String[] codigos = {"PQ105", "PQ214", "PQ318", "PQ421", "PQ567", "PQ731", "PQ842", "PQ942"};
        List<Paquete> ordenada = new ArrayList<>();
        for (String codigo : codigos) {
            ordenada.add(new Paquete(codigo, "Armenia", 1.0, 3, 5));
        }
        int posicion = BusquedaBinaria.buscarPorCodigo(ordenada, "PQ731");
        System.out.println("   - Códigos ordenados: " + List.of(codigos));
        System.out.println("   - Posición de PQ731: " + posicion + " (esperado 5)");
        int ausente = BusquedaBinaria.buscarPorCodigo(ordenada, "PQ999");
        System.out.println("   - Posición de PQ999 (inexistente): " + ausente + " (esperado -1)");
        System.out.println("   - Resultado: " + (posicion == 5 && ausente == -1 ? "OK" : "FALLO"));
    }

    /** Pesos 4.5, 12.0, 3.2, 18.7, 7.4, 5.1: mayor peso 18.7 kg, con traza. */
    private void ejemploMayorPeso() {
        System.out.println("\n5) Paquete de mayor peso con divide y vencerás");
        double[] pesos = {4.5, 12.0, 3.2, 18.7, 7.4, 5.1};
        List<Paquete> lista = new ArrayList<>();
        for (int i = 0; i < pesos.length; i++) {
            lista.add(new Paquete("PQ" + (200 + i), "Armenia", pesos[i], 3, 5));
        }
        DivideYVenceras.TRAZA = true;
        Paquete mayor = DivideYVenceras.paqueteMayorPeso(lista);
        DivideYVenceras.TRAZA = false;

        System.out.println("   - Pesos: 4.5, 12.0, 3.2, 18.7, 7.4, 5.1");
        System.out.println("   - Mayor peso: " + mayor.getPeso() + " kg (esperado 18.7)");
        System.out.println("   - Resultado: " + (Math.abs(mayor.getPeso() - 18.7) < 1e-9 ? "OK" : "FALLO"));
    }
}