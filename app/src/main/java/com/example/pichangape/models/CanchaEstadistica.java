package com.example.pichangape.models;
/**
 * Clase CanchaEstadistica
 *
 * Esta clase representa las estadísticas de una cancha de fútbol, incluyendo:
 * - Identificador de la cancha
 * - Nombre de la cancha
 * - Ganancias generadas
 * - Total de reservas realizadas
 * - Total de reservas pagadas
 *
 * Se utiliza en la aplicación para mostrar las estadísticas en la interfaz de usuario.
 */

public class CanchaEstadistica {
    private String idCancha;
    private String nombre;
    private double ganancias;
    private int totalReservas;
    private int totalReservasPagadas;

    public CanchaEstadistica(String idCancha, String nombre, double ganancias, int totalReservas, int totalReservasPagadas) {
        this.idCancha = idCancha;
        this.nombre = nombre;
        this.ganancias = ganancias;
        this.totalReservas = totalReservas;
        this.totalReservasPagadas = totalReservasPagadas;
    }

    // Getters
    public String getIdCancha() { return idCancha; }
    public String getNombre() { return nombre; }
    public double getGanancias() { return ganancias; }
    public int getTotalReservas() { return totalReservas; }
    public int getTotalReservasPagadas() { return totalReservasPagadas; }
}
