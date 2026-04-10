package com.ecommerce.crud.domain.usecase;

import com.ecommerce.crud.domain.exception.ProductoConflictException;
import com.ecommerce.crud.domain.exception.ProductoNotFoundException;
import com.ecommerce.crud.domain.model.Producto;
import com.ecommerce.crud.domain.model.gateway.ProductoGateway;
import com.ecommerce.crud.infraestructure.entry_points.dto.FlexibleNumberParser;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ProductoUseCase {
    private final ProductoGateway productoGateway;

    public Producto guardarProducto(Producto producto) {
        Producto existente = productoGateway.buscarProductoPorId(producto.getProductId());
        if (existente.getProductId() != null) {
            throw new ProductoConflictException("Ya existe un producto con el productId " + producto.getProductId());
        }
        return productoGateway.guardarProducto(producto);
    }

    public Producto buscarProductoPorId(String productId) {
        return productoGateway.buscarProductoPorId(productId);
    }

    public List<Producto> buscarProductosPorFiltro(Producto filtro) {
        return productoGateway.listarProductos().stream()
                .filter(producto -> coincideConFiltrosEspecificos(producto, filtro))
                .toList();
    }

    public List<Producto> buscarProductosPorValor(String valor) {
        String valorNormalizado = FlexibleNumberParser.normalizeSearchToken(valor);
        return productoGateway.listarProductos().stream()
                .filter(producto -> coincideConValor(producto, valorNormalizado))
                .toList();
    }

    public Producto actualizarProducto(Producto producto) {
        Producto existente = productoGateway.buscarProductoPorId(producto.getProductId());
        if (existente.getProductId() == null) {
            throw new ProductoNotFoundException("No existe un producto con el productId " + producto.getProductId());
        }
        return productoGateway.actualizarProducto(producto);
    }

    public void eliminarProductoPorId(String productId) {
        productoGateway.eliminarProductoPorId(productId);
    }

    public List<Producto> listarProductos() {
        return productoGateway.listarProductos();
    }

    private boolean coincideConValor(Producto producto, String valor) {
        if (valor == null || valor.isEmpty()) {
            return false;
        }

        return contiene(producto.getProductId(), valor)
                || contiene(producto.getNombre(), valor)
                || contiene(producto.getDescripcion(), valor)
                || contieneNumero(producto.getPrecio(), valor)
                || contieneNumero(producto.getStock(), valor);
    }

    private boolean coincideConFiltrosEspecificos(Producto producto, Producto filtro) {
        return coincideTexto(producto.getProductId(), filtro.getProductId())
                && coincideTexto(producto.getNombre(), filtro.getNombre())
                && coincideTexto(producto.getDescripcion(), filtro.getDescripcion())
                && coincideNumeroExacto(producto.getPrecio(), filtro.getPrecio())
                && coincideNumeroExacto(producto.getStock(), filtro.getStock());
    }

    private boolean contiene(String texto, String valor) {
        return FlexibleNumberParser.normalizeSearchToken(texto).contains(valor);
    }

    private boolean contieneNumero(Number numero, String valor) {
        if (numero == null) {
            return false;
        }
        String numeroNormalizado = FlexibleNumberParser.normalizeNumericForContains(numero);
        String valorNormalizado = FlexibleNumberParser.normalizeSearchToken(valor).replaceAll("[^0-9]", "");
        return numeroNormalizado.contains(valor) || (!valorNormalizado.isEmpty() && numeroNormalizado.contains(valorNormalizado));
    }

    private boolean coincideTexto(String valor, String filtro) {
        if (filtro == null) {
            return true;
        }
        return FlexibleNumberParser.normalizeSearchToken(valor)
                .contains(FlexibleNumberParser.normalizeSearchToken(filtro));
    }

    private boolean coincideNumeroExacto(Number valor, Number filtro) {
        return filtro == null || (valor != null && Double.compare(valor.doubleValue(), filtro.doubleValue()) == 0);
    }
}
