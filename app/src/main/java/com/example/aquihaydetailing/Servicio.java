package com.example.aquihaydetailing;

import java.util.List;

public class Servicio {
    private String nombre;
    private List<String> tareasIncluidas;
    private String tiempoEstimado;

    public Servicio(String nombre, List<String> tareasIncluidas, String tiempoEstimado) {
        this.nombre = nombre;
        this.tareasIncluidas = tareasIncluidas;
        this.tiempoEstimado = tiempoEstimado;
    }

    public String getNombre() {
        return nombre;
    }

    public List<String> getTareasIncluidas() {
        return tareasIncluidas;
    }

    public String getTiempoEstimado() {
        return tiempoEstimado;
    }
}