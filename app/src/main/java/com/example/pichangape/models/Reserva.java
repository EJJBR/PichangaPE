package com.example.pichangape.models;

public class Reserva {
    private String fechaInicio;
    private String horaInicio;
    private String horaFin;
    private String estadoReserva;

    public Reserva(String fechaInicio, String horaInicio, String horaFin, String estadoReserva) {
        this.fechaInicio = fechaInicio;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estadoReserva = estadoReserva;
    }

    // Getters y setters
    public String getFechaInicio() { return fechaInicio; }
    public String getHoraInicio() { return horaInicio; }
    public String getHoraFin() { return horaFin; }
    public String getEstadoReserva() { return estadoReserva; }
}
