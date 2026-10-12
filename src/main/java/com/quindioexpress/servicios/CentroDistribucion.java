package com.quindioexpress.servicios;
import com.quindioexpress.modelo.Paquete;
import com.quindioexpress.modelo.Repartidor;

import java.util.*;

/**
 * Gestiona las operaciones centrales de QuindíoExpress utilizando
 * el Java Collections Framework (JCF) para optimizar búsquedas, colas de prioridad y reportes.
 */
public class CentroDistribucion {

    // Mapa para búsqueda rápida O(1) por código de paquete
    private Map<String, Paquete> mapaPaquetes;

    // Cola de prioridad para atención prioritaria (con reglas de desempate)
    private PriorityQueue<Paquete> colaPrioridad;

    //  Conjunto ordenado alfabéticamente para los destinos únicos
    private Set<String> destinosUnicos;

    //  Mapa para agrupar paquetes por municipio de destino
    private Map<String, List<Paquete>> paquetesPorMunicipio;

    //  Lista de repartidores registrados
    private List<Repartidor> repartidores;

    public CentroDistribucion() {
        this.mapaPaquetes = new HashMap<>();

        // Configuramos la PriorityQueue con las reglas de negocio del proyecto:
        // 1. Mayor prioridad (número más alto o según diseño, aquí asumimos 5 como máxima prioridad)
        // 2. Menor tiempo estimado
        // 3. Código alfabético ascendente
        this.colaPrioridad = new PriorityQueue<>((p1, p2) -> {
            // Comparar por prioridad (mayor prioridad primero)
            int comparacionPrioridad = Integer.compare(p2.getPrioridad(), p1.getPrioridad());
            if (comparacionPrioridad != 0) {
                return comparacionPrioridad;
            }
            // Si empatan, comparar por menor tiempo estimado
            int comparacionTiempo = Double.compare(p1.getTiempoEstimado(), p2.getTiempoEstimado());
            if (comparacionTiempo != 0) {
                return comparacionTiempo;
            }
            // Si vuelven a empatar, comparar por código alfabético ascendente
            return p1.getCodigo().compareTo(p2.getCodigo());
        });

        this.destinosUnicos = new TreeSet<>();
        this.paquetesPorMunicipio = new HashMap<>();
        this.repartidores = new ArrayList<>();
    }

    /**
     * Registra un nuevo paquete en el centro de distribución actualizando todas las colecciones.
     */
    public void registrarPaquete(Paquete paquete) {
        if (paquete == null) return;

        // Evitar duplicados por código
        if (mapaPaquetes.containsKey(paquete.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un paquete con el código: " + paquete.getCodigo());
        }

        // Agregar al mapa de búsqueda rápida
        mapaPaquetes.put(paquete.getCodigo(), paquete);

        // Agregar a la cola de prioridad
        colaPrioridad.add(paquete);

        // Registrar destino único ordenado
        destinosUnicos.add(paquete.getDestino());

        // Agrupar por municipio
        paquetesPorMunicipio.putIfAbsent(paquete.getDestino(), new ArrayList<>());
        paquetesPorMunicipio.get(paquete.getDestino()).add(paquete);
    }

    /**
     * Registra un repartidor en el sistema.
     */
    public void registrarRepartidor(Repartidor repartidor) {
        if (repartidor != null && !repartidores.contains(repartidor)) {
            repartidores.add(repartidor);
        }
    }

    /**
     * Busca un paquete por su código utilizando el HashMap O(1).
     */
    public Paquete buscarPaquetePorCodigo(String codigo) {
        return mapaPaquetes.get(codigo);
    }

    /**
     * Obtiene el siguiente paquete a despachar según la PriorityQueue.
     */
    public Paquete siguientePaqueteADespachar() {
        return colaPrioridad.poll(); // Extrae y remueve el de mayor prioridad
    }

    // --- Getters para consultar las estructuras ---
    public Set<String> getDestinosUnicos() {
        return destinosUnicos;
    }

    public Map<String, List<Paquete>> getPaquetesPorMunicipio() {
        return paquetesPorMunicipio;
    }

    public List<Repartidor> getRepartidores() {
        return repartidores;
    }

    public int totalPaquetesRegistrados() {
        return mapaPaquetes.size();
    }

    // Sumar pesos de un municipio por indices
    public double calcularPesoTotalRecursivo(List<Paquete> lista, String municipio, int indice) {
        if (indice == lista.size()) {
            return 0.0;
        }
        Paquete paqueteActual = lista.get(indice);
        if (paqueteActual.getDestino().equalsIgnoreCase(municipio)) {
            return paqueteActual.getPeso() + calcularPesoTotalRecursivo(lista, municipio, indice + 1);
        } else {
            return calcularPesoTotalRecursivo(lista, municipio, indice + 1);
        }
    }

    // Contar paquetes que cumplan con la prioridad minima
    public int contarPaquetesPorPrioridadRecursivo(List<Paquete> lista, int prioridadMinima, int indice) {
        if (indice == lista.size()) {
            return 0;
        }
        Paquete paqueteActual = lista.get(indice);
        if (paqueteActual.getPrioridad() >= prioridadMinima) {
            return 1 + contarPaquetesPorPrioridadRecursivo(lista, prioridadMinima, indice + 1);
        } else {
            return contarPaquetesPorPrioridadRecursivo(lista, prioridadMinima, indice + 1);
        }
    }

    // Busqueda binaria dividiendo la lista ordenada a la mitad
    public int busquedaBinariaPorCodigo(List<Paquete> lista, String codigoBuscado, int izquierda, int derecha) {
        if (izquierda > derecha) {
            return -1;
        }
        int mitad = (izquierda + derecha) / 2;
        Paquete paqueteMitad = lista.get(mitad);
        int resultadoComparacion = paqueteMitad.getCodigo().compareTo(codigoBuscado);

        if (resultadoComparacion == 0) {
            return mitad;
        }
        if (resultadoComparacion > 0) {
            return busquedaBinariaPorCodigo(lista, codigoBuscado, izquierda, mitad - 1);
        } else {
            return busquedaBinariaPorCodigo(lista, codigoBuscado, mitad + 1, derecha);
        }
    }

    // Divide y venceras para retornar el paquete mas pesado
    public Paquete encontrarMasPesadoDivideYVenceras(List<Paquete> lista, int inicio, int fin) {
        if (inicio == fin) {
            return lista.get(inicio);
        }
        int mitad = (inicio + fin) / 2;
        Paquete maxIzquierdo = encontrarMasPesadoDivideYVenceras(lista, inicio, mitad);
        Paquete maxDerecho = encontrarMasPesadoDivideYVenceras(lista, mitad + 1, fin);

        if (maxIzquierdo.getPeso() >= maxDerecho.getPeso()) {
            return maxIzquierdo;
        } else {
            return maxDerecho;
        }
    }
}
