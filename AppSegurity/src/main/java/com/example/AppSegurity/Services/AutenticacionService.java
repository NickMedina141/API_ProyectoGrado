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
import com.example.AppSegurity.DTO.RegistroSolicitudRequest;
import com.example.AppSegurity.DTO.RegistroConfirmarRequest;
import com.example.AppSegurity.DTO.RecuperarSolicitudRequest;
import com.example.AppSegurity.DTO.RecuperarConfirmarRequest;
import com.example.AppSegurity.Sub_Clases.Auditoria;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    // --- CACHÉ DE CÓDIGOS OTP PARA REGISTRO TEMPORAL (10 MINUTOS) ---
    private static class OtpInfo {
        String codigo;
        LocalDateTime expiracion;
        String rol;

        OtpInfo(String codigo, LocalDateTime expiracion, String rol) {
            this.codigo = codigo;
            this.expiracion = expiracion;
            this.rol = rol;
        }
    }

    private final java.util.concurrent.ConcurrentHashMap<String, OtpInfo> cacheOtp = new java.util.concurrent.ConcurrentHashMap<>();

    public void solicitarCodigoRegistro(RegistroSolicitudRequest req) {
        if (req == null || req.getEmail() == null || req.getCedula() == null || req.getRol() == null) {
            throw new IllegalArgumentException("Todos los campos obligatorios deben ser diligenciados.");
        }

        String email = req.getEmail().trim().toLowerCase();
        String cedula = req.getCedula().trim();
        String rol = req.getRol().trim().toUpperCase();

        // 1. Validar dominio institucional (@unicesar.edu.co o @gmail.com de prueba)
        if (!email.matches("^[A-Za-z0-9._%+-]+@(unicesar\\.edu\\.co|gmail\\.com)$")) {
            throw new IllegalArgumentException("El correo debe pertenecer al dominio institucional (@unicesar.edu.co).");
        }

        // 2. Validar que la cédula sea numérica y válida
        if (!cedula.matches("^[0-9]{6,12}$")) {
            throw new IllegalArgumentException("La cédula debe contener entre 6 y 12 dígitos numéricos.");
        }

        // 3. Validar no duplicados en BD según el rol
        if ("ESTUDIANTE".equals(rol)) {
            if (!estudianteRepository.findByEmailIgnoreCase(email).isEmpty()) {
                throw new IllegalStateException("El correo institucional ya se encuentra registrado para un estudiante.");
            }
            if (estudianteRepository.findByCedula(cedula).isPresent()) {
                throw new IllegalStateException("La cédula ya se encuentra registrada en el sistema.");
            }
        } else if ("PROFESOR".equals(rol)) {
            if (profesorRepository.findByEmailInstitucional(email).isPresent()) {
                throw new IllegalStateException("El correo institucional ya se encuentra registrado para un docente.");
            }
            if (profesorRepository.findByCedula(cedula).isPresent()) {
                throw new IllegalStateException("La cédula ya se encuentra registrada en el sistema.");
            }
        } else {
            throw new IllegalArgumentException("Rol no válido. Debe ser ESTUDIANTE o PROFESOR.");
        }

        // 4. Generar código de 6 dígitos numéricos
        String codigoOtp = String.format("%06d", new java.util.Random().nextInt(1000000));

        // 5. Guardar en caché con expiración de 10 minutos
        cacheOtp.put(email, new OtpInfo(codigoOtp, LocalDateTime.now().plusMinutes(10), rol));

        // 6. Enviar correo electrónico institucional
        emailService.enviarCodigoVerificacion(email, req.getNombre(), codigoOtp, rol);
    }

    public void confirmarRegistroEstudiante(RegistroConfirmarRequest req) {
        if (req == null || req.getEmail() == null || req.getCodigoOtp() == null || req.getPassword() == null) {
            throw new IllegalArgumentException("Todos los campos son requeridos.");
        }

        String email = req.getEmail().trim().toLowerCase();
        String codigoIngresado = req.getCodigoOtp().trim();

        // 1. Validar OTP en caché
        OtpInfo otpInfo = cacheOtp.get(email);
        if (otpInfo == null) {
            throw new IllegalStateException("No hay una solicitud de código pendiente para este correo o el código ya venció.");
        }
        if (LocalDateTime.now().isAfter(otpInfo.expiracion)) {
            cacheOtp.remove(email);
            throw new IllegalStateException("El código de verificación ha expirado. Solicita uno nuevo.");
        }
        if (!otpInfo.codigo.equals(codigoIngresado)) {
            throw new IllegalArgumentException("El código de verificación de 6 dígitos es incorrecto.");
        }

        // 2. Validar contraseña mínima
        if (req.getPassword().length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
        }

        // 3. Crear el nuevo Estudiante
        Estudiante nuevo = new Estudiante();
        nuevo.setEstudianteId("EST-" + req.getCedula().trim());
        nuevo.setNombre(req.getNombre().trim());
        nuevo.setApellidos(req.getApellidos().trim());
        nuevo.setCedula(req.getCedula().trim());
        nuevo.setEmail(email);
        nuevo.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        nuevo.setEstadoUsuario(EstadoUsuario.ACTIVO);
        nuevo.setMateriasInscritas(new java.util.ArrayList<>());

        Auditoria aud = new Auditoria();
        aud.setFechaRegistro(LocalDateTime.now());
        nuevo.setAuditoria(aud);

        estudianteRepository.save(nuevo);
        cacheOtp.remove(email);
        System.out.println("[REGISTRO EXITOSO] Estudiante creado: " + email + " (" + nuevo.getEstudianteId() + ")");
    }

    public void confirmarRegistroProfesor(RegistroConfirmarRequest req) {
        if (req == null || req.getEmail() == null || req.getCodigoOtp() == null || req.getPassword() == null) {
            throw new IllegalArgumentException("Todos los campos son requeridos.");
        }

        String email = req.getEmail().trim().toLowerCase();
        String codigoIngresado = req.getCodigoOtp().trim();

        // 1. Validar OTP en caché
        OtpInfo otpInfo = cacheOtp.get(email);
        if (otpInfo == null) {
            throw new IllegalStateException("No hay una solicitud de código pendiente para este correo o el código ya venció.");
        }
        if (LocalDateTime.now().isAfter(otpInfo.expiracion)) {
            cacheOtp.remove(email);
            throw new IllegalStateException("El código de verificación ha expirado. Solicita uno nuevo.");
        }
        if (!otpInfo.codigo.equals(codigoIngresado)) {
            throw new IllegalArgumentException("El código de verificación de 6 dígitos es incorrecto.");
        }

        // 2. Validar contraseña mínima
        if (req.getPassword().length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
        }

        // 3. Código profesor
        String codProf = (req.getCodigoProfesor() != null && !req.getCodigoProfesor().trim().isEmpty())
                ? req.getCodigoProfesor().trim()
                : "PROF-" + req.getCedula().trim();

        // 4. Crear el nuevo Profesor
        Profesor nuevo = new Profesor();
        nuevo.setCodigoProfesor(codProf);
        nuevo.setNombre(req.getNombre().trim());
        nuevo.setApellidos(req.getApellidos().trim());
        nuevo.setCedula(req.getCedula().trim());
        nuevo.setEmailInstitucional(email);
        nuevo.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        nuevo.setEstado(EstadoUsuario.ACTIVO);
        nuevo.setMaterias(new java.util.ArrayList<>());

        Auditoria aud = new Auditoria();
        aud.setFechaRegistro(LocalDateTime.now());
        nuevo.setAuditoria(aud);

        profesorRepository.save(nuevo);
        cacheOtp.remove(email);
        System.out.println("[REGISTRO EXITOSO] Docente creado: " + email + " (" + nuevo.getCodigoProfesor() + ")");
    }

    public void solicitarCodigoRecuperacion(RecuperarSolicitudRequest req) {
        if (req == null || req.getEmail() == null || req.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El correo institucional es obligatorio.");
        }

        String email = req.getEmail().trim().toLowerCase();

        // 1. Buscar si el usuario existe como Estudiante o Profesor
        String nombreUsuario = null;
        boolean existe = false;

        var estudiantes = estudianteRepository.findByEmailIgnoreCase(email);
        if (!estudiantes.isEmpty()) {
            nombreUsuario = estudiantes.get(0).getNombre() + " " + estudiantes.get(0).getApellidos();
            existe = true;
        } else {
            var profOpt = profesorRepository.findByEmailInstitucional(email);
            if (profOpt.isPresent()) {
                nombreUsuario = profOpt.get().getNombre() + " " + profOpt.get().getApellidos();
                existe = true;
            }
        }

        if (!existe) {
            throw new IllegalArgumentException("No se encontró ninguna cuenta asociada al correo institucional: " + email);
        }

        // 2. Generar código OTP de 6 dígitos numéricos
        String codigoOtp = String.format("%06d", new java.util.Random().nextInt(1000000));

        // 3. Guardar en caché con expiración de 10 minutos
        cacheOtp.put(email, new OtpInfo(codigoOtp, LocalDateTime.now().plusMinutes(10), "RECUPERACION"));

        // 4. Enviar correo de restablecimiento
        emailService.enviarCodigoRecuperacion(email, nombreUsuario, codigoOtp);
    }

    public void confirmarRecuperacionPassword(RecuperarConfirmarRequest req) {
        if (req == null || req.getEmail() == null || req.getCodigoOtp() == null || req.getNuevaPassword() == null) {
            throw new IllegalArgumentException("Todos los campos son requeridos.");
        }

        String email = req.getEmail().trim().toLowerCase();
        String codigoIngresado = req.getCodigoOtp().trim();

        // 1. Validar OTP en caché
        OtpInfo otpInfo = cacheOtp.get(email);
        if (otpInfo == null) {
            throw new IllegalStateException("No hay una solicitud de código pendiente para este correo o el código ya venció.");
        }
        if (LocalDateTime.now().isAfter(otpInfo.expiracion)) {
            cacheOtp.remove(email);
            throw new IllegalStateException("El código de verificación ha expirado. Solicita uno nuevo.");
        }
        if (!otpInfo.codigo.equals(codigoIngresado)) {
            throw new IllegalArgumentException("El código de verificación de 6 dígitos es incorrecto.");
        }

        // 2. Validar contraseña mínima (8 caracteres)
        if (req.getNuevaPassword().length() < 8) {
            throw new IllegalArgumentException("La nueva contraseña debe tener al menos 8 caracteres.");
        }

        // 3. Actualizar contraseña en la BD
        String nuevoHash = passwordEncoder.encode(req.getNuevaPassword());
        boolean actualizada = false;

        var estudiantes = estudianteRepository.findByEmailIgnoreCase(email);
        if (!estudiantes.isEmpty()) {
            for (Estudiante est : estudiantes) {
                est.setPasswordHash(nuevoHash);
                estudianteRepository.save(est);
            }
            actualizada = true;
            System.out.println("[RECUPERACIÓN EXITOSA] Contraseña actualizada para estudiante: " + email);
        } else {
            var profOpt = profesorRepository.findByEmailInstitucional(email);
            if (profOpt.isPresent()) {
                Profesor prof = profOpt.get();
                prof.setPasswordHash(nuevoHash);
                profesorRepository.save(prof);
                actualizada = true;
                System.out.println("[RECUPERACIÓN EXITOSA] Contraseña actualizada para docente: " + email);
            }
        }

        if (!actualizada) {
            throw new IllegalStateException("No se pudo localizar el usuario para actualizar sus credenciales.");
        }

        cacheOtp.remove(email);
    }

    public String loginEstudiante(String email, String passwordTextoPlano) {
        //
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, passwordTextoPlano));

        //Buscamos al estudiante por su email en la base de datos 
        java.util.List<Estudiante> estudiantes = estudianteRepository.findByEmailIgnoreCase(email);
        Estudiante estudiante = null;
        if (estudiantes.size() > 1) {
            for (Estudiante e : estudiantes) {
                if (e.getEstudianteId() != null && e.getEstudianteId().contains(" ")) {
                    estudianteRepository.delete(e);
                } else {
                    estudiante = e;
                }
            }
        } else if (estudiantes.size() == 1) {
            estudiante = estudiantes.get(0);
        }
        if (estudiante == null) {
            throw new RuntimeException("No se encontro el email del estudiante");
        }

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
