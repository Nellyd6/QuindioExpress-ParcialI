package com.quindioexpress.consola;

import com.quindioexpress.servicios.CentroDistribucion;

import java.util.ArrayList;
import java.util.List;

/**
 * Menú interactivo principal de QuindíoExpress.
 *
 * <p>Registra cada opción junto con su acción (referencia a método) y ejecuta
 * el bucle de atención. Cada operación vive en su propia clase de dominio
 * ({@link MenuPaquetes}, {@link MenuMunicipios}, {@link MenuDespacho},
 * {@link MenuRepartidores}, {@link MenuAlgoritmos}) y la demostración completa
 * en {@link DemoAutomatica}.</p>
 */
public class MenuPrincipal {

    private final EntradaConsola entrada;
    private final List<Opcion> opciones = new ArrayList<>();

    public MenuPrincipal(CentroDistribucion centro, EntradaConsola entrada) {
        this.entrada = entrada;

        DemoAutomatica demo = new DemoAutomatica(centro);
        MenuPaquetes paquetes = new MenuPaquetes(centro, entrada);
        MenuMunicipios municipios = new MenuMunicipios(centro, entrada);
        MenuDespacho despacho = new MenuDespacho(centro, entrada);
        MenuRepartidores repartidores = new MenuRepartidores(centro, entrada);
        MenuAlgoritmos algoritmos = new MenuAlgoritmos(centro, entrada);
        MenuOrdenamientos ordenamientos = new MenuOrdenamientos(centro);
        DemoListaPropia listaPropia = new DemoListaPropia();
        DemoEjemplosExactos ejemplosExactos = new DemoEjemplosExactos();

        registrar(1, "Ejecutar demostración automática completa", demo::ejecutar);
        registrar(2, "Listar paquetes en orden de llegada", paquetes::listar);
        registrar(3, "Registrar paquete nuevo", paquetes::registrar);
        registrar(4, "Consultar paquete por código (HashMap O(1))", paquetes::consultar);
        registrar(5, "Ver municipios (únicos y ordenados)", municipios::verMunicipios);
        registrar(6, "Ver agrupación de paquetes por municipio", municipios::verAgrupacion);
        registrar(7, "Ver paquetes de un municipio", municipios::verPaquetesDeMunicipio);
        registrar(8, "Atender por orden de llegada (FIFO)", despacho::atenderFifo);
        registrar(9, "Despachar por prioridad (PriorityQueue)", despacho::despacharPrioridad);
        registrar(10, "Ver repartidores", repartidores::ver);
        registrar(11, "Registrar repartidor nuevo", repartidores::registrar);
        registrar(12, "Asignar y entregar paquete por código", repartidores::asignarYEntregar);
        registrar(13, "Ver historial de entregas", repartidores::verHistorial);
        registrar(14, "Peso total de un municipio (recursivo)", algoritmos::pesoTotal);
        registrar(15, "Contar paquetes por prioridad mínima (recursivo)", algoritmos::contarPrioridad);
        registrar(16, "Búsqueda binaria por código", algoritmos::busquedaBinaria);
        registrar(17, "Paquete de mayor peso (divide y vencerás)", algoritmos::mayorPeso);
        registrar(18, "Consultar repartidor por identificación (HashMap O(1))", repartidores::consultar);
        registrar(19, "Ver ordenamientos: natural, prioridad, peso y tiempo (Comparator)", ordenamientos::verTodos);
        registrar(20, "Demostrar lista enlazada propia (casos límite)", listaPropia::ejecutar);
        registrar(21, "Ver ejemplos exactos del enunciado (despacho, recursivos, binaria, DyV)", ejemplosExactos::ejecutar);
    }

    /** Agrega una opción al menú. */
    private void registrar(int numero, String descripcion, Accion accion) {
        opciones.add(new Opcion(numero, descripcion, accion));
    }

    /** Inicia el bucle interactivo hasta que el usuario elija salir o acabe la entrada. */
    public void iniciar() {
        boolean salir = false;
        while (!salir) {
            mostrar();
            int numero = entrada.leerEntero("Seleccione una opción: ");
            if (numero == Integer.MIN_VALUE) { // fin de entrada (EOF)
                break;
            }
            if (numero == 0) {
                salir = true;
                continue;
            }
            Opcion opcion = buscar(numero);
            if (opcion == null) {
                System.out.println("  Opción inválida. Intente de nuevo.");
            } else {
                opcion.accion().ejecutar();
            }
        }
        System.out.println("\nSaliendo del sistema QuindíoExpress. ¡Hasta pronto!");
    }

    private Opcion buscar(int numero) {
        for (Opcion opcion : opciones) {
            if (opcion.numero() == numero) {
                return opcion;
            }
        }
        return null;
    }

    private void mostrar() {
        System.out.println("\n===================== MENÚ PRINCIPAL =====================");
        for (Opcion opcion : opciones) {
            System.out.printf("  %2d. %s%n", opcion.numero(), opcion.descripcion());
        }
        System.out.println("   0. Salir");
        System.out.println("===========================================================");
    }
}
