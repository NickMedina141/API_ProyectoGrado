package com.example.AppSegurity.Models.Alerta;

import com.example.AppSegurity.Models.AlertaEvidencia;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;

@Getter
@Setter

@TypeAlias("AUDIO")
public class AlertaAudio extends AlertaEvidencia{
    //Parametros de la clase
    private String transcripcion;
    private Integer vocesDetectadas;
    private double confianzaVoz;
    private String urlAudio; // ruta de MinIO
    
    //Constructor sin parametros
    public AlertaAudio(){
        
    }
    
    //Constructor con Parametros
    public AlertaAudio(String transcripcion, Integer vocesDetectadas, double confianzaVoz, String urlAudio) {
        this.transcripcion = transcripcion;
        this.vocesDetectadas = vocesDetectadas;
        this.confianzaVoz = confianzaVoz;
        this.urlAudio = urlAudio;
    }
    
    
}
