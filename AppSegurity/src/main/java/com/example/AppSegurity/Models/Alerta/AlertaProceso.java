package com.example.AppSegurity.Models.Alerta;

import com.example.AppSegurity.Enums.AccionTomada;
import com.example.AppSegurity.Enums.CategoriaProceso;
import com.example.AppSegurity.Models.AlertaEvidencia;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;

@Getter
@Setter
@TypeAlias("PROCESO")
public class AlertaProceso extends AlertaEvidencia {
    //Parametros de la clase
    private Integer pidProceso;
    private String nombreProceso;
    private CategoriaProceso categoriaProceso;
    private AccionTomada accionTomada;
    private String urlCapturaPantalla;
    
    //Constructor sin parametros
    public AlertaProceso(){
        
    }
    
    //Constructor con parametros

    public AlertaProceso(Integer pidProceso, String nombreProceso, CategoriaProceso categoriaProceso, AccionTomada accionTomada, String urlCapturaPantalla) {
        this.pidProceso = pidProceso;
        this.nombreProceso = nombreProceso;
        this.categoriaProceso = categoriaProceso;
        this.accionTomada = accionTomada;
        this.urlCapturaPantalla = urlCapturaPantalla;
    }
}
