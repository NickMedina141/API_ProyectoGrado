package com.example.AppSegurity.Sub_Clases;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class SeguridadJwt {
    //Parametros de la clase
    private List<String> tokensActivosRefrescados;
    private LocalDateTime cambioPassword;
    private Integer intentosFallidos;
    
    //Constructor sin parametros
    public SeguridadJwt(){
        
    }
    
    //Constructor con parametros
    public SeguridadJwt(List<String> tokensActivosRefrescados, LocalDateTime cambioPassword, Integer intentosFallidos){
        this.tokensActivosRefrescados = tokensActivosRefrescados;
        this.cambioPassword = cambioPassword;
        this.intentosFallidos = intentosFallidos;
    }
    
    
}
