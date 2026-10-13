package com.quindioexpress.modelo;

import com.quindioexpress.excepciones.ReglaNegocioException;

import java.util.Objects;

/**
 * Entidad que representa a un repartidor.
 *
 * <p>La identificación, el nombre y la zona son inmutables; el único estado
 * mutable es {@code disponible}. Las validaciones se ejecutan en el
 * constructor y lanzan {@link ReglaNegocioException} con mensaje descriptivo.</p>
 */
public class Repartidor {

    private final String idRepartidor;
    private final String nombre;
    private final String zonaAsignada;
    private boolean disponible;

    /**
     * @param idRepartidor identificación única; no nula ni vacía.
     * @param nombre       nombre; no nulo ni vacío.
     * @param zonaAsignada municipio de la zona; no nulo ni vacío.
     * @throws ReglaNegocioException si algún valor viola las reglas.
     */
    public Repartidor(String idRepartidor, String nombre, String zonaAsignada) {
        this.idRepartidor = validar(idRepartidor, "La identificación del repartidor no puede estar vacía.");
        this.nombre = validar(nombre, "El nombre del repartidor no puede estar vacío.");
        this.zonaAsignada = validar(zonaAsignada, "La zona del repartidor no puede estar vacía.");
        this.disponible = true;
    }

    private static String validar(String valor, String mensaje) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ReglaNegocioException(mensaje);
        }
        return valor.trim();
    }

    public String getIdRepartidor() {
        return idRepartidor;
    }

    public String getNombre() {
        return nombre;
    }

    public String getZonaAsignada() {
        return zonaAsignada;
    }

    public boolean isDisponible() {
        return disponible;
    }

    /** Único mutador permitido: cambia la disponibilidad del repartidor. */
    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Repartidor that = (Repartidor) o;
        return Objects.equals(idRepartidor, that.idRepartidor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idRepartidor);
    }

    @Override
    public String toString() {
        return "Repartidor{" +
                "idRepartidor='" + idRepartidor + '\'' +
                ", nombre='" + nombre + '\'' +
                ", zonaAsignada='" + zonaAsignada + '\'' +
                ", disponible=" + disponible +
                '}';
    }
}
