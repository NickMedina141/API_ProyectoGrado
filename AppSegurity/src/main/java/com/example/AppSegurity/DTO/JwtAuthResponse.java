package com.example.AppSegurity.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter 
public class JwtAuthResponse {
    //Parametros de la clase
    private String token;
    private String tipo = "Bearer";

    //Constructor sin parametros
    public JwtAuthResponse(){
        
    }
    
    //Constructor con parametros

    public JwtAuthResponse(String token) {
        this.token = token;
    }
    

}


