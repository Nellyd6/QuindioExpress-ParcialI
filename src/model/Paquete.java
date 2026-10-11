package model;

public class Paquete {
    private String codigo;
    private String destino;
    private double peso;
    private int prioridad;

    // Constructor: El bloque que inicializa el objeto cuando lo creamos
    public Paquete(String codigo, String destino, double peso, int prioridad) {
        this.codigo = codigo;
        this.destino = destino;
        this.peso = peso;
        this.prioridad = prioridad;
    }

    // Getters: Métodos simples para que tus algoritmos puedan leer los datos desde fuera
    public String getCodigo() { return codigo; }
    public String getDestino() { return destino; }
    public double getPeso() { return peso; }
    public int getPrioridad() { return prioridad; }
}
