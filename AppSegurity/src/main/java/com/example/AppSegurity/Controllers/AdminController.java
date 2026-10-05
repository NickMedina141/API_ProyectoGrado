package com.example.AppSegurity.Controllers;

import com.example.AppSegurity.Services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/resumen")
    public ResponseEntity<?> obtenerResumen() {
        try {
            return ResponseEntity.ok(adminService.obtenerResumen());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/profesores")
    public ResponseEntity<?> listarProfesores() {
        try {
            return ResponseEntity.ok(adminService.listarProfesores());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/estudiantes")
    public ResponseEntity<?> listarEstudiantes() {
        try {
            return ResponseEntity.ok(adminService.listarEstudiantes());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/profesores/{codigoProfesor}/estado")
    public ResponseEntity<?> cambiarEstadoProfesor(@PathVariable String codigoProfesor, @RequestBody Map<String, String> payload) {
        try {
            String nuevoEstado = payload.get("estado");
            if (nuevoEstado == null || nuevoEstado.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El campo estado es requerido"));
            }
            adminService.cambiarEstadoProfesor(codigoProfesor, nuevoEstado);
            return ResponseEntity.ok(Map.of("mensaje", "Estado del docente actualizado con éxito a " + nuevoEstado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/estudiantes/{estudianteId}/estado")
    public ResponseEntity<?> cambiarEstadoEstudiante(@PathVariable String estudianteId, @RequestBody Map<String, String> payload) {
        try {
            String nuevoEstado = payload.get("estado");
            if (nuevoEstado == null || nuevoEstado.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El campo estado es requerido"));
            }
            adminService.cambiarEstadoEstudiante(estudianteId, nuevoEstado);
            return ResponseEntity.ok(Map.of("mensaje", "Estado del estudiante actualizado con éxito a " + nuevoEstado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
