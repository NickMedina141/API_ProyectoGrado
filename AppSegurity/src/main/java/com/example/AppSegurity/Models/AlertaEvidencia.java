package com.example.AppSegurity.Models;

import com.example.AppSegurity.Enums.ClaseAlerta;
import com.example.AppSegurity.Enums.NivelRiesgo;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Getter
@Setter
@Document(collection = "alerta_evidencia")
public class AlertaEvidencia {
    //Parametros de la clase
    @Id
    private String idAlerta;
    private String sesionId;
    private ClaseAlerta claseAlerta;
    private LocalDateTime horaCaptura;
    private NivelRiesgo nivelRiesgo;
    
    @org.springframework.data.annotation.Transient
    private String nombreEstudiante;
    
    //Constructor sin parametros
    public AlertaEvidencia(){
        
    }
    
    //Constructor con parametros

    public AlertaEvidencia(String idAlerta, String sesionId, ClaseAlerta claseAlerta, LocalDateTime horaCaptura, NivelRiesgo nivelRiesgo) {
        this.idAlerta = idAlerta;
        this.sesionId = sesionId;
        this.claseAlerta = claseAlerta;
        this.horaCaptura = horaCaptura;
        this.nivelRiesgo = nivelRiesgo;
    }
    
}
