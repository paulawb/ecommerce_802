package com.ecommerce.crud.domain.exception;

public class ProductoConflictException extends RuntimeException {
    public ProductoConflictException(String message) {
        super(message);
    }
}
