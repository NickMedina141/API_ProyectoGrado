package com.example.AppSegurity.Models;

import com.example.AppSegurity.Enums.EstadoExamen;
import com.example.AppSegurity.Sub_Clases.ConfiguracionExamen;
import com.example.AppSegurity.Sub_Clases.ControlAcceso;
import com.example.AppSegurity.Sub_Clases.FechaExamen;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter

@Document(collection = "examen")
public class Examen {
    //Parametros de la clase
    //Id principales del examen
    @Id
    private String codigoExamen;
    private String profesorId;
    private String materiaCodigo;
    private ControlAcceso controlAcceso;
    private FechaExamen fechaExamen;
    private ConfiguracionExamen configuracionExamen;
    
    //Constructor sin parametros
    public Examen(){
        
    }
    
    //Constructor con parametros

    public Examen(String codigoExamen, String profesorId, String materiaCodigo, ControlAcceso controlAcceso, FechaExamen fechaExamen, ConfiguracionExamen configuracionExamen) {
        this.codigoExamen = codigoExamen;
        this.profesorId = profesorId;
        this.materiaCodigo = materiaCodigo;
        this.controlAcceso = controlAcceso;
        this.fechaExamen = fechaExamen;
        this.configuracionExamen = configuracionExamen;
    }
    
    
}
