package com.example.AppSegurity.Config;

import com.example.AppSegurity.Enums.EstadoUsuario;
import com.example.AppSegurity.Models.Profesor;
import com.example.AppSegurity.Repositorys.ProfesorRepository;
import com.example.AppSegurity.Sub_Clases.Auditoria;
import com.example.AppSegurity.Sub_Clases.SeguridadJwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
@ConditionalOnProperty(name = "app.seeders.enabled", havingValue = "true")
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private ProfesorRepository profesorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // TODO: IMPORTANTE - Eliminar o comentar esta clase antes de salir a producción (Sustentación).
        // Esto es una puerta trasera temporal solo para pruebas de desarrollo.
        
        String emailPrueba = "profe@unicesar.edu.co";
        
        // Verifica si el profesor de prueba ya existe
        if (profesorRepository.findByEmailInstitucional(emailPrueba).isEmpty()) {
            
            Profesor profePrueba = new Profesor();
            profePrueba.setCodigoProfesor("PROF-TEST-001");
            profePrueba.setNombre("Profesor");
            profePrueba.setApellidos("De Prueba");
            profePrueba.setCedula("1234567890");
            profePrueba.setEmailInstitucional(emailPrueba);
            
            // Encriptamos la clave '123456' usando Argon2
            profePrueba.setPasswordHash(passwordEncoder.encode("123456"));
            profePrueba.setEstado(EstadoUsuario.ACTIVO);
            profePrueba.setMaterias(new ArrayList<>());
            
            // Llenamos sub clases básicas
            Auditoria auditoria = new Auditoria();
            auditoria.setFechaRegistro(LocalDateTime.now());
            profePrueba.setAuditoria(auditoria);
            
            SeguridadJwt seguridad = new SeguridadJwt();
            profePrueba.setSeguridadJwt(seguridad);
            
            profesorRepository.save(profePrueba);
            System.out.println("=========================================================");
            System.out.println("CREADO PROFESOR DE PRUEBA: " + emailPrueba + " / CLAVE: 123456");
            System.out.println("=========================================================");
        }

        // Verifica si el SuperADMIN ya existe
        String emailAdmin = "admin@unicesar.edu.co";
        if (profesorRepository.findByEmailInstitucional(emailAdmin).isEmpty()) {
            Profesor admin = new Profesor();
            admin.setCodigoProfesor("ADMIN-001");
            admin.setNombre("Super Administrador");
            admin.setApellidos("Institucional UPC");
            admin.setCedula("1000000001");
            admin.setEmailInstitucional(emailAdmin);
            admin.setPasswordHash(passwordEncoder.encode("admin12345"));
            admin.setEstado(EstadoUsuario.ACTIVO);
            admin.setRol("SUPERADMIN");
            admin.setMaterias(new ArrayList<>());

            Auditoria auditoria = new Auditoria();
            auditoria.setFechaRegistro(LocalDateTime.now());
            admin.setAuditoria(auditoria);

            SeguridadJwt seguridad = new SeguridadJwt();
            admin.setSeguridadJwt(seguridad);

            profesorRepository.save(admin);
            System.out.println("=========================================================");
            System.out.println("CREADO SUPERADMIN: " + emailAdmin + " / CLAVE: admin12345");
            System.out.println("=========================================================");
        }
    }
}
