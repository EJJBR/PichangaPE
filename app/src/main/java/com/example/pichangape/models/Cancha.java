package com.example.pichangape.models;

public class Cancha {
    private String idCancha;
    private String nombre;
    private String ubicacion;
    private String precioHora;

    public Cancha(String idCancha, String nombre, String ubicacion, float precioHora) {
        this.idCancha = idCancha;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.precioHora=String.format(String.valueOf(precioHora));
    }

    public String getIdCancha() { return idCancha; }
    public String getNombre() { return nombre; }
    public String getUbicacion() { return ubicacion; }

    public String getPrecioHora() {
        return precioHora;
    }
}

