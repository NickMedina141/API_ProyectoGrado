package com.example.AppSegurity.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistroSolicitudRequest {

    private String email;
    private String cedula;
    private String rol; // "ESTUDIANTE" o "PROFESOR"
    private String nombre;

    public RegistroSolicitudRequest() {
    }

    public RegistroSolicitudRequest(String email, String cedula, String rol, String nombre) {
        this.email = email;
        this.cedula = cedula;
        this.rol = rol;
        this.nombre = nombre;
    }
}
