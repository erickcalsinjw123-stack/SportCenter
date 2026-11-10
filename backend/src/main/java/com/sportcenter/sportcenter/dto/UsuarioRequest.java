package com.sportcenter.sportcenter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UsuarioRequest {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(max = 50, message = "El usuario no puede superar los 50 caracteres")
    private String nombreUsuario;

    private String contrasena;

    @NotBlank(message = "El rol es obligatorio")
    @Pattern(regexp = "ADMIN|RECEPCIONISTA",
            message = "El rol debe ser ADMIN o RECEPCIONISTA")
    private String rol;

    @NotBlank(message = "El estado es obligatorio")
    @Pattern(regexp = "ACTIVO|INACTIVO",
            message = "El estado debe ser ACTIVO o INACTIVO")
    private String estado;

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}