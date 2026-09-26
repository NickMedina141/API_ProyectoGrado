package com.example.AppSegurity.DTO;

import com.example.AppSegurity.Sub_Clases.Conexion;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class IniciarSesionRequest {

    //Parametros de la clase
    private String estudianteId;
    private String pinExamen;
    private Conexion conexion;

    //Constructor sin parametros
    public IniciarSesionRequest() {

    }

    //Constructor con parametros
    public IniciarSesionRequest(String estudianteId, String pinExamen, Conexion conexion) {
        this.estudianteId = estudianteId;
        this.pinExamen = pinExamen;
        this.conexion = conexion;
    }

}
