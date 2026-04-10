package com.ecommerce.auth.domain.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {
    private String cedula;
    private String nombres;
    private String correo;
    private String password;
    private Integer edad;
    private String telefono;
    private String rol;

}
