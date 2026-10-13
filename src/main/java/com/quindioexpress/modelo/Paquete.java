package com.quindioexpress.modelo;

import com.quindioexpress.excepciones.ReglaNegocioException;

import java.util.Objects;

/**
 * Entidad que representa un paquete.
 *
 * <p>Es <b>inmutable</b>: todos sus atributos son {@code final} y solo se
 * exponen getters. Las validaciones de negocio se ejecutan en el constructor y
 * lanzan {@link ReglaNegocioException} si algún valor no cumple las reglas.</p>
 *
 * <p>El orden natural ({@link Comparable}) es por <b>código ascendente</b>, y es
 * coherente con {@code equals}/{@code hashCode}.</p>
 */
public class Paquete implements Comparable<Paquete> {

    /** Prioridad mínima válida. */
    public static final int PRIORIDAD_MIN = 1;

    /** Prioridad máxima válida. */
    public static final int PRIORIDAD_MAX = 5;

    /** Prioridad asignada cuando no se indica. */
    public static final int PRIORIDAD_POR_DEFECTO = 3;

    private final String codigo;
    private final String destino;
    private final double peso;
    private final int prioridad;
    private final int tiempoEstimado;

    /**
     * Constructor completo.
     *
     * @param codigo         código único; no nulo ni vacío.
     * @param destino        municipio de destino; no nulo ni vacío.
     * @param peso           peso en kg; mayor que 0.
     * @param prioridad      prioridad entre {@value #PRIORIDAD_MIN} y {@value #PRIORIDAD_MAX}.
     * @param tiempoEstimado tiempo estimado en minutos; entero mayor que 0.
     * @throws ReglaNegocioException si algún valor viola las reglas.
     */
    public Paquete(String codigo, String destino, double peso, int prioridad, int tiempoEstimado) {
        this.codigo = validarCodigo(codigo);
        this.destino = validarDestino(destino);
        this.peso = validarPeso(peso);
        this.prioridad = validarPrioridad(prioridad);
        this.tiempoEstimado = validarTiempo(tiempoEstimado);
    }

    /**
     * Constructor sin prioridad: usa {@link #PRIORIDAD_POR_DEFECTO}.
     *
     * @param codigo         código único; no nulo ni vacío.
     * @param destino        municipio de destino; no nulo ni vacío.
     * @param peso           peso en kg; mayor que 0.
     * @param tiempoEstimado tiempo estimado en minutos; entero mayor que 0.
     */
    public Paquete(String codigo, String destino, double peso, int tiempoEstimado) {
        this(codigo, destino, peso, PRIORIDAD_POR_DEFECTO, tiempoEstimado);
    }

    private static String validarCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new ReglaNegocioException("El código del paquete no puede estar vacío.");
        }
        return codigo.trim();
    }

    private static String validarDestino(String destino) {
        if (destino == null || destino.trim().isEmpty()) {
            throw new ReglaNegocioException("El destino del paquete no puede estar vacío.");
        }
        return destino.trim();
    }

    private static double validarPeso(double peso) {
        if (peso <= 0) {
            throw new ReglaNegocioException("El peso del paquete debe ser mayor que 0.");
        }
        return peso;
    }

    private static int validarPrioridad(int prioridad) {
        if (prioridad < PRIORIDAD_MIN || prioridad > PRIORIDAD_MAX) {
            throw new ReglaNegocioException(
                    "La prioridad debe estar entre " + PRIORIDAD_MIN + " y " + PRIORIDAD_MAX + ".");
        }
        return prioridad;
    }

    private static int validarTiempo(int tiempoEstimado) {
        if (tiempoEstimado <= 0) {
            throw new ReglaNegocioException("El tiempo estimado debe ser un entero mayor que 0.");
        }
        return tiempoEstimado;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDestino() {
        return destino;
    }

    public double getPeso() {
        return peso;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public int getTiempoEstimado() {
        return tiempoEstimado;
    }

    /** Orden natural: código ascendente. */
    @Override
    public int compareTo(Paquete otro) {
        if (otro == null) {
            return 1;
        }
        return this.codigo.compareTo(otro.codigo);
    }

    /** Igualdad por código, coherente con {@link #compareTo(Paquete)}. */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Paquete paquete = (Paquete) o;
        return Objects.equals(codigo, paquete.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return codigo + " | " + destino + " | " + peso + " kg | P" + prioridad + " | " + tiempoEstimado + " min";
    }
}
