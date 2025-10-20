package com.example.silkroad_iot.data;

import com.google.firebase.firestore.PropertyName;
import java.io.Serializable;
import java.util.List;

public class GuideFb implements Serializable {

    private String id;
    private String nombre;
    private String langs;
    private String estado;
    private String email;
    private String telefono;
    private String tourActual;
    private List<String> historial;
    private String fotoUrl;

    // 🔹 nuevos campos para estado administrativo
    private boolean guideApproved;
    private String guideApprovalStatus; // "PENDING", "APPROVED", "REJECTED"

    public GuideFb() {}

    @PropertyName("id")
    public String getId() { return id; }
    @PropertyName("id")
    public void setId(String id) { this.id = id; }

    @PropertyName("nombre")
    public String getNombre() { return nombre; }
    @PropertyName("nombre")
    public void setNombre(String nombre) { this.nombre = nombre; }

    @PropertyName("langs")
    public String getLangs() { return langs; }
    @PropertyName("langs")
    public void setLangs(String langs) { this.langs = langs; }

    @PropertyName("estado")
    public String getEstado() { return estado; }
    @PropertyName("estado")
    public void setEstado(String estado) { this.estado = estado; }

    @PropertyName("email")
    public String getEmail() { return email; }
    @PropertyName("email")
    public void setEmail(String email) { this.email = email; }

    @PropertyName("telefono")
    public String getTelefono() { return telefono; }
    @PropertyName("telefono")
    public void setTelefono(String telefono) { this.telefono = telefono; }

    @PropertyName("tourActual")
    public String getTourActual() { return tourActual; }
    @PropertyName("tourActual")
    public void setTourActual(String tourActual) { this.tourActual = tourActual; }

    @PropertyName("historial")
    public List<String> getHistorial() { return historial; }
    @PropertyName("historial")
    public void setHistorial(List<String> historial) { this.historial = historial; }

    @PropertyName("fotoUrl")
    public String getFotoUrl() { return fotoUrl; }
    @PropertyName("fotoUrl")
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }

    @PropertyName("guideApproved")
    public boolean isGuideApproved() { return guideApproved; }
    @PropertyName("guideApproved")
    public void setGuideApproved(boolean guideApproved) { this.guideApproved = guideApproved; }

    @PropertyName("guideApprovalStatus")
    public String getGuideApprovalStatus() { return guideApprovalStatus; }
    @PropertyName("guideApprovalStatus")
    public void setGuideApprovalStatus(String guideApprovalStatus) { this.guideApprovalStatus = guideApprovalStatus; }
}