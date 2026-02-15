package com.example.aquihaydetailing;

public class Cita {
    private String servicio;
    private String fecha;
    private String hora;
    private String precio;
    private String tamaño;
    private String estado;

    public Cita() {}

    public Cita(String servicio, String fecha, String hora, String precio, String tamaño, String estado) {
        this.servicio = servicio;
        this.fecha = fecha;
        this.hora = hora;
        this.precio = precio;
        this.tamaño = tamaño;
        this.estado = estado;
    }

    public String getServicio() { return servicio; }
    public String getFecha() { return fecha; }
    public String getHora() { return hora; }
    public String getPrecio() { return precio; }
    public String getTamaño() { return tamaño; }
    public String getEstado() { return estado; }
}


