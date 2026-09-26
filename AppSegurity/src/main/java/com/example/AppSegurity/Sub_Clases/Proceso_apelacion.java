package com.example.AppSegurity.Sub_Clases;

import com.example.AppSegurity.Enums.ApelacionSolicitada;
import com.example.AppSegurity.Enums.EstadoApelacion;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class Proceso_apelacion {
    //Parametros de la clase
    private String idApelacion;
    private ApelacionSolicitada apelacionSolicitada;
    private LocalDateTime fechaSolicitud;
    private String argumentoEstudiante;
    private EstadoApelacion estadoApelacion;
    private String resolucionComite;
    
    
    //Constructor sin parametros
    public Proceso_apelacion(){
        
    }
    
    //Constructor con parametros
    public Proceso_apelacion(String idApelacion, ApelacionSolicitada apelacionSolicitada, LocalDateTime fechaSolicitud, String argumentoEstudiante, EstadoApelacion estadoApelacion, String resolucionComite) {
        this.idApelacion = idApelacion;
        this.apelacionSolicitada = apelacionSolicitada;
        this.fechaSolicitud = fechaSolicitud;
        this.argumentoEstudiante = argumentoEstudiante;
        this.estadoApelacion = estadoApelacion;
        this.resolucionComite = resolucionComite;
    }
    
}
