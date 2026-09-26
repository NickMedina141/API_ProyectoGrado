package com.example.AppSegurity.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESEncryptionUtil {

    //Llave secreta de 16 caracteres (128 bits). 
    private static final String LLAVE_AES = "ProyectoGrado123"; 

    public static String encriptarDatoSensible(String dato) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(LLAVE_AES.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(dato.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Error cifrando los datos de biometría");
        }
    }

    public static String desencriptarDatoSensible(String datoEncriptado) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(LLAVE_AES.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(datoEncriptado)));
        } catch (Exception e) {
            throw new RuntimeException("Error descifrando los datos");
        }
    }
}