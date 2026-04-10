package com.ecommerce.crud.infraestructure.driver_adapters.jpa_repository;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Productos")
public class ProductoData {
    @Id
    private String productId;
    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer stock;
}
