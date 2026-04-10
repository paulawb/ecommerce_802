package com.ecommerce.crud.infraestructure.entry_points;

import com.ecommerce.crud.domain.exception.ProductoNotFoundException;
import com.ecommerce.crud.domain.model.Producto;
import com.ecommerce.crud.domain.usecase.ProductoUseCase;
import com.ecommerce.crud.infraestructure.entry_points.dto.FlexibleNumberParser;
import com.ecommerce.crud.infraestructure.entry_points.dto.ProductoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ecommerce/productos")
@RequiredArgsConstructor
public class ProductoController {
    private final ProductoUseCase productoUseCase;

    @PostMapping("/guardar")
    public ResponseEntity<?> guardarProducto(@RequestBody ProductoRequest request) {
        Producto producto = construirProducto(request, null, true);
        return new ResponseEntity<>(productoUseCase.guardarProducto(producto), HttpStatus.OK);
    }

    @GetMapping("/buscar/{productId}")
    public ResponseEntity<?> buscarProducto(@PathVariable String productId) {
        if (isBlank(productId)) {
            throw new IllegalArgumentException("productId es obligatorio.");
        }
        Producto producto = productoUseCase.buscarProductoPorId(normalizeStoredText(productId));
        if (producto.getProductId() == null) {
            throw new ProductoNotFoundException("Producto no encontrado.");
        }
        return new ResponseEntity<>(producto, HttpStatus.OK);
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscarProductosPorFiltro(
            @RequestParam(required = false) String valor,
            @RequestParam(required = false) String productId,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) Object precio,
            @RequestParam(required = false) Object stock
    ) {
        String valorGeneral = normalizeSearchText(valor);
        Producto filtro = new Producto(
                normalizeSearchText(productId),
                normalizeSearchText(nombre),
                normalizeSearchText(descripcion),
                parsePrecioOpcional(precio),
                parseStockOpcional(stock)
        );

        if (valorGeneral == null
                && filtro.getProductId() == null
                && filtro.getNombre() == null
                && filtro.getDescripcion() == null
                && filtro.getPrecio() == null
                && filtro.getStock() == null) {
            throw new IllegalArgumentException("Debes enviar al menos un parametro de busqueda.");
        }

        List<Producto> productos = valorGeneral != null
                ? productoUseCase.buscarProductosPorValor(valorGeneral)
                : productoUseCase.buscarProductosPorFiltro(filtro);

        if (valorGeneral != null
                && (filtro.getProductId() != null
                || filtro.getNombre() != null
                || filtro.getDescripcion() != null
                || filtro.getPrecio() != null
                || filtro.getStock() != null)) {
            productos = productoUseCase.buscarProductosPorFiltro(filtro).stream()
                    .filter(productos::contains)
                    .toList();
        }

        if (productos.isEmpty()) {
            throw new ProductoNotFoundException("Producto no encontrado.");
        }
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }

    @GetMapping("/listar")
    public ResponseEntity<?> listarProductos() {
        return new ResponseEntity<>(productoUseCase.listarProductos(), HttpStatus.OK);
    }

    @PutMapping("/actualizar/{productId}")
    public ResponseEntity<?> actualizarProducto(@PathVariable String productId, @RequestBody ProductoRequest request) {
        if (isBlank(productId)) {
            throw new IllegalArgumentException("productId es obligatorio.");
        }
        String productIdNormalizado = normalizeStoredText(productId);
        Producto existente = productoUseCase.buscarProductoPorId(productIdNormalizado);
        if (existente.getProductId() == null) {
            throw new ProductoNotFoundException("Producto no encontrado.");
        }

        Producto actualizado = construirProducto(request, productIdNormalizado, false);
        if (!isBlank(actualizado.getNombre())) {
            existente.setNombre(actualizado.getNombre());
        }
        if (!isBlank(actualizado.getDescripcion())) {
            existente.setDescripcion(actualizado.getDescripcion());
        }
        if (actualizado.getPrecio() != null) {
            existente.setPrecio(actualizado.getPrecio());
        }
        if (actualizado.getStock() != null) {
            existente.setStock(actualizado.getStock());
        }
        return new ResponseEntity<>(productoUseCase.actualizarProducto(existente), HttpStatus.OK);
    }

    @DeleteMapping("/eliminar/{productId}")
    public ResponseEntity<?> eliminarProducto(@PathVariable String productId) {
        if (isBlank(productId)) {
            throw new IllegalArgumentException("productId es obligatorio.");
        }
        String productIdNormalizado = normalizeStoredText(productId);
        Producto producto = productoUseCase.buscarProductoPorId(productIdNormalizado);
        if (producto.getProductId() == null) {
            throw new ProductoNotFoundException("Producto no encontrado.");
        }
        productoUseCase.eliminarProductoPorId(productIdNormalizado);
        return new ResponseEntity<>(Map.of("message", "Producto eliminado correctamente."), HttpStatus.OK);
    }

    private Producto construirProducto(ProductoRequest request, String productIdPath, boolean esCreacion) {
        if (request == null) {
            throw new IllegalArgumentException("El cuerpo de la solicitud no puede estar vacio.");
        }

        String productId = productIdPath;
        if (esCreacion) {
            productId = toRequiredText(request.getProductId(), "productId");
        }

        String nombre = esCreacion ? toRequiredText(request.getNombre(), "nombre") : normalizeStoredText(request.getNombre());
        String descripcion = esCreacion ? toRequiredText(request.getDescripcion(), "descripcion") : normalizeStoredText(request.getDescripcion());
        Double precio = esCreacion ? parsePrecio(request.getPrecio()) : parsePrecioOpcional(request.getPrecio());
        Integer stock = esCreacion ? parseStock(request.getStock()) : parseStockOpcional(request.getStock());

        return new Producto(productId, nombre, descripcion, precio, stock);
    }

    private String toRequiredText(Object valor, String campo) {
        String texto = normalizeStoredText(valor);
        if (isBlank(texto)) {
            throw new IllegalArgumentException("El campo " + campo + " es obligatorio.");
        }
        return texto;
    }

    private String normalizeStoredText(Object valor) {
        if (valor == null) {
            return null;
        }
        String texto = String.valueOf(valor).trim().replaceAll("\\s+", " ");
        return texto.isEmpty() ? null : texto;
    }

    private String normalizeSearchText(String valor) {
        String texto = FlexibleNumberParser.normalizeSearchToken(valor);
        return texto.isEmpty() ? null : texto;
    }

    private Double parsePrecio(Object valor) {
        Double precio = parsePrecioOpcional(valor);
        if (precio == null) {
            throw new IllegalArgumentException("El campo precio es obligatorio.");
        }
        return precio;
    }

    private Double parsePrecioOpcional(Object valor) {
        return FlexibleNumberParser.parseFlexibleDouble(valor, "precio");
    }

    private Integer parseStock(Object valor) {
        Integer stock = parseStockOpcional(valor);
        if (stock == null) {
            throw new IllegalArgumentException("El campo stock es obligatorio.");
        }
        return stock;
    }

    private Integer parseStockOpcional(Object valor) {
        return FlexibleNumberParser.parseFlexibleInteger(valor, "stock");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
