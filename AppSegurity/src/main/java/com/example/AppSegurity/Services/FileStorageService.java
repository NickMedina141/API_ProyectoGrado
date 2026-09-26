package com.example.AppSegurity.Services;

import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.Base64;
import java.util.UUID;

@Service
public class FileStorageService {

    // Apuntamos a la carpeta de Documentos del usuario actual
    private final String BASE_DIR = System.getProperty("user.home") + "/Documents/DataSupervision/Examenes/";

    public String guardarEvidenciaBase64(String base64String, String sesionId, String prefijo, String nombreEstudiante, String materiaCodigo) {
        if (base64String == null || base64String.isEmpty()) {
            return null;
        }

        try {
            // Eliminar cabecera si existe (ej. data:image/webp;base64,...)
            if (base64String.contains(",")) {
                base64String = base64String.split(",")[1];
            }

            byte[] decodedBytes;
            // Si el string empieza con gAAAAA, es un token de Fernet AES-256 (texto plano seguro)
            if (base64String.startsWith("gAAAAA")) {
                decodedBytes = base64String.getBytes();
            } else {
                // Decodificar imagenes viejas no encriptadas
                decodedBytes = Base64.getDecoder().decode(base64String);
            }

            // Crear carpeta del estudiante y su subcarpeta de datos biometricos (para uso futuro)
            String estudianteDirPath = BASE_DIR + materiaCodigo + "/" + nombreEstudiante + "/";
            File dirBiometricos = new File(estudianteDirPath + "datos_biometricos");
            if (!dirBiometricos.exists()) {
                dirBiometricos.mkdirs();
            }

            // Crear jerarquia de carpetas para las evidencias: C:/.../DataSupervision/Examenes/Materia/Estudiante/sesionId/tipo/
            String pathJerarquia = materiaCodigo + "/" + nombreEstudiante + "/" + sesionId + "/" + prefijo + "/";
            File directorio = new File(BASE_DIR + pathJerarquia);
            
            if (!directorio.exists()) {
                directorio.mkdirs();
            }

            // Generar nombre unico con extension dinamica
            String extension = prefijo.equals("audio") ? ".wav" : ".webp";
            String fileName = prefijo + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;
            File archivo = new File(directorio, fileName);

            try (FileOutputStream fos = new FileOutputStream(archivo)) {
                fos.write(decodedBytes);
            }

            // Retornar la ruta relativa o absoluta para la BD
            return pathJerarquia + fileName;
            
        } catch (Exception e) {
            System.err.println("Error al guardar la evidencia: " + e.getMessage());
            return null;
        }
    }
}
