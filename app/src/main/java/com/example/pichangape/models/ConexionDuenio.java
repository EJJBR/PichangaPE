package com.example.pichangape.models;
import java.io.Serializable;

public class ConexionDuenio implements Serializable {
    private String id_cliente, nombre, apellido, numeroCel, correo, documento, tipoDoc, fechaNac, usuario,contrasenia, rol, numYape, numTransfer;
    public ConexionDuenio(String id_cliente, String nombre, String apellido, String numeroCel, String correo, String documento,
                          String tipoDoc, String fechaNac, String usuario, String contrasenia, String rol, String numYape, String numTransfer) {
        this.id_cliente = id_cliente;
        this.nombre = nombre;
        this.apellido = apellido;
        this.numeroCel = numeroCel;
        this.correo = correo;
        this.documento = documento;
        this.tipoDoc = tipoDoc;
        this.fechaNac = fechaNac;
        this.usuario = usuario;
        this.contrasenia=contrasenia;
        this.rol = rol;
        this.numYape = numYape;
        this.numTransfer = numTransfer;
    }

    public String getId_cliente() {
        return id_cliente;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getNumeroCel() {
        return numeroCel;
    }

    public String getCorreo() {
        return correo;
    }

    public String getDocumento() {
        return documento;
    }

    public String getTipoDoc() {
        return tipoDoc;
    }

    public String getFechaNac() {
        return fechaNac;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public String getRol() {
        return rol;
    }

    public String getNumYape() {
        return numYape;
    }

    public String getNumTransfer() {
        return numTransfer;
    }
}
