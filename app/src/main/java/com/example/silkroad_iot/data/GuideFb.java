package com.example.silkroad_iot.data;

import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.util.List;

public class GuideFb implements Serializable {

    private String id;

    private String nombre;
    private String apellidos;
    private String langs;
    private String estado;
    private String email;
    private String telefono;
    private String direccion;

    private String fotoUrl;

    private String tourActual;
    private String tourIdAsignado;

    private List<String> historial;

    // ---------- 🔵 GEOLOCALIZACIÓN REAL ----------
    private Double latActual;
    private Double lngActual;
    private Long lastUpdate;

    private boolean guideApproved;
    private String guideApprovalStatus;

    public GuideFb() {}

    @PropertyName("id")
    public String getId() { return id; }
    @PropertyName("id")
    public void setId(String id) { this.id = id; }

    @PropertyName("nombre")
    public String getNombre() { return nombre; }
    @PropertyName("nombre")
    public void setNombre(String nombre) { this.nombre = nombre; }

    @PropertyName("apellidos")
    public String getApellidos() { return apellidos; }
    @PropertyName("apellidos")
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

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

    @PropertyName("direccion")
    public String getDireccion() { return direccion; }
    @PropertyName("direccion")
    public void setDireccion(String direccion) { this.direccion = direccion; }

    @PropertyName("fotoUrl")
    public String getFotoUrl() { return fotoUrl; }
    @PropertyName("fotoUrl")
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }

    @PropertyName("tourActual")
    public String getTourActual() { return tourActual; }
    @PropertyName("tourActual")
    public void setTourActual(String tourActual) { this.tourActual = tourActual; }

    @PropertyName("tourIdAsignado")
    public String getTourIdAsignado() { return tourIdAsignado; }
    @PropertyName("tourIdAsignado")
    public void setTourIdAsignado(String tourIdAsignado) { this.tourIdAsignado = tourIdAsignado; }

    @PropertyName("historial")
    public List<String> getHistorial() { return historial; }
    @PropertyName("historial")
    public void setHistorial(List<String> historial) { this.historial = historial; }

    // 🔵 GEO
    @PropertyName("latActual")
    public Double getLatActual() { return latActual; }
    @PropertyName("latActual")
    public void setLatActual(Double latActual) { this.latActual = latActual; }

    @PropertyName("lngActual")
    public Double getLngActual() { return lngActual; }
    @PropertyName("lngActual")
    public void setLngActual(Double lngActual) { this.lngActual = lngActual; }

    @PropertyName("lastUpdate")
    public Long getLastUpdate() { return lastUpdate; }
    @PropertyName("lastUpdate")
    public void setLastUpdate(Long lastUpdate) { this.lastUpdate = lastUpdate; }

    @PropertyName("guideApproved")
    public boolean isGuideApproved() { return guideApproved; }
    @PropertyName("guideApproved")
    public void setGuideApproved(boolean guideApproved) { this.guideApproved = guideApproved; }

    @PropertyName("guideApprovalStatus")
    public String getGuideApprovalStatus() { return guideApprovalStatus; }
    @PropertyName("guideApprovalStatus")
    public void setGuideApprovalStatus(String guideApprovalStatus) { this.guideApprovalStatus = guideApprovalStatus; }
}