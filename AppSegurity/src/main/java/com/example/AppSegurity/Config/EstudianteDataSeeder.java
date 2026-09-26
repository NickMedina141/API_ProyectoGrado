package com.example.AppSegurity.Config;

import com.example.AppSegurity.Enums.EstadoUsuario;
import com.example.AppSegurity.Enums.Estado_pin;
import com.example.AppSegurity.Enums.Sensibilidad_IA;
import com.example.AppSegurity.Models.Estudiante;
import com.example.AppSegurity.Models.Examen;
import com.example.AppSegurity.Models.Profesor;
import com.example.AppSegurity.Repositorys.EstudianteRepository;
import com.example.AppSegurity.Repositorys.ExamenRepository;
import com.example.AppSegurity.Repositorys.ProfesorRepository;
import com.example.AppSegurity.Sub_Clases.Auditoria;
import com.example.AppSegurity.Sub_Clases.ConfiguracionExamen;
import com.example.AppSegurity.Sub_Clases.ControlAcceso;
import com.example.AppSegurity.Sub_Clases.SeguridadJwt;
import com.example.AppSegurity.Sub_Clases.Materia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@ConditionalOnProperty(name = "app.seeders.enabled", havingValue = "true")
public class EstudianteDataSeeder implements CommandLineRunner {

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private ExamenRepository examenRepository;

    @Autowired
    private ProfesorRepository profesorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        
        // --- DATA BÁSICA PARA GENERAR ---
        String[] nombresEst = {"Carlos", "Maria", "Andres", "Laura", "Jorge", "Camila", "Luis", "Daniela", "Diego", "Valentina"};
        String[] apellidosEst = {"Gomez", "Rodriguez", "Martinez", "Lopez", "Garcia", "Perez", "Sanchez", "Ramirez", "Torres", "Flores"};
        
        String[] nombresProf = {"Roberto", "Ana", "Miguel", "Patricia", "Fernando", "Elena", "Alejandro", "Carmen", "Javier", "Diana"};
        String[] apellidosProf = {"Mendoza", "Castillo", "Vargas", "Rojas", "Moreno", "Diaz", "Ortiz", "Silva", "Romero", "Herrera"};
        
        String[] codigosMateria = {"MAT-201", "ING-301", "SIS-401", "FIS-102", "MAT-105", "SIS-205", "SIS-306", "SIS-501", "RED-401", "ING-501"};
        String[] nombresMateria = {"Cálculo Vectorial", "Ingeniería de Software", "Bases de Datos", "Física Mecánica", "Álgebra Lineal", "Estructura de Datos", "Sistemas Operativos", "Inteligencia Artificial", "Redes", "Arquitectura"};
        
        String claveGlobal = passwordEncoder.encode("123456");

        // 1. POBLAR PROFESORES
        if (profesorRepository.count() < 10) {
            System.out.println("Inyectando 10 Profesores piloto...");
            for (int i = 0; i < 10; i++) {
                String email = nombresProf[i].toLowerCase() + "." + apellidosProf[i].toLowerCase() + "@unicesar.edu.co";
                if (profesorRepository.findByEmailInstitucional(email).isEmpty()) {
                    Profesor prof = new Profesor();
                    prof.setCodigoProfesor("PROF-" + (1000 + i)); // Restaurado para evitar bugs de ruteo de Spring Boot
                    prof.setNombre(nombresProf[i]);
                    prof.setApellidos(apellidosProf[i]);
                    prof.setCedula("77" + (100000 + i));
                    prof.setEmailInstitucional(email);
                    prof.setPasswordHash(claveGlobal);
                    prof.setEstado(EstadoUsuario.ACTIVO);
                    // Le asignamos todas las materias a los profes para que no haya conflictos en la UI
                    List<Materia> materiasDelProfe = new ArrayList<>();
                    for (int j = 0; j < 10; j++) {
                        Materia mat = new Materia();
                        mat.setCodigoMateria(codigosMateria[j]);
                        mat.setNombreMateria(nombresMateria[j]);
                        materiasDelProfe.add(mat);
                    }
                    prof.setMaterias(materiasDelProfe);
                    
                    Auditoria aud = new Auditoria();
                    aud.setFechaRegistro(LocalDateTime.now());
                    prof.setAuditoria(aud);
                    
                    profesorRepository.save(prof);
                }
            }
        }

