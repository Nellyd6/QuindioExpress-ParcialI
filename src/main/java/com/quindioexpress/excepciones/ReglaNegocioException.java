package com.quindioexpress.excepciones;

/**
 * Excepción propia para las violaciones de reglas de negocio del sistema.
 *
 * <p>Se usa en todas las validaciones del dominio: duplicados de código o
 * identificación, valores fuera de rango y asignaciones no permitidas. Es una
 * {@link RuntimeException} con mensaje descriptivo.</p>
 */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
