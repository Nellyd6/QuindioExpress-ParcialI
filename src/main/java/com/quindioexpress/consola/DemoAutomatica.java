package com.quindioexpress.consola;

import com.quindioexpress.excepciones.ReglaNegocioException;
import com.quindioexpress.modelo.Paquete;
import com.quindioexpress.modelo.Repartidor;
import com.quindioexpress.servicios.CentroDistribucion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Demostración automática completa del sistema (opción 1 del menú).
 *
 * <p>Ejecuta, sin intervención del usuario, un recorrido por todas las
 * capacidades: consultas, colecciones, atención FIFO, despacho por prioridad,
 * ordenamientos, algoritmos, lista propia, ejemplos exactos, casos de error,
 * asignación/entrega e historial.</p>
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
        ordenamientosAlternativos();
        algoritmosAdicionales();
        new DemoListaPropia().ejecutar();
        new DemoEjemplosExactos().ejecutar();
        casosDeError();
        asignacionYEntrega();
        historialDeEntregas();
        consultaRepartidor();
        System.out.println("\n=== FIN DE LA DEMOSTRACIÓN ===");
    }

    private void resumenCarga() {
        System.out.println("\n1) DATOS INICIALES CARGADOS");
        System.out.println("   - Paquetes registrados: " + centro.totalPaquetesRegistrados());
        System.out.println("   - Repartidores registrados: " + centro.totalRepartidores());
        Paquete sinPrioridad = new Paquete("PQ000", "Armenia", 1.0, 10);
        System.out.println("   - Paquete sin indicar prioridad -> " + sinPrioridad
                + "  (prioridad por defecto = " + Paquete.PRIORIDAD_POR_DEFECTO + ")");
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

    private void ordenamientosAlternativos() {
        System.out.println("\n7) ORDENAMIENTOS: NATURAL (Comparable) Y ALTERNATIVOS (Comparator)");
        System.out.println("   - Natural (código asc, Comparable): " + codigos(centro.ordenarNatural(), null));
        System.out.println("   - Por prioridad (mayor->menor):     " + codigos(centro.ordenarPorPrioridad(), "prioridad"));
        System.out.println("   - Por peso (mayor->menor):          " + codigos(centro.ordenarPorPeso(), "peso"));
        System.out.println("   - Por tiempo (menor->mayor):        " + codigos(centro.ordenarPorTiempo(), "tiempo"));
        System.out.println("   - Orden de registro intacto: " + centro.obtenerOrdenLlegada().size() + " paquetes");
    }

    private void algoritmosAdicionales() {
        System.out.println("\n8) ALGORITMOS ADICIONALES");
        double pesoArmenia = centro.pesoTotalPorMunicipio("Armenia");
        System.out.println("   - Peso total recursivo para Armenia: " + pesoArmenia + " kg");

        int prioritarios = centro.contarPrioridadMinima(4);
        System.out.println("   - Cantidad de paquetes con prioridad >= 4: " + prioritarios);

        String codigoBuscado = "PQ731";
        int posBinaria = centro.buscarBinariaPorCodigo(codigoBuscado);
        System.out.println("   - Búsqueda binaria de " + codigoBuscado + ". Posición: " + posBinaria);

        Paquete masPesado = centro.paqueteMayorPeso();
        System.out.println("   - Paquete más pesado (Divide y Vencerás): " + masPesado.getCodigo()
                + " (" + masPesado.getPeso() + " kg)");
    }

    private void casosDeError() {
        System.out.println("\n9) CASOS DE ERROR (validaciones y rechazos)");

        // Errores de construcción del modelo
        probarPaqueteInvalido("peso <= 0", () -> new Paquete("PQ900", "Armenia", 0.0, 3, 5));
        probarPaqueteInvalido("prioridad fuera de rango", () -> new Paquete("PQ901", "Armenia", 1.0, 9, 5));
        probarPaqueteInvalido("destino vacío", () -> new Paquete("PQ902", "", 1.0, 3, 5));
        probarPaqueteInvalido("tiempo <= 0", () -> new Paquete("PQ903", "Armenia", 1.0, 3, 0));

        // Duplicados
        String codigoExistente = centro.obtenerOrdenLlegada().get(0).getCodigo();
        try {
            new Paquete(codigoExistente, "Armenia", 1.0, 3, 5);
            centro.registrarNuevoPaquete(new Paquete(codigoExistente, "Armenia", 1.0, 3, 5));
            System.out.println("   - Código duplicado -> NO se rechazó (incorrecto)");
        } catch (ReglaNegocioException e) {
            System.out.println("   - Código duplicado -> rechazado: " + e.getMessage());
        }
        try {
            centro.registrarRepartidor(new Repartidor("REP-01", "Duplicado", "Armenia"));
            System.out.println("   - Identificación de repartidor duplicada -> NO se rechazó (incorrecto)");
        } catch (ReglaNegocioException e) {
            System.out.println("   - Identificación duplicada -> rechazada: " + e.getMessage());
        }

        // Rechazos de asignación
        Repartidor noDisponible = centro.consultarRepartidor("REP-02");
        Paquete calarca = new Paquete("PQ950", "Calarcá", 1.0, 3, 5);
        System.out.println("   - Asignación a repartidor no disponible -> "
                + (centro.realizarAsignacion(calarca, noDisponible) ? "aceptada (incorrecto)" : "rechazada"));

        Repartidor deOtraZona = centro.consultarRepartidor("REP-01"); // Armenia
        System.out.println("   - Asignación con zona distinta al destino -> "
                + (centro.realizarAsignacion(calarca, deOtraZona) ? "aceptada (incorrecto)" : "rechazada"));

        Paquete filandia = new Paquete("PQ951", "Filandia", 1.0, 3, 5);
        Repartidor sinCobertura = centro.buscarRepartidorDisponiblePara(filandia);
        System.out.println("   - Municipio sin repartidor (Filandia) -> "
                + (sinCobertura == null ? "no hay repartidor disponible (correcto)" : "hay repartidor (incorrecto)"));
    }

    private void probarPaqueteInvalido(String caso, java.util.function.Supplier<Paquete> creador) {
        try {
            creador.get();
            System.out.println("   - " + caso + " -> NO se rechazó (incorrecto)");
        } catch (ReglaNegocioException e) {
            System.out.println("   - " + caso + " -> rechazado: " + e.getMessage());
        }
    }

    private void asignacionYEntrega() {
        System.out.println("\n10) ASIGNACIÓN Y ENTREGA (paquete -> repartidor -> historial)");
        int asignados = 0;
        int sinRepartidor = 0;
        for (Paquete paquete : new ArrayList<>(centro.obtenerOrdenLlegada())) {
            if (centro.procesarAsignacionYEntrega(paquete)) {
                asignados++;
                System.out.println("   -> " + paquete.getCodigo() + " (" + paquete.getDestino()
                        + ") asignado y enviado al historial");
            } else {
                sinRepartidor++;
                System.out.println("   -x " + paquete.getCodigo() + " (" + paquete.getDestino()
                        + ") sin repartidor disponible");
            }
        }
        System.out.println("   Paquetes asignados y entregados: " + asignados);
        System.out.println("   Paquetes sin repartidor: " + sinRepartidor);
        System.out.println("   Entregas en el historial: " + centro.totalEntregas());
        System.out.println("   Paquetes aún pendientes: " + centro.totalPaquetesRegistrados());
        System.out.println("   Pendientes de despacho: " + centro.cantidadPendientesDespacho());
    }

    private void historialDeEntregas() {
        System.out.println("\n11) HISTORIAL DE ENTREGAS (for-each sobre la lista enlazada propia)");
        for (Paquete entregado : centro.getHistorialEntregas().getHistorial()) {
            System.out.println("   - " + entregado.getCodigo() + " (" + entregado.getDestino() + ")");
        }
    }

    private void consultaRepartidor() {
        System.out.println("\n12) CONSULTA DE REPARTIDOR POR IDENTIFICACIÓN (HashMap O(1))");
        System.out.println("   - consultarRepartidor(\"REP-01\") -> " + centro.consultarRepartidor("REP-01"));
        System.out.println("   - consultarRepartidor(\"REP-99\") -> " + centro.consultarRepartidor("REP-99"));
    }

    /**
     * Compone una cadena con el código de cada paquete y, opcionalmente, el valor
     * del criterio de ordenamiento entre paréntesis.
     */
    private List<String> codigos(List<Paquete> paquetes, String criterio) {
        List<String> resultado = new ArrayList<>();
        for (Paquete paquete : paquetes) {
            String etiqueta = paquete.getCodigo();
            if ("prioridad".equals(criterio)) {
                etiqueta += "(P" + paquete.getPrioridad() + ")";
            } else if ("peso".equals(criterio)) {
                etiqueta += "(" + paquete.getPeso() + " kg)";
            } else if ("tiempo".equals(criterio)) {
                etiqueta += "(" + paquete.getTiempoEstimado() + " min)";
            }
            resultado.add(etiqueta);
        }
        return resultado;
    }
}
