package com.example.silkroad_iot.data;

import com.google.firebase.firestore.PropertyName;
import java.io.Serializable;

/**
 * Representa una empresa en Firestore.
 * Compatible con las vistas del cliente y del administrador.
 */
public class EmpresaFb implements Serializable {

    private String id;           // ID del documento (asignado con doc.getId())
    private String nombre;       // Nombre de la empresa
    private String imagen;       // URL de imagen o logo

    // Campos adicionales para el rol de Administrador
    private String email;
    private String telefono;
    private String direccion;
    private double lat;
    private double lng;

    // 🔹 Constructor vacío requerido por Firestore
    public EmpresaFb() {}

    // 🔹 Constructor opcional (útil si se quiere crear manualmente)
    public EmpresaFb(String id, String nombre, String imagen,
                     String email, String telefono, String direccion,
                     double lat, double lng) {
        this.id = id;
        this.nombre = nombre;
        this.imagen = imagen;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.lat = lat;
        this.lng = lng;
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

    @PropertyName("lat")
    public double getLat() { return lat; }

    @PropertyName("lat")
    public void setLat(double lat) { this.lat = lat; }

    @PropertyName("lng")
    public double getLng() { return lng; }

    @PropertyName("lng")
    public void setLng(double lng) { this.lng = lng; }

    @Override
    public String toString() {
        return "EmpresaFb{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", imagen='" + imagen + '\'' +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                ", direccion='" + direccion + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}