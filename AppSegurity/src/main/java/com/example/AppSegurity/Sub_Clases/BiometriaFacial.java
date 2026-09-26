package com.example.AppSegurity.Sub_Clases;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class BiometriaFacial {

    //Parametros de la clase
    private String vectorRostro; // String para soportar el cifrado AES-256
    private String urlFotoRegistro;
    private String modeloIA; //FaceNET, ArcFace, etc.
    private Integer dimension; //128, 512, etc

    //Constructor sin parametros
    public BiometriaFacial() {

    }

    //Constructor con parametros
    public BiometriaFacial(String vectorRostro, String urlFotoRegistro, String modeloIA, Integer dimension) {
        this.vectorRostro = vectorRostro;
        this.urlFotoRegistro = urlFotoRegistro;
        this.modeloIA = modeloIA;
        this.dimension = dimension;
    }
}
