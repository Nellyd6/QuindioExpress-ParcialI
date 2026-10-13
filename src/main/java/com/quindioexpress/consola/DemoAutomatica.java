package com.quindioexpress.consola;

import com.quindioexpress.modelo.Paquete;
import com.quindioexpress.servicios.CentroDistribucion;
import com.quindioexpress.servicios.GestorDespachoPrioritario;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Demostración automática completa del sistema (opción 1 del menú).
 *
 * <p>Ejecuta, sin intervención del usuario, un recorrido por todas las
 * capacidades: consultas, colecciones, atención FIFO, despacho por prioridad,
 * asignación/entrega, historial y algoritmos.</p>
 */
public class DemoAutomatica {

    private final CentroDistribucion centro;

    public DemoAutomatica(CentroDistribucion centro) {
        this.centro = centro;
    }

    /** Ejecuta la demostración completa paso a paso. */
    public void ejecutar() {
        System.out.println("\n=== DEMOSTRACIÓN AUTOMÁTICA ===");
        resumenCarga();
        consultaPorCodigo();
        municipios();
        agrupacionPorMunicipio();
        atencionPorOrdenDeLlegada(5);
        despachoPorPrioridad(10);
        verificacionOrdenDeDespacho();
        asignacionYEntrega();
        historialDeEntregas();
        algoritmosAdicionales();
        System.out.println("\n=== FIN DE LA DEMOSTRACIÓN ===");
    }

    private void resumenCarga() {
        System.out.println("\n1) DATOS INICIALES CARGADOS");
        System.out.println("   - Paquetes registrados: " + centro.totalPaquetesRegistrados());
        System.out.println("   - Repartidores registrados: " + centro.totalRepartidores());
    }

    private void consultaPorCodigo() {
        System.out.println("\n2) CONSULTA POR CÓDIGO (HashMap O(1))");
        String codigo = centro.obtenerOrdenLlegada().get(0).getCodigo();
        System.out.println("   - buscarPorCodigo(\"" + codigo + "\") -> " + centro.buscarPorCodigo(codigo));
        System.out.println("   - buscarPorCodigo(\"XX000\") -> " + centro.buscarPorCodigo("XX000"));
    }

    private void municipios() {
        System.out.println("\n3) MUNICIPIOS");
        System.out.println("   - Únicos (HashSet, tamaño=" + centro.obtenerMunicipiosUnicos().size() + ")");
        System.out.println("   - Ordenados (TreeSet): " + centro.obtenerMunicipiosOrdenados());
    }

    private void agrupacionPorMunicipio() {
        System.out.println("\n4) PAQUETES AGRUPADOS POR MUNICIPIO (Map<String, Set<Paquete>>)");
        for (Map.Entry<String, Set<Paquete>> entrada : centro.obtenerAgrupacionPorMunicipio().entrySet()) {
            System.out.println("   - " + entrada.getKey() + ": " + entrada.getValue().size() + " paquete(s)");
        }
    }

    private void atencionPorOrdenDeLlegada(int cantidad) {
        System.out.println("\n5) ATENCIÓN POR ORDEN DE LLEGADA (FIFO) - primeros " + cantidad);
        for (int i = 0; i < cantidad; i++) {
            Paquete atendido = centro.atenderPorOrdenLlegada();
            if (atendido != null) {
                System.out.println("   -> " + atendido.getCodigo() + " (" + atendido.getDestino() + ")");
            }
        }
    }

    private void despachoPorPrioridad(int cantidad) {
        System.out.println("\n6) DESPACHO POR PRIORIDAD (PriorityQueue) - primeros " + cantidad);
        for (int i = 0; i < cantidad; i++) {
            Paquete despachado = centro.despacharPorPrioridad();
            if (despachado != null) {
                System.out.println("   -> " + despachado.getCodigo()
                        + " (Prioridad: " + despachado.getPrioridad()
                        + ", Tiempo: " + despachado.getTiempoEstimado() + ")");
            }
        }
        System.out.println("   Pendientes de despacho: " + centro.cantidadPendientesDespacho());
    }

