package com.example.AppSegurity.Services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Async
    public void enviarResolucionApelacion(String correoEstudiante, String nombreEstudiante, String codigoExamen, boolean esAprobada, String motivoProfesor) {
        try {
            MimeMessage mensaje = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(correoEstudiante);
            helper.setSubject("Resolucion de Segunda Revision - Examen: " + codigoExamen);

            String colorEstado = esAprobada ? "#27AE60" : "#C0392B";
            String textoEstado = esAprobada ? "APROBADA (SANCION LEVANTADA)" : "RECHAZADA (SANCION MANTENIDA)";
            
            String parrafoContexto = esAprobada 
                    ? "Tras una evaluaci&oacute;n exhaustiva de la evidencia t&eacute;cnica (grabaciones de c&aacute;mara, audio y monitoreo de perif&eacute;ricos) recopilada durante la sesi&oacute;n, el comit&eacute; ha determinado que las anomal&iacute;as detectadas por el sistema no constituyen una falta al reglamento estudiantil."
                    : "Tras una auditor&iacute;a exhaustiva de la evidencia forense digital (incluyendo an&aacute;lisis de comportamiento visual, procesos de escritorio en segundo plano y patrones auditivos), el comit&eacute; ha ratificado que existen pruebas contundentes de actividad fraudulenta que violan el reglamento de integridad acad&eacute;mica.";

            String htmlTemplate = "<div style=\"font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; max-width: 650px; margin: 0 auto; border: 1px solid #E0E0E0; border-radius: 4px; overflow: hidden; box-shadow: 0 4px 10px rgba(0,0,0,0.05);\">"
                    + "<div style=\"background-color: #003B13; padding: 25px 30px; text-align: center; border-bottom: 4px solid #F1C40F;\">"
                    + "<h2 style=\"color: #FFFFFF; margin: 0; font-size: 20px; font-weight: 600; letter-spacing: 0.5px;\">Comit&eacute; de Integridad Institucional</h2>"
                    + "<p style=\"color: #A9DFBF; margin: 5px 0 0 0; font-size: 13px;\">Universidad Popular del Cesar - Supervisi&oacute;n Acad&eacute;mica</p>"
                    + "</div>"
                    + "<div style=\"padding: 40px 35px; background-color: #FFFFFF;\">"
                    + "<p style=\"font-size: 15px; color: #2C3E50; margin-top: 0;\">Estimado/a estudiante <strong>" + nombreEstudiante + "</strong>,</p>"
                    + "<p style=\"font-size: 14px; color: #34495E; line-height: 1.6; text-align: justify;\">Le notificamos formalmente que su solicitud de apelaci&oacute;n y segunda revisi&oacute;n de novedades correspondiente al examen <strong>" + codigoExamen + "</strong> ha concluido su proceso de auditor&iacute;a por parte del cuerpo docente.</p>"
                    + "<p style=\"font-size: 14px; color: #34495E; line-height: 1.6; text-align: justify;\">" + parrafoContexto + "</p>"
                    + "<div style=\"margin: 30px 0; padding: 20px; border: 1px solid " + colorEstado + "; background-color: #F8F9F9; border-radius: 3px;\">"
                    + "<h3 style=\"margin-top: 0; color: " + colorEstado + "; font-size: 16px; text-transform: uppercase; letter-spacing: 1px;\">Veredicto Oficial: " + textoEstado + "</h3>"
                    + "<p style=\"margin-bottom: 5px; font-size: 13px; color: #7F8C8D; font-weight: 600; text-transform: uppercase;\">Argumento de la decisi&oacute;n:</p>"
                    + "<p style=\"margin: 0; font-size: 14px; color: #2C3E50; font-style: italic; line-height: 1.5;\">\"" + motivoProfesor + "\"</p>"
                    + "</div>"
                    + "<p style=\"font-size: 13px; color: #7F8C8D; line-height: 1.5; border-top: 1px solid #EEEEEE; padding-top: 20px; margin-top: 40px;\">De acuerdo con los estatutos de la universidad, esta decisi&oacute;n tiene car&aacute;cter definitivo. Si requiere asistencia adicional, por favor dir&iacute;jase a la decanatura correspondiente.</p>"
                    + "</div>"
                    + "<div style=\"background-color: #F2F4F4; padding: 15px 30px; text-align: left; font-size: 11px; color: #95A5A6; border-top: 1px solid #E5E7E9;\">"
                    + "Notificaci&oacute;n autogenerada. Por favor, no responda a este correo.<br>"
                    + "&copy; 2026 Universidad Popular del Cesar. Todos los derechos reservados."
                    + "</div>"
                    + "</div>";

            helper.setText(htmlTemplate, true);
            javaMailSender.send(mensaje);

        } catch (Exception e) {
            System.err.println("Error enviando el correo a: " + correoEstudiante);
            e.printStackTrace();
        }
    }
}