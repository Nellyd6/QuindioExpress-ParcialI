package com.quindioexpress.consola;

import java.util.Scanner;

/**
 * Encapsula la lectura de datos por consola con validaciones básicas.
 *
 * <p>Centraliza el uso de {@link Scanner} para que el resto de las clases del
 * menú no manipulen la entrada estándar directamente. Si la entrada se agota
 * (EOF), los métodos devuelven un valor centinela para que el menú pueda
 * terminar de forma limpia.</p>
 */
public class EntradaConsola implements AutoCloseable {

    private final Scanner scanner;

    public EntradaConsola() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Lee una línea completa y la recorta.
     *
     * @param mensaje texto a mostrar antes de leer.
     * @return la línea leída, o {@code null} si ya no hay entrada (EOF).
     */
    public String leerLinea(String mensaje) {
        System.out.print(mensaje);
        if (!scanner.hasNextLine()) {
            return null;
        }
        return scanner.nextLine().trim();
    }

    /**
     * Lee un texto no vacío.
     *
     * @return el texto, o {@code null} si no hay entrada (EOF).
     */
    public String leerTexto(String mensaje) {
        while (true) {
            String linea = leerLinea(mensaje);
            if (linea == null) {
                return null;
            }
            if (!linea.isEmpty()) {
                return linea;
            }
            System.out.println("  El valor no puede estar vacío.");
        }
    }

    /**
     * Lee un entero, repitiendo hasta obtener un valor válido.
     *
     * @return el entero leído, o {@link Integer#MIN_VALUE} si no hay entrada (EOF).
     */
    public int leerEntero(String mensaje) {
        while (true) {
            String linea = leerLinea(mensaje);
            if (linea == null) {
                return Integer.MIN_VALUE;
            }
            if (linea.isEmpty()) {
                System.out.println("  Debe ingresar un número.");
                continue;
            }
            try {
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.println("  '" + linea + "' no es un número entero válido.");
            }
        }
    }

    /**
     * Lee un número decimal (acepta coma o punto), repitiendo hasta ser válido.
     *
     * @return el número leído, o {@link Double#NaN} si no hay entrada (EOF).
     */
    public double leerDouble(String mensaje) {
        while (true) {
            String linea = leerLinea(mensaje);
            if (linea == null) {
                return Double.NaN;
            }
            try {
                return Double.parseDouble(linea.replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("  '" + linea + "' no es un número válido.");
            }
        }
    }

    @Override
    public void close() {
        scanner.close();
    }
}
