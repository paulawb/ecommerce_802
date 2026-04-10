package com.ecommerce.crud.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Producto {
    private String productId;
    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer stock;
}
