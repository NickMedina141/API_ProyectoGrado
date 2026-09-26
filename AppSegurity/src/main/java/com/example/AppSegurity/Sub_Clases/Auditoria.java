package com.example.AppSegurity.Sub_Clases;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Auditoria {
    //Parametros de la clase
    private LocalDateTime fechaRegistro;
    private LocalDateTime ultimoAcceso;
    private LocalDateTime fechaActualizacion; // cuando se actualice cualquier dato ya sea del docente o el estudiante
    
    
    //Constructor vacio
    public Auditoria(){
        
    }
    
    //Constructor con parametros
    public Auditoria(LocalDateTime fechaRegistro, LocalDateTime ultimoAcceso, LocalDateTime fechaActualizacion){
        this.fechaRegistro = fechaRegistro;
        this.ultimoAcceso = ultimoAcceso;
        this.fechaActualizacion = fechaActualizacion;
    }
    
    //REVISAR AQUI QUE RAYOS HICE? SE ME OLVIDO ENSERIO
    //Constructor con un solo parametro
    public Auditoria(LocalDateTime fechaRegistro){
        this.fechaRegistro = fechaRegistro;
    }
    
}
