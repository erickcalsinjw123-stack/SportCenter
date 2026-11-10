package com.sportcenter.sportcenter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UsuarioEstadoRequest {

    @NotBlank(message = "El estado es obligatorio")
    @Pattern(
        regexp = "ACTIVO|INACTIVO",
        message = "El estado debe ser ACTIVO o INACTIVO"
    )
    private String estado;

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}