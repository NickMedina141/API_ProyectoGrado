package com.example.AppSegurity.Controllers;

import com.example.AppSegurity.DTO.LoginRequest;
import com.example.AppSegurity.Services.AutenticacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
public class AutenticacionController {

    @Autowired
    private AutenticacionService autenticacionService;

    //Metodos Post
    @PostMapping("/login/estudiante")
    public ResponseEntity<?> loginEstudiante(@RequestBody LoginRequest loginRequest) {
        try {
            //Iniciamos el login del service pasandole el email y password y guardamos en una variable llamada token
            String token = autenticacionService.loginEstudiante(loginRequest.getEmail(), loginRequest.getPassword());
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Error de autenticación: "+e.getMessage());
        }
    }
    
    @Autowired
    private com.example.AppSegurity.Repositorys.ProfesorRepository profesorRepository;

    @PostMapping("/login/profesor")
    public ResponseEntity<?> loginProfesor(@RequestBody LoginRequest loginRequest) {
        try {
            //Iniciamos el login del service pasandole el email y password y guardamos en una variable llamada token
            String token = autenticacionService.loginProfesor(loginRequest.getEmail(), loginRequest.getPassword());
            
            // Buscamos al profesor para obtener su ID real
            com.example.AppSegurity.Models.Profesor prof = profesorRepository.findByEmailInstitucional(loginRequest.getEmail()).orElse(null);
            
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            if(prof != null) {
                response.put("profesorId", prof.getCodigoProfesor());
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Error de autenticación: "+e.getMessage());
        }
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<?> refrescarToken(@RequestHeader("Authorization") String tokenViejoHeader){
        try {
            //Extraemos el token viejo, cortamos y obtenemos los elementos esenciales del mismo para validarlo
            String tokenLimpio = tokenViejoHeader.substring(7);
            
            // Generamos el token para los estudiantes y docentes
            String nuevoToken = autenticacionService.refrescarToken(tokenLimpio);
            Map<String, String> response = new HashMap<>();
            response.put("token", nuevoToken);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(403).body("Token inválido o expirado");
        }
    }
}
