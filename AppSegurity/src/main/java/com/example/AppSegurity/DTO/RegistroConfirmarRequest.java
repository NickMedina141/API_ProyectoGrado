package com.example.AppSegurity.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistroConfirmarRequest {

    private String email;
    private String codigoOtp;
    private String nombre;
    private String apellidos;
    private String cedula;
    private String password;
    private String codigoProfesor; // Opcional, solo para profesores

    public RegistroConfirmarRequest() {
    }

    public RegistroConfirmarRequest(String email, String codigoOtp, String nombre, String apellidos, String cedula, String password, String codigoProfesor) {
        this.email = email;
        this.codigoOtp = codigoOtp;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.cedula = cedula;
        this.password = password;
        this.codigoProfesor = codigoProfesor;
    }
}
