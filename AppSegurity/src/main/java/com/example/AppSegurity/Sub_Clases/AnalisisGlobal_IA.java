package com.example.AppSegurity.Sub_Clases;

import com.example.AppSegurity.Enums.NivelRiesgo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter 
public class AnalisisGlobal_IA {

        //Parametros de la clase
    private String resumenDetallado;
    private NivelRiesgo riesgoGlobalCalculado;
    private Integer puntajeRiesgoCalculado;
    private String modeloIaUtilizado;
    
    //Constructor sin parametros
    public AnalisisGlobal_IA(){
        
    }
    
    //Constructor con parametros
    public AnalisisGlobal_IA(String resumenDetallado, NivelRiesgo riesgoGlobalCalculado, Integer puntajeRiesgoCalculado, String modeloIaUtilizado) {
        this.resumenDetallado = resumenDetallado;
        this.riesgoGlobalCalculado = riesgoGlobalCalculado;
        this.puntajeRiesgoCalculado = puntajeRiesgoCalculado;
        this.modeloIaUtilizado = modeloIaUtilizado;
    }

    
}
