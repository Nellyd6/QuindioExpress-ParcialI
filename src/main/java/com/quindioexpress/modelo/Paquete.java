package com.quindioexpress.modelo;

import java.util.Objects;

public class Paquete implements Comparable<Paquete> {

    private String codigo;
    private String destino;
    private double peso;
    private int prioridad;
    private double tiempo;

    public Paquete(String codigo, String destino, double peso, int prioridad, double tiempo) {
        setCodigo(codigo);
        setDestino(destino);
        setPeso(peso);
        setPrioridad(prioridad);
        setTiempoEstimado(tiempo);
    }


    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El código del paquete no puede estar vacío.");
        }
        this.codigo = codigo;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        if (destino == null || destino.trim().isEmpty()) {
            throw new IllegalArgumentException("El destino no puede estar vacío.");
        }
        this.destino = destino;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        if (peso <= 0) {
            throw new IllegalArgumentException("El peso debe ser mayor a 0.");
        }
        this.peso = peso;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        if (prioridad < 1 || prioridad > 5) {
            throw new IllegalArgumentException("La prioridad debe estar entre 1 y 5.");
        }
        this.prioridad = prioridad;
    }

    public double getTiempoEstimado() {
        return tiempo;
    }

    public void setTiempoEstimado(double tiempoEstimado) {
        if (tiempoEstimado <= 0) {
            throw new IllegalArgumentException("El tiempo estimado debe ser mayor a 0.");
        }
        this.tiempo = tiempoEstimado;
    }

    //ComparareTo para el orden de los paquetes basado en si codigo de forma ascendente
    @Override
    public int compareTo(Paquete otro) {
        if (otro == null) {
            return 1;
        }
        return this.codigo.compareTo(otro.codigo);
    }

    //Compara dos paquetes basandose en su codigo
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Paquete paquete = (Paquete) o;
        return Objects.equals(codigo, paquete.codigo);
    }
    //
    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return "Paquete{" +
                "codigo='" + codigo + '\'' +
                ", destino='" + destino + '\'' +
                ", peso=" + peso +
                ", prioridad=" + prioridad +
                ", tiempoEstimado=" + tiempo +
                '}';
    }
}



