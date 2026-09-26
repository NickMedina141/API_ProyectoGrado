package com.example.AppSegurity.Sub_Clases;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Materia {
    //Parametros de la clase
    private String codigoMateria;
    private String nombreMateria;
    
    //Constructor vacio
    public Materia(){
        
    }
    
    //Constructor con parametros
    public Materia(String codigoMateria, String nombreMateria){
       this.codigoMateria = codigoMateria;
       this.nombreMateria = nombreMateria;
    }
    
}
