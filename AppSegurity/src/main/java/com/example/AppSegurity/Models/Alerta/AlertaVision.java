package com.example.AppSegurity.Models.Alerta;

import com.example.AppSegurity.Enums.ClaseAlerta;
import com.example.AppSegurity.Enums.NivelRiesgo;
import com.example.AppSegurity.Enums.TipoEvidenciaVision;
import com.example.AppSegurity.Models.AlertaEvidencia;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;

@Getter
@Setter

@TypeAlias("VISION")
public class AlertaVision extends AlertaEvidencia{
    //Parametros de la clase
    private TipoEvidenciaVision tipoEvidencia;
    private Integer cantidadRostros;
    private String objetoDetectado;
    private Double confianzaIa;
    private String urlFotoWebcam;
    private String urlCapturaPantalla;
    
    //Constructor sin parametros
    public AlertaVision(){
        
    }
    
    //Constructor con parametros
    public AlertaVision(TipoEvidenciaVision tipoEvidencia, Integer cantidadRostros, String objetoDetectado, Double confianzaIa, String urlFotoWebcam, String urlCapturaPantalla) {
        this.tipoEvidencia = tipoEvidencia;
        this.cantidadRostros = cantidadRostros;
        this.objetoDetectado = objetoDetectado;
        this.confianzaIa = confianzaIa;
        this.urlFotoWebcam = urlFotoWebcam;
        this.urlCapturaPantalla = urlCapturaPantalla;
    }
    
    
    
}
