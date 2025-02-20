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

import android.os.Parcel;
import android.os.Parcelable;

public class CanchaEstadistica implements Parcelable {
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

    // Constructor para Parcelable
    protected CanchaEstadistica(Parcel in) {
        idCancha = in.readString();
        nombre = in.readString();
        ganancias = in.readDouble();
        totalReservas = in.readInt();
        totalReservasPagadas = in.readInt();
    }

    public static final Creator<CanchaEstadistica> CREATOR = new Creator<CanchaEstadistica>() {
        @Override
        public CanchaEstadistica createFromParcel(Parcel in) {
            return new CanchaEstadistica(in);
        }

        @Override
        public CanchaEstadistica[] newArray(int size) {
            return new CanchaEstadistica[size];
        }
    };

    // Getters
    public String getIdCancha() { return idCancha; }
    public String getNombre() { return nombre; }
    public double getGanancias() { return ganancias; }
    public int getTotalReservas() { return totalReservas; }
    public int getTotalReservasPagadas() { return totalReservasPagadas; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(idCancha);
        parcel.writeString(nombre);
        parcel.writeDouble(ganancias);
        parcel.writeInt(totalReservas);
        parcel.writeInt(totalReservasPagadas);
    }

    @Override
    public String toString() {
        return "CanchaEstadistica{" +
                "idCancha='" + idCancha + '\'' +
                ", nombre='" + nombre + '\'' +
                ", ganancias=" + ganancias +
                ", totalReservas=" + totalReservas +
                ", totalReservasPagadas=" + totalReservasPagadas +
                '}';
    }
}
