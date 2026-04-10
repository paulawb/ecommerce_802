package com.ecommerce.crud.domain.model.gateway;

import com.ecommerce.crud.domain.model.Producto;

import java.util.List;

public interface ProductoGateway {
    Producto guardarProducto(Producto producto);

    Producto buscarProductoPorId(String productId);

    List<Producto> buscarProductosPorFiltro(Producto filtro);

    Producto actualizarProducto(Producto producto);

    void eliminarProductoPorId(String productId);

    List<Producto> listarProductos();
}
