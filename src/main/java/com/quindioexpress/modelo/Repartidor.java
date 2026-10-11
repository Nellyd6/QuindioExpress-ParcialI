package com.quindioexpress.modelo;

import java.util.Objects;


public class Repartidor {
    private String idRepartidor;
    private String nombre;
    private String zonaAsignada;
    private boolean disponible;

    public Repartidor(String idRepartidor, String nombre, String zonaAsignada) {
        setIdRepartidor(idRepartidor);
        setNombre(nombre);
        setZonaAsignada(zonaAsignada);
        this.disponible = true; // Por defecto inicia disponible
    }

    public String getIdRepartidor() {
        return idRepartidor;
    }

    public void setIdRepartidor(String idRepartidor) {
        if (idRepartidor == null || idRepartidor.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del repartidor no puede estar vacío.");
        }
        this.idRepartidor = idRepartidor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del repartidor no puede estar vacío.");
        }
        this.nombre = nombre;
    }

    public String getZonaAsignada() {
        return zonaAsignada;
    }

    public void setZonaAsignada(String zonaAsignada) {
        if (zonaAsignada == null || zonaAsignada.trim().isEmpty()) {
            throw new IllegalArgumentException("La zona asignada no puede estar vacía.");
        }
        this.zonaAsignada = zonaAsignada;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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
                "id='" + idRepartidor + '\'' +
                ", nombre='" + nombre + '\'' +
                ", zona='" + zonaAsignada + '\'' +
                ", disponible=" + disponible +
                '}';
    }
}
