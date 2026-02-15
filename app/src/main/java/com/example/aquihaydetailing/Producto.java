package com.example.aquihaydetailing;


public class Producto {


    private String nombre;
    private String descripcion;
    private String imagenUrl;
    private String serviciosUsados;
    private String enlaceFullCarX; // opcional
    private String categoria;      // 🔥 NUEVO


    // 🔹 Constructor vacío (obligatorio para Firebase)
    public Producto() {}


    // 🔹 Constructor completo con categoría
    public Producto(String nombre, String descripcion, String imagenUrl,
                    String serviciosUsados, String enlaceFullCarX, String categoria) {


        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagenUrl = imagenUrl;
        this.serviciosUsados = serviciosUsados;
        this.enlaceFullCarX = enlaceFullCarX;
        this.categoria = categoria;
    }


    // 🔹 Getters y setters
    public String getNombre() {
        return nombre;
    }


    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    public String getDescripcion() {
        return descripcion;
    }


    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }


    public String getImagenUrl() {
        return imagenUrl;
    }


    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }


    public String getServiciosUsados() {
        return serviciosUsados;
    }


    public void setServiciosUsados(String serviciosUsados) {
        this.serviciosUsados = serviciosUsados;
    }


    public String getEnlaceFullCarX() {
        return enlaceFullCarX;
    }


    public void setEnlaceFullCarX(String enlaceFullCarX) {
        this.enlaceFullCarX = enlaceFullCarX;
    }


    public String getCategoria() {
        return categoria;
    }


    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}