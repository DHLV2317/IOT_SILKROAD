package com.example.silkroad_iot.data;

import com.google.firebase.firestore.PropertyName;
import java.io.Serializable;

/**
 * Modelo Firestore para "tours".
 * Mantiene compatibilidad y agrega 'langs' (idiomas).
 */
public class TourFB implements Serializable {

    private String id;                 // ID del doc (no se guarda solo, asígnalo con doc.getId())
    private String nombre;             // p.ej. "Cusco Mágico"
    private String imagen;             // URL imagen
    private double precio;             // precio
    private int cantidad_personas;     // cupo / personas
    private String id_paradas;         // id/ref a paradas (si aplica)
    private String empresaId;          // empresa propietaria

    // 🔹 NUEVO: idiomas del tour (mismo formato que usas en el wizard, ej: "Español/Inglés")
    private String langs;

    // (Opcional) otros campos que ya consumes con reflexión en las pantallas admin
    private String duration;           // "3h", "1 día", etc.
    private String assignedGuideName;  // guía asignado
    private Double paymentProposal;    // propuesta de pago



    public TourFB() {}

    public TourFB(String id, String nombre, String imagen, double precio,
                  int cantidad_personas, String id_paradas, String empresaId) {
        this.id = id;
        this.nombre = nombre;
        this.imagen = imagen;
        this.precio = precio;
        this.cantidad_personas = cantidad_personas;
        this.id_paradas = id_paradas;
        this.empresaId = empresaId;
    }

    // --- Getters y Setters ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @PropertyName("nombre")
    public String getNombre() { return nombre; }
    @PropertyName("nombre")
    public void setNombre(String nombre) { this.nombre = nombre; }

    @PropertyName("imagen")
    public String getImagen() { return imagen; }
    @PropertyName("imagen")
    public void setImagen(String imagen) { this.imagen = imagen; }

    @PropertyName("precio")
    public double getPrecio() { return precio; }
    @PropertyName("precio")
    public void setPrecio(double precio) { this.precio = precio; }

    @PropertyName("cantidad_personas")
    public int getCantidad_personas() { return cantidad_personas; }
    @PropertyName("cantidad_personas")
    public void setCantidad_personas(int cantidad_personas) { this.cantidad_personas = cantidad_personas; }

    @PropertyName("id_paradas")
    public String getId_paradas() { return id_paradas; }
    @PropertyName("id_paradas")
    public void setId_paradas(String id_paradas) { this.id_paradas = id_paradas; }

    @PropertyName("empresaId")
    public String getEmpresaId() { return empresaId; }
    @PropertyName("empresaId")
    public void setEmpresaId(String empresaId) { this.empresaId = empresaId; }

    // 🔹 Idiomas
    @PropertyName("langs")
    public String getLangs() { return langs; }
    @PropertyName("langs")
    public void setLangs(String langs) { this.langs = langs; }

    // (opcionales) Para que tus pantallas admin que usan reflexión no revienten
    @PropertyName("duration")
    public String getDuration() { return duration; }
    @PropertyName("duration")
    public void setDuration(String duration) { this.duration = duration; }

    @PropertyName("assignedGuideName")
    public String getAssignedGuideName() { return assignedGuideName; }
    @PropertyName("assignedGuideName")
    public void setAssignedGuideName(String assignedGuideName) { this.assignedGuideName = assignedGuideName; }

    @PropertyName("paymentProposal")
    public Double getPaymentProposal() { return paymentProposal; }
    @PropertyName("paymentProposal")
    public void setPaymentProposal(Double paymentProposal) { this.paymentProposal = paymentProposal; }

    @Override
    public String toString() {
        return "TourFB{" +
                "nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", empresaId='" + empresaId + '\'' +
                ", langs='" + langs + '\'' +
                '}';
    }
}