package com.example.pichangape.models;

public class Reserva {
    private int idReserva;
    private String fechaInicio;
    private String horaInicio;
    private String horaFin;
    private String estadoReserva;

    public Reserva(int idReserva, String fechaInicio, String horaInicio, String horaFin, String estadoReserva) {
        this.idReserva = idReserva;
        this.fechaInicio = fechaInicio;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estadoReserva = estadoReserva;
    }

    // Getters
    public int getIdReserva() {
        return idReserva;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public String getEstadoReserva() {
        return estadoReserva;
    }
}
