package com.quindioexpress.principal;

import com.quindioexpress.consola.EntradaConsola;
import com.quindioexpress.consola.MenuPrincipal;
import com.quindioexpress.servicios.CentroDistribucion;

/**
 * Punto de entrada de QuindíoExpress.
 *
 * <p>Solo se encarga de cargar los datos iniciales e iniciar el menú
 * interactivo. Toda la lógica del menú está separada en el paquete
 * {@code com.quindioexpress.consola}.</p>
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("==============================================================");
        System.out.println("       CENTRO DE DISTRIBUCIÓN QUINDÍO EXPRESS");
        System.out.println("==============================================================");

        CentroDistribucion centro = new CentroDistribucion();
        centro.cargarDatosIniciales();
        System.out.println("Datos iniciales cargados: "
                + centro.totalPaquetesRegistrados() + " paquetes y "
                + centro.totalRepartidores() + " repartidores.");

        try (EntradaConsola entrada = new EntradaConsola()) {
            new MenuPrincipal(centro, entrada).iniciar();
        }
    }
}
