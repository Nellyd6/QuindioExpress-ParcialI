package com.quindioexpress.consola;

import com.quindioexpress.modelo.Paquete;
import com.quindioexpress.servicios.CentroDistribucion;

import java.util.Map;
import java.util.Set;

/** Opciones del menú relacionadas con los municipios de destino. */
public class MenuMunicipios {

    private final CentroDistribucion centro;
    private final EntradaConsola entrada;

    public MenuMunicipios(CentroDistribucion centro, EntradaConsola entrada) {
        this.centro = centro;
        this.entrada = entrada;
    }

    /** Muestra los municipios únicos (HashSet) y ordenados (TreeSet). */
    public void verMunicipios() {
        System.out.println("\n-- Municipios --");
        System.out.println("  Únicos (HashSet, " + centro.obtenerMunicipiosUnicos().size() + "): "
                + centro.obtenerMunicipiosUnicos());
        System.out.println("  Ordenados (TreeSet): " + centro.obtenerMunicipiosOrdenados());
    }

    /** Muestra la agrupación de paquetes por municipio. */
    public void verAgrupacion() {
        System.out.println("\n-- Agrupación de paquetes por municipio --");
        for (Map.Entry<String, Set<Paquete>> entradaMapa : centro.obtenerAgrupacionPorMunicipio().entrySet()) {
            System.out.println("  " + entradaMapa.getKey() + ": " + entradaMapa.getValue().size() + " paquete(s)");
        }
    }

    /** Muestra los paquetes de un municipio concreto. */
    public void verPaquetesDeMunicipio() {
        String municipio = entrada.leerTexto("\nMunicipio: ");
        if (municipio == null) return;
        Set<Paquete> paquetes = centro.obtenerPaquetesPorMunicipio(municipio);
        if (paquetes.isEmpty()) {
            System.out.println("  No hay paquetes registrados para " + municipio + ".");
            return;
        }
        for (Paquete paquete : paquetes) {
            System.out.println("  " + paquete);
        }
    }
}
