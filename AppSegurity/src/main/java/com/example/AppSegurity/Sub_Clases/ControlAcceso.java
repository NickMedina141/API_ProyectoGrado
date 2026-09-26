package com.example.AppSegurity.Sub_Clases;

import com.example.AppSegurity.Enums.Estado_pin;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ControlAcceso {
    //Parametros de la clase
    private String pinSesion; // Generado de manera automatica para los estudiantes
    private Estado_pin estadoPin; // Activo o Finalizado
    
    //Constructor sin parametros
    public ControlAcceso(){
        
    }
    
    //Constructor con parametros
    public ControlAcceso(String pinSesion, Estado_pin estadoPin){
        this.pinSesion = pinSesion;
        this.estadoPin = estadoPin;
    }
    
}
