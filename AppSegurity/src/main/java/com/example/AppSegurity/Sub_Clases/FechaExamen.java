package com.example.AppSegurity.Sub_Clases;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FechaExamen {
    //Parametros de la clase
    private LocalDateTime creacion;
    private LocalDateTime horaInicio;
    private LocalDateTime horaFin;
    
    //Constructor sin parametros
    public FechaExamen(){
        
    }
    
    //Constructor con parametros
    public FechaExamen(LocalDateTime creacion, LocalDateTime horaInicio, LocalDateTime horaFin){
        this.creacion = creacion;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }
    
}
