package com.example.AppSegurity.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecuperarConfirmarRequest {
    private String email;
    private String codigoOtp;
    private String nuevaPassword;

    public RecuperarConfirmarRequest() {}
    public RecuperarConfirmarRequest(String email, String codigoOtp, String nuevaPassword) {
        this.email = email;
        this.codigoOtp = codigoOtp;
        this.nuevaPassword = nuevaPassword;
    }
}
