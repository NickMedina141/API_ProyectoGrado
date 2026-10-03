package com.example.AppSegurity.Sub_Clases;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class Conexion {
    //Parametros de la clase
    private String ipEstudiante;
    private String sistemaOperativo;
    private Boolean vpnDetectada;
    private String protocoloConexion;
    private Double latitud;
    private Double longitud;
    private String deviceId;
    private String direccionMac;
    
    //Constructor sin parametros
    public Conexion(){
        
    }
    
    //Constructor con parametros
    public Conexion(String ipEstudiante, String sistemaOperativo, Boolean vpnDetectada, String protocoloConexion, Double latitud, Double longitud){
        this.ipEstudiante = ipEstudiante;
        this.sistemaOperativo = sistemaOperativo;
        this.vpnDetectada = vpnDetectada;
        this.protocoloConexion = protocoloConexion;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public Conexion(String ipEstudiante, String sistemaOperativo, Boolean vpnDetectada, String protocoloConexion, Double latitud, Double longitud, String deviceId, String direccionMac){
        this.ipEstudiante = ipEstudiante;
        this.sistemaOperativo = sistemaOperativo;
        this.vpnDetectada = vpnDetectada;
        this.protocoloConexion = protocoloConexion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.deviceId = deviceId;
        this.direccionMac = direccionMac;
    }
    
}
