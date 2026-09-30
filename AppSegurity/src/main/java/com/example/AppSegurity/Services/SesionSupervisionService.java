package com.example.AppSegurity.Services;

import com.example.AppSegurity.DTO.NuevaAlertaRequest;
import com.example.AppSegurity.Enums.ClaseAlerta;
import com.example.AppSegurity.Enums.EstadoSesion;
import com.example.AppSegurity.Enums.NivelRiesgo;
import com.example.AppSegurity.Models.Alerta.AlertaAudio;
import com.example.AppSegurity.Models.Alerta.AlertaProceso;
import com.example.AppSegurity.Models.Alerta.AlertaTeclado;
import com.example.AppSegurity.Models.Alerta.AlertaVision;
import com.example.AppSegurity.Models.AlertaEvidencia;
import com.example.AppSegurity.Models.Estudiante;
import com.example.AppSegurity.Models.Examen;
import com.example.AppSegurity.Models.SesionSupervision;
import com.example.AppSegurity.Repositorys.AlertaEvidenciaRepository;
import com.example.AppSegurity.Repositorys.EstudianteRepository;
import com.example.AppSegurity.Repositorys.ExamenRepository;
import com.example.AppSegurity.Repositorys.SesionSupervisionRepository;
import com.example.AppSegurity.Sub_Clases.AnalisisGlobal_IA;
import com.example.AppSegurity.Sub_Clases.Conexion;
import com.example.AppSegurity.Sub_Clases.Materia;
import com.example.AppSegurity.Sub_Clases.ReporteFinal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Service
public class SesionSupervisionService {

    @Autowired
    private SesionSupervisionRepository sesionSupervisionRepository;
    @Autowired
    private ExamenRepository examenRepository;
    @Autowired
    private AlertaEvidenciaRepository alertaEvidenciaRepository;
    @Autowired
    private EstudianteRepository estudianteRepository;
    @Autowired
    private SimpMessagingTemplate webSocketMessagingTemplate;
    @Autowired
    private FileStorageService fileStorageService;

