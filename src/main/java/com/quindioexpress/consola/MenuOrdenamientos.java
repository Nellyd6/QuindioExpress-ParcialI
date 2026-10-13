package com.quindioexpress.consola;

import com.quindioexpress.modelo.Paquete;
import com.quindioexpress.servicios.CentroDistribucion;

import java.util.List;

/**
 * Opciones del menú que ejercitan el orden natural ({@code Comparable}) y los
 * criterios alternativos ({@code Comparator}).
 *
 * <p>Todos los ordenamientos se aplican sobre <b>copias</b>: nunca se reordena la
 * lista de registro original.</p>
 */
public class MenuOrdenamientos {

    private final CentroDistribucion centro;

    public MenuOrdenamientos(CentroDistribucion centro) {
        this.centro = centro;
    }

    /** Muestra el orden natural y los tres criterios alternativos. */
    public void verTodos() {
        System.out.println("\n-- Ordenamientos (sobre copias; el orden de registro no cambia) --");
        System.out.println("  Natural (código ascendente, Comparable):");
        System.out.println("    " + codigosCon(centro.ordenarNatural(), "", "", ""));
        System.out.println("  Por prioridad (mayor a menor, ComparadorPrioridad):");
        System.out.println("    " + codigosCon(centro.ordenarPorPrioridad(), "(P", ")", "prioridad"));
        System.out.println("  Por peso (mayor a menor, ComparadorPeso):");
        System.out.println("    " + codigosCon(centro.ordenarPorPeso(), "(", " kg)", "peso"));
        System.out.println("  Por tiempo (menor a mayor, ComparadorTiempo):");
        System.out.println("    " + codigosCon(centro.ordenarPorTiempo(), "(", " min)", "tiempo"));
    }

    /**
     * Compone una cadena con el código de cada paquete y, opcionalmente, el valor
     * de la clave de ordenamiento entre etiquetas.
     */
    private String codigosCon(List<Paquete> paquetes, String prefijo, String sufijo, String clave) {
        StringBuilder constructor = new StringBuilder();
        for (int i = 0; i < paquetes.size(); i++) {
            if (i > 0) {
                constructor.append(", ");
            }
            Paquete paquete = paquetes.get(i);
            constructor.append(paquete.getCodigo());
            switch (clave) {
                case "prioridad":
                    constructor.append(prefijo).append(paquete.getPrioridad()).append(sufijo);
                    break;
                case "peso":
                    constructor.append(prefijo).append(paquete.getPeso()).append(sufijo);
                    break;
                case "tiempo":
                    constructor.append(prefijo).append(paquete.getTiempoEstimado()).append(sufijo);
                    break;
                default:
                    // Orden natural: solo el código
                    break;
            }
        }
        return constructor.toString();
    }
}