        // 2. POBLAR ESTUDIANTES (Asignándoles todas las materias para que puedan entrar a cualquier examen)
        if (estudianteRepository.count() < 10) {
            System.out.println("Inyectando 10 Estudiantes piloto...");
            List<Materia> todasLasMaterias = new ArrayList<>();
            for (int j = 0; j < 10; j++) {
                Materia m = new Materia();
                m.setCodigoMateria(codigosMateria[j]);
                m.setNombreMateria(nombresMateria[j]);
                todasLasMaterias.add(m);
            }

            for (int i = 0; i < 10; i++) {
                String email = nombresEst[i].toLowerCase() + "." + apellidosEst[i].toLowerCase() + "@unicesar.edu.co";
                if (estudianteRepository.findByEmail(email).isEmpty()) {
                    Estudiante est = new Estudiante();
                    est.setEstudianteId("EST-" + (2000 + i));
                    est.setNombre(nombresEst[i]);
                    est.setApellidos(apellidosEst[i]);
                    est.setCedula("10" + (900000 + i));
                    est.setEmail(email);
                    est.setPasswordHash(claveGlobal);
                    est.setEstadoUsuario(EstadoUsuario.ACTIVO);
                    est.setMateriasInscritas(todasLasMaterias);
                    
                    Auditoria aud = new Auditoria();
                    aud.setFechaRegistro(LocalDateTime.now());
                    est.setAuditoria(aud);
                    
                    estudianteRepository.save(est);
                }
            }
        }

        // 3. POBLAR EXÁMENES (TODOS asignados al Profesor 1 para poder verlos todos en su Dashboard)
        System.out.println("Inyectando 10 Exámenes piloto (asignados a PROF-1000)...");
        String profId = "PROF-1000"; // Se los asignamos TODOS a Roberto Mendoza
        
        for (int i = 0; i < 10; i++) {
            String pin = String.valueOf(1000 + i); // Pines del 1000 al 1009
            String codigoEx = "EXAM-" + (3000 + i);
            
            Examen ex = examenRepository.findById(codigoEx).orElse(new Examen());
            ex.setCodigoExamen(codigoEx);
            ex.setProfesorId(profId); // FORZAMOS que se actualice al correo correcto

            ex.setMateriaCodigo(codigosMateria[i]);
            
            if (ex.getControlAcceso() == null) {
                ControlAcceso ca = new ControlAcceso();
                ca.setPinSesion(pin);
                ca.setEstadoPin(Estado_pin.ACTIVO);
                ex.setControlAcceso(ca);
            }
            
            if (ex.getFechaExamen() == null) {
                com.example.AppSegurity.Sub_Clases.FechaExamen fechas = new com.example.AppSegurity.Sub_Clases.FechaExamen();
                fechas.setHoraInicio(LocalDateTime.now().minusHours(1));
                fechas.setHoraFin(LocalDateTime.now().plusHours(2));
                ex.setFechaExamen(fechas);
            }
            
            if (ex.getConfiguracionExamen() == null) {
                ConfiguracionExamen conf = new ConfiguracionExamen();
                conf.setSensibilidadIA(Sensibilidad_IA.ALTA);
                conf.setDuracionExamen(120);
                conf.setPermitirReintentos(1);
                conf.setProcesosPermitidos(Arrays.asList("winword.exe", "excel.exe", "calc.exe"));
                conf.setUrlsPermitidas(Arrays.asList("https://moodle.unicesar.edu.co"));
                ex.setConfiguracionExamen(conf);
            }
            
            examenRepository.save(ex);
        }
        
        System.out.println("=========================================================");
        System.out.println("BASE DE DATOS POBLADA CON ÉXITO PARA LA PRUEBA PILOTO");
        System.out.println("Clave para todos (estudiantes y profesores): 123456");
        System.out.println("Pines de exámenes creados: 1000, 1001, 1002... hasta 1009");
        System.out.println("=========================================================");
    }
}