    public SesionSupervision iniciarSesion(String idEstudiante, String pinExamen, Conexion conexionInfo) {

        //Se busca y se extrae el PIN del examen
        Examen examen = examenRepository.findByControlAccesoPinSesion(pinExamen)
                .orElseThrow(() -> new RuntimeException("El PIN ingresado no existe o es incorrecto"));

        // Se trae el estudiante de la base de datos para ver las materias que tiene
        Estudiante estudiante = estudianteRepository.findById(idEstudiante)
                .orElseGet(() -> estudianteRepository.findByEmailIgnoreCase(idEstudiante).stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado (ID o Email: " + idEstudiante + ")")));

        // Validamos que el profesor tenga el examen abierto (Estado_pin == ACTIVO)
        if (examen.getControlAcceso() == null || 
            examen.getControlAcceso().getEstadoPin() != com.example.AppSegurity.Enums.Estado_pin.ACTIVO) {
            throw new RuntimeException("El examen no está activo en este momento (Cerrado por el profesor)");
        }
        
        // Validación estricta de tiempo
        if (examen.getFechaExamen() != null) {
            java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
            java.time.LocalDateTime horaInicio = examen.getFechaExamen().getHoraInicio();
            java.time.LocalDateTime horaFin = examen.getFechaExamen().getHoraFin();
            
            if (horaInicio != null && ahora.isBefore(horaInicio)) {
                throw new RuntimeException("El examen aún no ha comenzado. Inicia a las " + horaInicio.toString());
            }
            if (horaFin != null && ahora.isAfter(horaFin)) {
                throw new RuntimeException("El examen ya ha finalizado. Terminó a las " + horaFin.toString());
            }
        }

        //Validamos que el estudiante tenga la materia inscrita
        Boolean esta_inscrito = false;
        for (Materia materia : estudiante.getMateriasInscritas()) {
            if (materia.getCodigoMateria().equals(examen.getMateriaCodigo())) {
                esta_inscrito = true;
                break;
            }
        }

        //En caso de que no este inscrito
        if (!esta_inscrito) {
            throw new RuntimeException("No esta inscrito en la materia para hacer este examen");
        }

        //Validamos la información de la conexión
        //Bloqueamos VPNs o Proxies que puedan enmascarar al estudiante
        if (conexionInfo.getVpnDetectada() != false) {
            throw new RuntimeException("Acceso denegado: Se ha detectado una conexión VPN activa."
                    + " Por favor desactivarla para hacer el examen");
        }

        // Bloqueo por Sistema Operativo ya que nuestra App de Python solo corre en Windows
        if (conexionInfo.getSistemaOperativo() != null && !conexionInfo.getSistemaOperativo().toLowerCase().contains("windows")) {
            throw new RuntimeException("Acceso denegado: El sistema de supervisión actualmente solo es compatible con Windows.");
        }
        
        // --- NUEVAS VALIDACIONES: FECHAS ---
        if (examen.getFechaExamen() != null) {
            java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
            if (examen.getFechaExamen().getHoraInicio() != null && ahora.isBefore(examen.getFechaExamen().getHoraInicio())) {
                throw new RuntimeException("El examen aún no ha comenzado. Empieza a las: " + examen.getFechaExamen().getHoraInicio());
            }
            if (examen.getFechaExamen().getHoraFin() != null && ahora.isAfter(examen.getFechaExamen().getHoraFin())) {
                throw new RuntimeException("El tiempo límite para iniciar el examen ha concluido (" + examen.getFechaExamen().getHoraFin() + ").");
            }
        }

        // Revisar si ya tiene una sesión iniciada para no duplicar tarjetas en el panel del profesor
        List<SesionSupervision> sesionesPrevias = sesionSupervisionRepository.findByExamenId(examen.getCodigoExamen());
        int intentosCompletados = 0;
        boolean accesoBloqueado = false;
        String motivoBloqueo = "";
        
        for (SesionSupervision s : sesionesPrevias) {
            if (s.getEstudianteId().equals(estudiante.getEstudianteId())) {
                if (s.getEstadoSesion() == EstadoSesion.INICIADA) {
                    try {
                        java.util.Map<String, String> evento = new java.util.HashMap<>();
                        evento.put("tipoEvento", "ESTUDIANTE_UNIDO");
                        evento.put("sesionId", s.getSesionId());
                        evento.put("estudianteId", s.getEstudianteId());
                        webSocketMessagingTemplate.convertAndSend("/topic/alertas/" + s.getExamenId(), evento);
                    } catch (Exception e) {}
                    return s; // Reutiliza la sesion si ya estaba adentro
                } else if (s.getEstadoSesion() == EstadoSesion.FINALIZADA || s.getEstadoSesion() == EstadoSesion.ANULADA || s.getEstadoSesion() == EstadoSesion.APELACION_CURSO) {
                    intentosCompletados++;
                    
                    // Si fue anulada por fraude o esta en revision, activamos la bandera de bloqueo
                    if (s.getEstadoSesion() == EstadoSesion.ANULADA) {
                        accesoBloqueado = true;
                        motivoBloqueo = "Tu examen previo fue anulado por faltas al reglamento.";
                    } else if (s.getEstadoSesion() == EstadoSesion.APELACION_CURSO) {
                        accesoBloqueado = true;
                        motivoBloqueo = "Tu solicitud de Segunda Revision se encuentra en proceso.";
                    }
                }
            }
        }
        
        // --- NUEVAS VALIDACIONES: REINTENTOS Y BLOQUEOS DE SEGURIDAD ---
        if (accesoBloqueado) {
             throw new RuntimeException("Acceso denegado: " + motivoBloqueo);
        }
        
        if (examen.getConfiguracionExamen() != null && examen.getConfiguracionExamen().getPermitirReintentos() != null) {
             if (intentosCompletados > examen.getConfiguracionExamen().getPermitirReintentos()) {
                 throw new RuntimeException("Has alcanzado el limite maximo de intentos permitidos.");
             }
        }
        SesionSupervision nuevaSesion = new SesionSupervision();
        nuevaSesion.setExamenId(examen.getCodigoExamen());
        nuevaSesion.setEstudianteId(estudiante.getEstudianteId());
        nuevaSesion.setConexion(conexionInfo);
        nuevaSesion.setEstadoSesion(EstadoSesion.INICIADA);

        //Se guarda todo en la base de datos
        SesionSupervision guardada = sesionSupervisionRepository.save(nuevaSesion);
        
        try {
            java.util.Map<String, String> evento = new java.util.HashMap<>();
            evento.put("tipoEvento", "ESTUDIANTE_UNIDO");
            evento.put("sesionId", guardada.getSesionId());
            evento.put("estudianteId", guardada.getEstudianteId());
            webSocketMessagingTemplate.convertAndSend(
                "/topic/alertas/" + guardada.getExamenId(),
                evento
            );
        } catch (Exception e) {}
        
        return guardada;
    }
    
    public SesionSupervision obtenerSesion(String sesionId) {
        return sesionSupervisionRepository.findById(sesionId).orElseThrow(() -> new RuntimeException("No existe"));
    }
    
    public com.example.AppSegurity.DTO.ReglasExamenResponse obtenerReglasExamen(String sesionId) {
        SesionSupervision sesion = sesionSupervisionRepository.findById(sesionId)
                .orElseThrow(() -> new RuntimeException("La sesión no existe"));
        Examen examen = examenRepository.findById(sesion.getExamenId())
                .orElseThrow(() -> new RuntimeException("El examen no existe"));
                
        String sensibilidad = "MEDIA";
        Integer duracion = 120;
        if (examen.getConfiguracionExamen() != null) {
            if (examen.getConfiguracionExamen().getSensibilidadIA() != null) {
                sensibilidad = examen.getConfiguracionExamen().getSensibilidadIA().toString();
            }
            if (examen.getConfiguracionExamen().getDuracionExamen() != null) {
                duracion = examen.getConfiguracionExamen().getDuracionExamen();
            }
        }
        
        return new com.example.AppSegurity.DTO.ReglasExamenResponse(
            examen.getConfiguracionExamen() != null ? examen.getConfiguracionExamen().getProcesosPermitidos() : new java.util.ArrayList<>(),
            examen.getConfiguracionExamen() != null ? examen.getConfiguracionExamen().getUrlsPermitidas() : new java.util.ArrayList<>(),
            sensibilidad,
            duracion
        );
    }

    public void registrarAlerta(String sesionId, NuevaAlertaRequest alertaRequest) {
        //Validamos primero que nada que la sesionSupervision exista en la base de datos
        SesionSupervision sesionSupervision = sesionSupervisionRepository.findById(sesionId)
                .orElseThrow(() -> new RuntimeException("La sesión no existe"));

        com.example.AppSegurity.Models.Examen examen = examenRepository.findById(sesionSupervision.getExamenId())
                .orElseThrow(() -> new RuntimeException("El examen no existe"));

        String nombreEstudiante = "Desconocido";
        var est = estudianteRepository.findById(sesionSupervision.getEstudianteId()).orElse(null);
        if (est != null) {
            nombreEstudiante = est.getNombre() + "_" + est.getApellidos();
            nombreEstudiante = nombreEstudiante.replaceAll("[^a-zA-Z0-9_-]", "_");
        }

        AlertaEvidencia alertaGuardar = null;

        if (alertaRequest.getClaseAlerta() == ClaseAlerta.VISION || alertaRequest.getClaseAlerta() == ClaseAlerta.OBJETO) {
            String rutaWebcam = "E2EE_WEBCAM";
            String rutaPantalla = "E2EE_PANTALLA";
            
            alertaGuardar = new AlertaVision(
                    alertaRequest.getTipoEvidenciaVision(),
                    alertaRequest.getCantidadRostros(),
                    alertaRequest.getObjetoDetectado(),
                    alertaRequest.getConfianzaIa(),
                    rutaWebcam,
                    rutaPantalla);

        } else if (alertaRequest.getClaseAlerta() == ClaseAlerta.AUDIO) {
            String rutaAudio = "E2EE_AUDIO";
            alertaGuardar = new AlertaAudio(
                    alertaRequest.getTranscripcion(),
                    alertaRequest.getVocesDetectadas(),
                    alertaRequest.getConfianzaVoz(),
                    rutaAudio);

        } else if (alertaRequest.getClaseAlerta() == ClaseAlerta.PROCESO) {
            String rutaPantalla = "E2EE_PROCESO";
            
            alertaGuardar = new AlertaProceso(
                    alertaRequest.getPidProceso(),
                    alertaRequest.getNombreProceso(),
                    alertaRequest.getCategoriaProceso(),
                    alertaRequest.getAccionTomada(),
                    rutaPantalla);

        } else if (alertaRequest.getClaseAlerta() == ClaseAlerta.TECLADO) {
            String rutaPantalla = "E2EE_TECLADO";
            alertaGuardar = new AlertaTeclado(
                    alertaRequest.getCombinacionTeclas(),
                    alertaRequest.getPatronSospechoso(),
                    rutaPantalla);
        }

        //Se llena en la clase padre los datos basicos que tendran todos los tipos de alertas
        if (alertaGuardar != null) {
            alertaGuardar.setHashWebcam(alertaRequest.getHashWebcam());
            alertaGuardar.setHashPantalla(alertaRequest.getHashPantalla());
            alertaGuardar.setHashAudio(alertaRequest.getHashAudio());
            alertaGuardar.setSesionId(sesionId);
            alertaGuardar.setClaseAlerta(alertaRequest.getClaseAlerta());
            alertaGuardar.setNivelRiesgo(alertaRequest.getNivelRiesgo());
            alertaGuardar.setHoraCaptura(LocalDateTime.now());
            //Por ultimo guardamos la alrta en la base de datos
            alertaEvidenciaRepository.save(alertaGuardar);
            
            // Enviar notificacion WebSocket al feed global del profesor
            // El profesor esta suscrito a /topic/alertas/{codigoExamen}
            try {
                alertaGuardar.setNombreEstudiante(nombreEstudiante.replace("_", " ")); // Formato bonito
                // Attach transient base64 for real-time bypass
                alertaGuardar.setBase64WebcamTransient(alertaRequest.getUrlFotoWebcam());
                alertaGuardar.setBase64PantallaTransient(alertaRequest.getUrlCapturaPantalla());
                alertaGuardar.setBase64AudioTransient(alertaRequest.getUrlAudio());
                
                webSocketMessagingTemplate.convertAndSend(
                    "/topic/alertas/" + sesionSupervision.getExamenId(),
                    alertaGuardar
                );
            } catch (Exception e) {
                // Ignorar error de WS
            }

        } //En caso de que venga null reportamos una excepción
        else {
            throw new RuntimeException("La alerta vino vacia");
        }

    }

    public void finalizarSesion(String idSesion) {
        //Traer la info de la sesión a finalizar
        SesionSupervision sesionSupervision = sesionSupervisionRepository.findById(idSesion)
                .orElseThrow(() -> new RuntimeException("Error al encontrar la sesión a finalizar"));

        //Se cambia el estado a finalizada
        sesionSupervision.setEstadoSesion(EstadoSesion.FINALIZADA);

        //Se guarda el cambio en la base de datos MongoDB
        sesionSupervisionRepository.save(sesionSupervision);
    }

    public List<AlertaEvidencia> obtenerHistorialAlertas(String sesionId) {
        //Se busca en alertaEvidenciaRepository todas las alertas que tengan ese id_sesion
        return alertaEvidenciaRepository.findBySesionIdOrderByHoraCapturaAsc(sesionId);
    }
    
    public List<AlertaEvidencia> obtenerAlertasExamen(String examenId) {
        // Obtenemos todas las sesiones de este examen
        List<SesionSupervision> sesiones = sesionSupervisionRepository.findByExamenId(examenId);
        List<String> sesionIds = new java.util.ArrayList<>();
        java.util.Map<String, String> mapNombres = new java.util.HashMap<>();
        
        for (SesionSupervision s : sesiones) {
            sesionIds.add(s.getSesionId());
            var est = estudianteRepository.findById(s.getEstudianteId()).orElse(null);
            if (est != null) {
                mapNombres.put(s.getSesionId(), est.getNombre() + " " + est.getApellidos());
            }
        }
        
        if (sesionIds.isEmpty()) return new java.util.ArrayList<>();
        
        List<AlertaEvidencia> alertas = alertaEvidenciaRepository.findBySesionIdInOrderByHoraCapturaDesc(sesionIds);
        for (AlertaEvidencia a : alertas) {
            a.setNombreEstudiante(mapNombres.getOrDefault(a.getSesionId(), "Desconocido"));
        }
        
        return alertas;
    }

    public void guardarVeredicto_Reporte(String sesionId, AnalisisGlobal_IA analisis, String veredictoDocente, String urlPDF) {
        //Se busca la sesionSupervision en la base de datos
        SesionSupervision sesionSupervision = sesionSupervisionRepository.findById(sesionId)
                .orElseThrow(() -> new RuntimeException("Error al encontrar la sesión"));

        //Se llena el elemento de analisis global de la ia 
        AnalisisGlobal_IA analisisGlobal_IA = analisis;

        //Se crea un reporte final y se llena con los datos necesarios
        ReporteFinal reporteFinal = new ReporteFinal();
        reporteFinal.setVeredictoProfesor(veredictoDocente);
        reporteFinal.setUrlPdfReporte(urlPDF);

        //Metemos ese reporte a la sesión 
        sesionSupervision.setAnalisisGlobalIA(analisisGlobal_IA);
        sesionSupervision.setReporteFinal(reporteFinal);

        //Al final se guarda todo en la base de datos
        sesionSupervisionRepository.save(sesionSupervision);
    }

    public List<SesionSupervision> obtenerSesionesPorExamen(String examenId, boolean incluirFinalizadas) {
        List<SesionSupervision> sesiones = sesionSupervisionRepository.findByExamenId(examenId);
        
        // Remover las que ya fueron cerradas/finalizadas si no queremos incluirlas
        if (!incluirFinalizadas) {
            sesiones.removeIf(s -> s.getEstadoSesion() != EstadoSesion.INICIADA);
        }
        
        // Agregar el nombre real del estudiante y metricas
        for (SesionSupervision s : sesiones) {
            estudianteRepository.findById(s.getEstudianteId()).ifPresent(est -> {
                s.setNombreEstudiante(est.getNombre() + " " + est.getApellidos());
            });
            
            List<com.example.AppSegurity.Models.AlertaEvidencia> alertas = alertaEvidenciaRepository.findBySesionIdOrderByHoraCapturaAsc(s.getSesionId());
            s.setCantidadAlertas(alertas.size());
            
            // Calcular integridad dinámica basada en las alertas
            int descuento = 0;
            for (com.example.AppSegurity.Models.AlertaEvidencia alerta : alertas) {
                if (alerta.getNivelRiesgo() == com.example.AppSegurity.Enums.NivelRiesgo.BAJO) {
                    descuento += 5;
                } else if (alerta.getNivelRiesgo() == com.example.AppSegurity.Enums.NivelRiesgo.MEDIO) {
                    descuento += 15;
                } else if (alerta.getNivelRiesgo() == com.example.AppSegurity.Enums.NivelRiesgo.ALTO) {
                    descuento += 30;
                }
            }
            int integridadReal = Math.max(0, 100 - descuento);
            s.setPorcentajeIntegridad(integridadReal);
        }
        
        return sesiones;
    }

}
