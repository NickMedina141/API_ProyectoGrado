package com.example.AppSegurity.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecuperarSolicitudRequest {
    private String email;

    public RecuperarSolicitudRequest() {}
    public RecuperarSolicitudRequest(String email) {
        this.email = email;
    }
}
