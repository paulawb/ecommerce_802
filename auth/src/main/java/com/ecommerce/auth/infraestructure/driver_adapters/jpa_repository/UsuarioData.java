package com.ecommerce.auth.infraestructure.driver_adapters.jpa_repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity //objeto
@AllArgsConstructor
@NoArgsConstructor
@Table(name="Usuarios") //en donde
@Data

public class UsuarioData {
    @Id
    private String cedula;
    private String nombres;

    @Column(length =30, nullable = false)
    private String correo;
    private String password;
    private Integer edad;

    @Column(length =10)
    private String telefono;
    private String rol;

}
