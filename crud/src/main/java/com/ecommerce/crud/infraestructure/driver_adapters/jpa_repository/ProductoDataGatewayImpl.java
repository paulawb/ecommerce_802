package com.ecommerce.crud.infraestructure.driver_adapters.jpa_repository;

import com.ecommerce.crud.domain.model.Producto;
import com.ecommerce.crud.domain.model.gateway.ProductoGateway;
import com.ecommerce.crud.infraestructure.mapper.ProductoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductoDataGatewayImpl implements ProductoGateway {

    private final ProductoDataJpaRepository repository;
    private final ProductoMapper productoMapper;

    @Override
    public Producto guardarProducto(Producto producto) {
        ProductoData productoData = productoMapper.toProductoData(producto);
        return productoMapper.toProducto(repository.save(productoData));
    }

    @Override
    public Producto buscarProductoPorId(String productId) {
        return repository.findById(productId)
                .map(productoMapper::toProducto)
                .orElse(new Producto());
    }

    @Override
    public List<Producto> buscarProductosPorFiltro(Producto filtro) {
        ProductoData filtroData = productoMapper.toProductoData(filtro);
        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withIgnoreNullValues()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        return repository.findAll(Example.of(filtroData, matcher)).stream()
                .map(productoMapper::toProducto)
                .toList();
    }

    @Override
    public Producto actualizarProducto(Producto producto) {
        ProductoData productoData = productoMapper.toProductoData(producto);
        return productoMapper.toProducto(repository.save(productoData));
    }

    @Override
    public void eliminarProductoPorId(String productId) {
        repository.deleteById(productId);
    }

    @Override
    public List<Producto> listarProductos() {
        return repository.findAll().stream()
                .map(productoMapper::toProducto)
                .toList();
    }
}
