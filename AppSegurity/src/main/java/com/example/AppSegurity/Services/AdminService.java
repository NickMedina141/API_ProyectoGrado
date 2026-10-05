package com.example.AppSegurity.Services;

import com.example.AppSegurity.Enums.EstadoUsuario;
import com.example.AppSegurity.Models.Estudiante;
import com.example.AppSegurity.Models.Profesor;
import com.example.AppSegurity.Repositorys.EstudianteRepository;
import com.example.AppSegurity.Repositorys.ExamenRepository;
import com.example.AppSegurity.Repositorys.ProfesorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    @Autowired
    private ProfesorRepository profesorRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private ExamenRepository examenRepository;

    public Map<String, Object> obtenerResumen() {
        Map<String, Object> resumen = new HashMap<>();
        long totalProfes = profesorRepository.count();
        long totalEstudiantes = estudianteRepository.count();
        long totalExamenes = examenRepository.count();

        long profesActivos = profesorRepository.findAll().stream()
                .filter(p -> p.getEstado() == EstadoUsuario.ACTIVO)
                .count();
        long estudiantesActivos = estudianteRepository.findAll().stream()
                .filter(e -> e.getEstadoUsuario() == EstadoUsuario.ACTIVO)
                .count();

        resumen.put("totalProfesores", totalProfes);
        resumen.put("totalEstudiantes", totalEstudiantes);
        resumen.put("totalExamenes", totalExamenes);
        resumen.put("profesoresActivos", profesActivos);
        resumen.put("estudiantesActivos", estudiantesActivos);
        return resumen;
    }

    public List<Profesor> listarProfesores() {
        return profesorRepository.findAll();
    }

    public List<Estudiante> listarEstudiantes() {
        return estudianteRepository.findAll();
    }

    public void cambiarEstadoProfesor(String codigoProfesor, String nuevoEstado) {
        Profesor prof = profesorRepository.findByCodigoProfesor(codigoProfesor)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el docente con código: " + codigoProfesor));
        prof.setEstado(EstadoUsuario.valueOf(nuevoEstado.toUpperCase().trim()));
        profesorRepository.save(prof);
    }

    public void cambiarEstadoEstudiante(String estudianteId, String nuevoEstado) {
        Estudiante est = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el estudiante con ID: " + estudianteId));
        est.setEstadoUsuario(EstadoUsuario.valueOf(nuevoEstado.toUpperCase().trim()));
        estudianteRepository.save(est);
    }
}
