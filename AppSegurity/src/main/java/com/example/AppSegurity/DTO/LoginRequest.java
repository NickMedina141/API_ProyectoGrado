package com.example.AppSegurity.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    //Parametros de la clase
    private String email;
    private String password;

    //Constructor sin parametros
    public LoginRequest() {

    }

    //Constructor con parametros
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
