package com.example.silkroad_iot.data;

import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;

/**
 * Representa una parada dentro de un tour.
 * Cada parada puede tener nombre, descripción, coordenadas, orden, etc.
 * Incluye campos opcionales "address" y "minutes" para facilitar el render en chips.
 */
public class ParadaFB implements Serializable {

    private String id;              // ID del documento (Firestore)
    private String nombre;          // Ej: "Plaza de Armas"
    private String descripcion;     // breve texto
    private double lat;             // coordenadas
    private double lng;
    private int orden;              // posición en la ruta
    private String tourId;          // referencia al tour padre

    // Opcionales para UI
    private String address;         // dirección legible (si la usas)
    private Integer minutes;        // minutos de estancia (si la usas)

    public ParadaFB() {}

    public ParadaFB(String id, String nombre, String descripcion,
                    double lat, double lng, int orden, String tourId) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.lat = lat;
        this.lng = lng;
        this.orden = orden;
        this.tourId = tourId;
    }

    @PropertyName("id") public String getId() { return id; }
    @PropertyName("id") public void setId(String id) { this.id = id; }

    @PropertyName("nombre") public String getNombre() { return nombre; }
    @PropertyName("nombre") public void setNombre(String nombre) { this.nombre = nombre; }

    @PropertyName("descripcion") public String getDescripcion() { return descripcion; }
    @PropertyName("descripcion") public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @PropertyName("lat") public double getLat() { return lat; }
    @PropertyName("lat") public void setLat(double lat) { this.lat = lat; }

    @PropertyName("lng") public double getLng() { return lng; }
    @PropertyName("lng") public void setLng(double lng) { this.lng = lng; }

    @PropertyName("orden") public int getOrden() { return orden; }
    @PropertyName("orden") public void setOrden(int orden) { this.orden = orden; }

    @PropertyName("tourId") public String getTourId() { return tourId; }
    @PropertyName("tourId") public void setTourId(String tourId) { this.tourId = tourId; }

    @PropertyName("address") public String getAddress() { return address; }
    @PropertyName("address") public void setAddress(String address) { this.address = address; }

    @PropertyName("minutes") public Integer getMinutes() { return minutes; }
    @PropertyName("minutes") public void setMinutes(Integer minutes) { this.minutes = minutes; }

    @Override
    public String toString() {
        return (nombre != null ? nombre : "Parada") + " (" + lat + ", " + lng + ")";
    }
}