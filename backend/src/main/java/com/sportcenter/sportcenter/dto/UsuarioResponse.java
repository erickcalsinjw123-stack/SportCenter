package com.sportcenter.sportcenter.dto;

import java.time.LocalDateTime;

public class UsuarioResponse {

    private Integer idUsuario;
    private String nombreUsuario;
    private String rol;
    private String estado;
    private LocalDateTime fechaCreacion;

    public UsuarioResponse(Integer idUsuario, String nombreUsuario,
                           String rol, String estado,
                           LocalDateTime fechaCreacion) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.rol = rol;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getRol() {
        return rol;
    }

    public String getEstado() {
        return estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
}