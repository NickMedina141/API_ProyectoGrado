package com.example.AppSegurity.Services;

import com.example.AppSegurity.Enums.EstadoUsuario;
import com.example.AppSegurity.Models.Estudiante;
import com.example.AppSegurity.Models.Profesor;
import com.example.AppSegurity.Repositorys.EstudianteRepository;
import com.example.AppSegurity.Repositorys.ProfesorRepository;
import com.example.AppSegurity.Security.JwtProveedor;
import com.example.AppSegurity.Security.ServicioDetallesUsuarioPersonalizados;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AutenticacionService {

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private ProfesorRepository profesorRepository;

    @Autowired
    private JwtProveedor jwtProveedor;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ServicioDetallesUsuarioPersonalizados userDetailsService;

    public String loginEstudiante(String email, String passwordTextoPlano) {
        //
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, passwordTextoPlano));

        //Buscamos al estudiante por su email en la base de datos 
        Estudiante estudiante = estudianteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No se encontro el email del estudiante"));

        //Validamos que el estudiante en cuestión su estado no sea inactivo o pfu
        if (estudiante.getEstadoUsuario() != EstadoUsuario.ACTIVO) {
            throw new RuntimeException("Solo los estudiante activos pueden iniciar sesión");
        }

        //Ya con el estudiante validado generamos el token del estudiante
        String token = jwtProveedor.generarToken(auth);

        //Actualizamos auditoria del estudiante para llevar el registro
        estudiante.getAuditoria().setUltimoAcceso(LocalDateTime.now());

        //Guardamos los cambios del estudiante en su base de datos
        estudianteRepository.save(estudiante);

        //Retornamos el token generado para ese estudiante
        return token;
    }

    public String loginProfesor(String email, String passwordTextoPlano) {
        //
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, passwordTextoPlano));

        //Buscamos al profesor por su email en la base de datos 
        Profesor profesor = profesorRepository.findByEmailInstitucional(email)
                .orElseThrow(() -> new RuntimeException("No se encontro el email del profesor"));

        //Validamos que el profesor en cuestión su estado no sea inactivo o suspendido
        if (profesor.getEstado() != EstadoUsuario.ACTIVO) {
            throw new RuntimeException("Solo los profesores activos pueden iniciar sesión");
        }

        //Ya con el profesor validado generamos el token del mismo
        String token = jwtProveedor.generarToken(auth);

        //Actualizamos auditoria del profesor para llevar el registro
        profesor.getAuditoria().setUltimoAcceso(LocalDateTime.now());

        //Guardamos los cambios del profesor en la base de datos
        profesorRepository.save(profesor);

        //Retornamos el token generado para ese profesor
        return token;
    }

    public String refrescarToken(String freshTokenViejo) {

        //Validamos que el token sigue siendo autentico
        if (!jwtProveedor.validarFirma(freshTokenViejo)) {
            throw new RuntimeException("El token proporcionado es invalido o fue alterado");
        }
        //Extraemos el email del token viejo
        String email = jwtProveedor.extraerEmail(freshTokenViejo);

        //Buscamos al usuario en la base de datos
        var detallesUsuario = userDetailsService.loadUserByUsername(email);

        //Creados una credencial manual temporal para el usuario (Profesor o estudiante)
        Authentication authTemporal = new UsernamePasswordAuthenticationToken(
                detallesUsuario, null, detallesUsuario.getAuthorities());

        //generamos el nuevo token y lo retornamos
        return jwtProveedor.generarToken(authTemporal);

    }

    public void cerrarSesion() {
        System.out.println("Petición de cerrar sesión recibida. El cliente debe destruir su token localmente.");
    }
}
