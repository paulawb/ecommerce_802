package com.ecommerce.crud.infraestructure.mapper;

import com.ecommerce.crud.domain.model.Producto;
import com.ecommerce.crud.infraestructure.driver_adapters.jpa_repository.ProductoData;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {
    public Producto toProducto(ProductoData productoData) {
        return new Producto(
                productoData.getProductId(),
                productoData.getNombre(),
                productoData.getDescripcion(),
                productoData.getPrecio(),
                productoData.getStock()
        );
    }

    public ProductoData toProductoData(Producto producto) {
        return new ProductoData(
                producto.getProductId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStock()
        );
    }
}
