package com.example.AppSegurity.Models;

import com.example.AppSegurity.Enums.EstadoSesion;
import com.example.AppSegurity.Sub_Clases.AnalisisGlobal_IA;
import com.example.AppSegurity.Sub_Clases.Conexion;
import com.example.AppSegurity.Sub_Clases.Proceso_apelacion;
import com.example.AppSegurity.Sub_Clases.ReporteFinal;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter

@Document(collection = "sesion_examen")
public class SesionSupervision {
    //Parametros de la clase
    @Id
    private String sesionId;
    private String examenId;
    private String estudianteId;
    private EstadoSesion estadoSesion;
    private Conexion conexion;
    private AnalisisGlobal_IA analisisGlobalIA;
    private ReporteFinal reporteFinal;
    private Proceso_apelacion apelacion;
    
    @org.springframework.data.annotation.Transient
    private String nombreEstudiante;
    
    @org.springframework.data.annotation.Transient
    private String cedula;
    
    @org.springframework.data.annotation.Transient
    private int cantidadAlertas;
    
    @org.springframework.data.annotation.Transient
    private int porcentajeIntegridad;
    
    //Constructor sin parametros
    public SesionSupervision(){
        
    }
    
    //Constructor con parametros

    public SesionSupervision(String sesionId, String examenId, String estudianteId, EstadoSesion estadoSesion, Conexion conexion, AnalisisGlobal_IA analisisGlobalIA, ReporteFinal reporteFinal, Proceso_apelacion apelacion) {
        this.sesionId = sesionId;
        this.examenId = examenId;
        this.estudianteId = estudianteId;
        this.estadoSesion = estadoSesion;
        this.conexion = conexion;
        this.analisisGlobalIA = analisisGlobalIA;
        this.reporteFinal = reporteFinal;
        this.apelacion = apelacion;
    }
    
}
