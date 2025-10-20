package com.example.silkroad_iot.data;

import com.google.firebase.firestore.PropertyName;
import java.io.Serializable;
import java.util.Date;

/**
 * Modelo Firestore para historial de tours realizados.
 * Solo guarda lo esencial: qué tour, quién lo hizo y cuándo.
 */
public class TourHistorialFB implements Serializable {

    private String id;               // ID del documento en Firestore
    private String id_tour;         // Referencia al tour original
    private String id_usuario;      // Usuario que realizó el tour
    private Date fecha_realizado; // Fecha de realización (formato texto o timestamp ISO)

    public TourHistorialFB() {}

    public TourHistorialFB(String id, String id_tour, String id_usuario, Date fecha_realizado) {
        this.id = id;
        this.id_tour = id_tour;
        this.id_usuario = id_usuario;
        this.fecha_realizado = fecha_realizado;
    }

    // Getters y Setters

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @PropertyName("id_tour")
    public String getIdTour() { return id_tour; }
    @PropertyName("id_tour")
    public void setIdTour(String id_tour) { this.id_tour = id_tour; }

    @PropertyName("id_usuario")
    public String getIdUsuario() { return id_usuario; }
    @PropertyName("id_usuario")
    public void setIdUsuario(String id_usuario) { this.id_usuario = id_usuario; }

    @PropertyName("fecha_realizado")
    public Date getFechaRealizado() { return fecha_realizado; }
    @PropertyName("fecha_realizado")
    public void setFechaRealizado(Date fecha_realizado) { this.fecha_realizado = fecha_realizado; }

    @Override
    public String toString() {
        return "TourHistorialFB{" +
                "id_tour='" + id_tour + '\'' +
                ", id_usuario='" + id_usuario + '\'' +
                ", fecha_realizado='" + fecha_realizado + '\'' +
                '}';
    }
}
