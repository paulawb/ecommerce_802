package com.ecommerce.crud.infraestructure.entry_points.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductoRequest {
    private Object productId;
    private String nombre;
    private String descripcion;
    private Object precio;
    private Object stock;
}
