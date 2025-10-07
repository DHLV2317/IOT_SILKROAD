package com.example.silkroad_iot.data;

import com.google.firebase.firestore.PropertyName;
import java.io.Serializable;

public class EmpresaFb implements Serializable {
    private String id;      // 🔹 ID del documento en Firestore
    private String nombre;
    private String imagen;

    public EmpresaFb() {}

    // --- Getters y Setters ---

    public String getId() {
        return id;
    }

    // Este id no viene de Firestore automáticamente, lo asignarás tú con doc.getId()
    public void setId(String id) {
        this.id = id;
    }

    @PropertyName("nombre")
    public String getNombre() {
        return nombre;
    }

    @PropertyName("nombre")
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @PropertyName("imagen")
    public String getImagen() {
        return imagen;
    }

    @PropertyName("imagen")
    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
