package com.quindioexpress.principal;

import com.quindioexpress.modelo.Paquete;
import com.quindioexpress.servicios.CentroDistribucion;
import com.quindioexpress.servicios.GestorDespachoPrioritario;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Demostración integral y única de QuindíoExpress.
 *
 * <p>Reúne en una sola clase todas las demostraciones del sistema: consultas,
 * ordenamientos, atención FIFO, despacho por prioridad, asignación/entrega e
 * historial.</p>
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=== CENTRO DE DISTRIBUCIÓN QUINDÍO EXPRESS — DEMOSTRACIÓN INTEGRAL ===\n");

        CentroDistribucion centro = new CentroDistribucion();
        centro.cargarDatosIniciales();

        resumenCarga(centro);
        consultaPorCodigo(centro);
        municipios(centro);
        agrupacionPorMunicipio(centro);
        atencionPorOrdenDeLlegada(centro, 5);
        despachoPorPrioridad(centro, 10);
        verificacionOrdenDeDespacho();
        asignacionYEntrega(centro);
        historialDeEntregas(centro);

        System.out.println("\n=== FIN DE LA DEMOSTRACIÓN ===");
    }

    private static void resumenCarga(CentroDistribucion centro) {
        System.out.println("1) DATOS INICIALES CARGADOS");
        System.out.println("   - Paquetes registrados: " + centro.totalPaquetesRegistrados());
        System.out.println("   - Repartidores registrados: " + centro.totalRepartidores());
    }

    private static void consultaPorCodigo(CentroDistribucion centro) {
        System.out.println("\n2) CONSULTA POR CÓDIGO (HashMap O(1))");
        String codigo = centro.obtenerOrdenLlegada().get(0).getCodigo();
        System.out.println("   - buscarPorCodigo(\"" + codigo + "\") -> " + centro.buscarPorCodigo(codigo));
        System.out.println("   - buscarPorCodigo(\"XX000\") -> " + centro.buscarPorCodigo("XX000"));
    }

    private static void municipios(CentroDistribucion centro) {
        System.out.println("\n3) MUNICIPIOS");
        System.out.println("   - Únicos (HashSet, tamaño=" + centro.obtenerMunicipiosUnicos().size() + ")");
        System.out.println("   - Ordenados (TreeSet): " + centro.obtenerMunicipiosOrdenados());
    }

    private static void agrupacionPorMunicipio(CentroDistribucion centro) {
        System.out.println("\n4) PAQUETES AGRUPADOS POR MUNICIPIO (Map<String, Set<Paquete>>)");
        for (Map.Entry<String, Set<Paquete>> entry : centro.obtenerAgrupacionPorMunicipio().entrySet()) {
            System.out.println("   - " + entry.getKey() + ": " + entry.getValue().size() + " paquete(s)");
        }
    }

    private static void atencionPorOrdenDeLlegada(CentroDistribucion centro, int cantidad) {
        System.out.println("\n5) ATENCIÓN POR ORDEN DE LLEGADA (FIFO) - primeros " + cantidad);
        for (int i = 0; i < cantidad; i++) {
            Paquete atendido = centro.atenderPorOrdenLlegada();
            if (atendido != null) {
                System.out.println("   -> " + atendido.getCodigo() + " (" + atendido.getDestino() + ")");
            }
        }
    }

    private static void despachoPorPrioridad(CentroDistribucion centro, int cantidad) {
        System.out.println("\n6) DESPACHO POR PRIORIDAD (PriorityQueue) - primeros " + cantidad);
        for (int i = 0; i < cantidad; i++) {
            Paquete despachado = centro.despacharPorPrioridad();
            if (despachado != null) {
                System.out.println("   -> " + despachado.getCodigo()
                        + " (Prioridad: " + despachado.getPrioridad()
                        + ", Tiempo: " + despachado.getTiempoEstimado() + "h)");
            }
        }
        System.out.println("   Pendientes de despacho: " + centro.cantidadPendientesDespacho());
    }

    /**
     * Comprueba y muestra el orden exacto de despacho del ejemplo del enunciado
     * y las tres reglas de desempate (prioridad ↓, tiempo ↑, código ↑).
     *
     * <p>Si el orden no coincide, informa el fallo por consola sin interrumpir
     * la ejecución del programa.</p>
     */
    private static void verificacionOrdenDeDespacho() {
        System.out.println("\n7) VERIFICACIÓN DEL ORDEN DE DESPACHO (reglas de desempate)");

        GestorDespachoPrioritario gestor = new GestorDespachoPrioritario();

        // Datos diseñados para ejercitar las tres reglas, encolados a propósito
        // en orden desordenado.
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

    private static void asignacionYEntrega(CentroDistribucion centro) {
        System.out.println("\n8) ASIGNACIÓN Y ENTREGA (paquete -> repartidor -> historial)");
        int asignados = 0;
        for (Paquete paquete : centro.obtenerOrdenLlegada()) {
            if (centro.procesarAsignacionYEntrega(paquete)) {
                asignados++;
                System.out.println("   -> " + paquete.getCodigo() + " (" + paquete.getDestino()
                        + ") asignado y enviado al historial");
            }
        }
        System.out.println("   Paquetes asignados y entregados: " + asignados);
        System.out.println("   Entregas en el historial: " + centro.totalEntregas());
    }

    private static void historialDeEntregas(CentroDistribucion centro) {
        System.out.println("\n9) HISTORIAL DE ENTREGAS (for-each sobre la lista enlazada propia)");
        for (Paquete entregado : centro.getHistorialEntregas().getHistorial()) {
            System.out.println("   - " + entregado.getCodigo() + " (" + entregado.getDestino() + ")");
        }
    }
}
