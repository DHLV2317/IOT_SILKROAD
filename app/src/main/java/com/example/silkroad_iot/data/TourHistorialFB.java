package com.example.silkroad_iot.data;

import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.util.Date;

/**
 * Modelo Firestore para historial de tours realizados.
 * Guarda qué tour, quién lo reservó, fechas, estado, datos del QR
 * y rating/comentario del cliente (si califica).
 *
 * Formato sugerido de qrData:
 *   RESERVA|<id_reserva>|<id_tour>|<id_usuario>|PAX:<pax>
 */
public class TourHistorialFB implements Serializable {

    private String id;            // ID del documento en Firestore
    private String id_tour;       // Referencia al tour original
    private String id_usuario;    // Usuario que realizó la reserva (email o uid)

    private Date fechaReserva;    // Fecha en la que se hizo la reserva
    private Date fecha_realizado; // Fecha de realización del tour (si aplica)

    private String estado;        // pendiente, aceptado, rechazado, check-in, check-out, finalizada, cancelado
    private int pax;              // cantidad de personas reservadas

    /**
     * Cadena que se codifica en el código QR.
     * Formato:
     * RESERVA|<id_reserva>|<id_tour>|<id_usuario>|PAX:<pax>
     */
    private String qrData;

    // ⭐ Nuevo: rating del cliente (0–5) y comentario
    private Float rating;
    private String comentario;

    public TourHistorialFB() {}

    public TourHistorialFB(String id,
                           String id_tour,
                           String id_usuario,
                           Date fechaReserva,
                           int pax,
                           String estado,
                           String qrData) {
        this.id = id;
        this.id_tour = id_tour;
        this.id_usuario = id_usuario;
        this.fechaReserva = fechaReserva;
        this.pax = pax;
        this.estado = estado;
        this.qrData = qrData;
    }

    // --- ID del doc ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    // --- Tour ---
    @PropertyName("id_tour")
    public String getIdTour() { return id_tour; }
    @PropertyName("id_tour")
    public void setIdTour(String id_tour) { this.id_tour = id_tour; }

    // --- Usuario ---
    @PropertyName("id_usuario")
    public String getIdUsuario() { return id_usuario; }
    @PropertyName("id_usuario")
    public void setIdUsuario(String id_usuario) { this.id_usuario = id_usuario; }

    // --- Fechas ---
    public Date getFechaReserva() { return fechaReserva; }
    public void setFechaReserva(Date fechaReserva) { this.fechaReserva = fechaReserva; }

    @PropertyName("fecha_realizado")
    public Date getFechaRealizado() { return fecha_realizado; }
    @PropertyName("fecha_realizado")
    public void setFechaRealizado(Date fecha_realizado) { this.fecha_realizado = fecha_realizado; }

    // --- Estado ---
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    // --- Pax ---
    public int getPax() { return pax; }
    public void setPax(int pax) { this.pax = pax; }

    // --- QR ---
    public String getQrData() { return qrData; }
    public void setQrData(String qrData) { this.qrData = qrData; }

    // --- Rating / Comentario ---
    public Float getRating() { return rating; }
    public void setRating(Float rating) { this.rating = rating; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    @Override
    public String toString() {
        return "TourHistorialFB{" +
                "id='" + id + '\'' +
                ", id_tour='" + id_tour + '\'' +
                ", id_usuario='" + id_usuario + '\'' +
                ", fechaReserva=" + fechaReserva +
                ", estado='" + estado + '\'' +
                ", pax=" + pax +
                ", rating=" + rating +
                '}';
    }
}