package com.example.silkroad_iot.ui.superadmin.entity;

import java.io.Serializable;
public class Administrador implements Serializable{
    private String nombre;
    //private Empresa empresa;
    private String nombreEmpresa;
    private String ubicacion;
    private String correo;
    private String telefono;
    private Byte foto1;
    private Byte foto2;

    private String contrasena;

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
    private boolean activo;

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Byte getFoto1() {
        return foto1;
    }

    public void setFoto1(Byte foto1) {
        this.foto1 = foto1;
    }

    public Byte getFoto2() {
        return foto2;
    }

    public void setFoto2(Byte foto2) {
        this.foto2 = foto2;
    }
}