    private void verificacionOrdenDeDespacho() {
        System.out.println("\n7) VERIFICACIÓN DEL ORDEN DE DESPACHO (reglas de desempate)");

        GestorDespachoPrioritario gestor = new GestorDespachoPrioritario();

        List<Paquete> entrada = Arrays.asList(
                new Paquete("PQ942", "Calarcá", 7.5, 5, 5),
                new Paquete("PQ420", "Filandia", 2.4, 4, 1),
                new Paquete("PQ731", "Salento", 3.2, 5, 5),
                new Paquete("PQ105", "Armenia", 1.8, 5, 2));
        for (Paquete paquete : entrada) {
            gestor.encolarPaquete(paquete);
        }

        List<String> ordenObtenido = new ArrayList<>();
        while (!gestor.estaVacia()) {
            ordenObtenido.add(gestor.despacharSiguiente().getCodigo());
        }

        List<String> ordenEsperado = Arrays.asList("PQ105", "PQ731", "PQ942", "PQ420");
        System.out.println("   - Esperado: " + ordenEsperado);
        System.out.println("   - Obtenido: " + ordenObtenido);
        if (ordenObtenido.equals(ordenEsperado)) {
            System.out.println("   - Resultado: OK (prioridad desc -> tiempo asc -> código asc)");
        } else {
            System.out.println("   - Resultado: FALLO - el orden no coincide con lo esperado");
        }
    }

    private void asignacionYEntrega() {
        System.out.println("\n8) ASIGNACIÓN Y ENTREGA (paquete -> repartidor -> historial)");
        int asignados = 0;
        for (Paquete paquete : new ArrayList<>(centro.obtenerOrdenLlegada())) {
            if (centro.procesarAsignacionYEntrega(paquete)) {
                asignados++;
                System.out.println("   -> " + paquete.getCodigo() + " (" + paquete.getDestino()
                        + ") asignado y enviado al historial");
            }
        }
        System.out.println("   Paquetes asignados y entregados: " + asignados);
        System.out.println("   Entregas en el historial: " + centro.totalEntregas());
    }

    private void historialDeEntregas() {
        System.out.println("\n9) HISTORIAL DE ENTREGAS (for-each sobre la lista enlazada propia)");
        for (Paquete entregado : centro.getHistorialEntregas().getHistorial()) {
            System.out.println("   - " + entregado.getCodigo() + " (" + entregado.getDestino() + ")");
        }
    }

    private void algoritmosAdicionales() {
        System.out.println("\n10) ALGORITMOS ADICIONALES");

        List<Paquete> todosLosPaquetes = centro.obtenerOrdenLlegada();

        double pesoArmenia = centro.calcularPesoTotalRecursivo(todosLosPaquetes, "Armenia", 0);
        System.out.println("   - Peso total recursivo para Armenia: " + pesoArmenia + " kg");

        int prioritarios = centro.contarPaquetesPorPrioridadRecursivo(todosLosPaquetes, 4, 0);
        System.out.println("   - Cantidad de paquetes con prioridad >= 4: " + prioritarios);

        List<Paquete> listaOrdenadaBusqueda = new ArrayList<>(todosLosPaquetes);
        Collections.sort(listaOrdenadaBusqueda);

        String codigoBuscado = "PQ731";
        int posBinaria = centro.busquedaBinariaPorCodigo(listaOrdenadaBusqueda, codigoBuscado, 0, listaOrdenadaBusqueda.size() - 1);
        System.out.println("   - Búsqueda binaria de " + codigoBuscado + ". Posición: " + posBinaria);

        Paquete masPesado = centro.encontrarMasPesadoDivideYVenceras(todosLosPaquetes, 0, todosLosPaquetes.size() - 1);
        System.out.println("   - Paquete más pesado (Divide y Vencerás): " + masPesado.getCodigo() + " (" + masPesado.getPeso() + " kg)");
    }
}
