package com.example.silkroad_iot.ui.superadmin.entity;

import java.io.Serializable;
import java.util.Date;

public class Log implements Serializable {
    private String nombre;
    private String tipo;
    private Date fecha;
    private String hora;
    private String usuario;
    private String tipoUsuario;
    private String usuarioAfectado;
    private String tipoUsuarioAfectado;
    private String descripcion;

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getUsuarioAfectado() {
        return usuarioAfectado;
    }

    public void setUsuarioAfectado(String usuarioAfectado) {
        this.usuarioAfectado = usuarioAfectado;
    }

    public String getTipoUsuarioAfectado() {
        return tipoUsuarioAfectado;
    }

    public void setTipoUsuarioAfectado(String tipoUsuarioAfectado) {
        this.tipoUsuarioAfectado = tipoUsuarioAfectado;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
