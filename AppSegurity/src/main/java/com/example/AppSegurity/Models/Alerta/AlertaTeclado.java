package com.example.AppSegurity.Models.Alerta;

import com.example.AppSegurity.Models.AlertaEvidencia;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;

@Getter
@Setter
@TypeAlias("TECLADO")
public class AlertaTeclado extends AlertaEvidencia{
    //Parametros de la clase
    private String combinacionTeclas;
    private String patronSospechoso;
    private String rutaCapturaPantalla;
    
    //Constructor sin parametros
    public AlertaTeclado(){
        
    }
    
    //Constructor con parametros

    public AlertaTeclado(String combinacionTeclas, String patronSospechoso, String rutaCapturaPantalla) {
        this.combinacionTeclas = combinacionTeclas;
        this.patronSospechoso = patronSospechoso;
        this.rutaCapturaPantalla = rutaCapturaPantalla;
    }
    
}
