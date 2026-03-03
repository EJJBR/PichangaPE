package com.example.pichangape.models;

public class Cancha {
    private String idCancha;
    private String nombre;
    private String ubicacion;
    private String precioHora;
    private String numYape;
    private String numTransfer;
    private String horasDisponibles;
    private String fechasAbiertas;

    // Constructor básico
    public Cancha(String idCancha, String nombre, String ubicacion, float precioHora) {
        this.idCancha = idCancha;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.precioHora = String.valueOf(precioHora);
    }

    // Constructor completo con datos de pago y disponibilidad
    public Cancha(String idCancha, String nombre, String ubicacion, float precioHora, 
                  String numYape, String numTransfer, String horasDisponibles, String fechasAbiertas) {
        this.idCancha = idCancha;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.precioHora = String.valueOf(precioHora);
        this.numYape = numYape;
        this.numTransfer = numTransfer;
        this.horasDisponibles = horasDisponibles;
        this.fechasAbiertas = fechasAbiertas;
    }

    public String getIdCancha() { return idCancha; }
    public String getNombre() { return nombre; }
    public String getUbicacion() { return ubicacion; }
    public String getPrecioHora() { return precioHora; }
    public String getNumYape() { return numYape; }
    public String getNumTransfer() { return numTransfer; }
    public String getHorasDisponibles() { return horasDisponibles; }
    public String getFechasAbiertas() { return fechasAbiertas; }
}
