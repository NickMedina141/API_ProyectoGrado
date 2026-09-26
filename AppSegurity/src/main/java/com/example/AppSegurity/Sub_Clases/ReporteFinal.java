package com.example.AppSegurity.Sub_Clases;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ReporteFinal {
    //Parametros de la clase
    private String veredictoProfesor;
    private String urlPdfReporte; // MinIO
    private Double notaFinal; // Nota sincronizada desde Moodle
    
    //Constructor sin parametros
    public ReporteFinal(){
        
    }
    
    //Constructor con parametros
    public ReporteFinal(String veredictoProfesor, String urlPdfReporte) {
        this.veredictoProfesor = veredictoProfesor;
        this.urlPdfReporte = urlPdfReporte;
    }
    

    
    
    
}
