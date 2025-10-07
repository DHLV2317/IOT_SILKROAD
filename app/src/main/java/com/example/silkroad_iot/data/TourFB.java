package com.example.silkroad_iot.data;

import java.io.Serializable;

/**
 * Representa un tour en el sistema.
 * Cada tour pertenece a una empresa (empresaId).
 * Compatible con Firestore y serializable para pasar entre Activities.
 */
public class TourFB implements Serializable {

    private String id;                 // ID del documento en Firestore (opcional pero útil)
    private String nombre;             // Nombre del tour (ej: "Cusco Mágico")
    private String imagen;             // URL de la imagen
    private double precio;             // Precio por persona o por tour
    private int cantidad_personas;     // Número de personas por grupo
    private String id_paradas;         // ID o referencia a las paradas del tour
    private String empresaId;          // ID de la empresa a la que pertenece este tour

    // 🔹 Constructor vacío requerido por Firestore
    public TourFB() {}

    // 🔹 Constructor opcional para uso manual (por si creas tours localmente)
    public TourFB(String id,String nombre, String imagen, double precio, int cantidad_personas, String id_paradas, String empresaId) {
        this.id = id;
        this.nombre = nombre;
        this.imagen = imagen;
        this.precio = precio;
        this.cantidad_personas = cantidad_personas;
        this.id_paradas = id_paradas;
        this.empresaId = empresaId;
    }

    // --- Getters y Setters ---


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getCantidad_personas() {
        return cantidad_personas;
    }

    public void setCantidad_personas(int cantidad_personas) {
        this.cantidad_personas = cantidad_personas;
    }

    public String getId_paradas() {
        return id_paradas;
    }

    public void setId_paradas(String id_paradas) {
        this.id_paradas = id_paradas;
    }

    public String getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }

    @Override
    public String toString() {
        return "TourFB{" +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", empresaId='" + empresaId + '\'' +
                '}';
    }
}
