package com.ecommerce.crud.infraestructure.entry_points;

import com.ecommerce.crud.domain.exception.ProductoConflictException;
import com.ecommerce.crud.domain.exception.ProductoNotFoundException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException e) {
        return new ResponseEntity<>(message(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductoConflictException.class)
    public ResponseEntity<Map<String, String>> handleConflict(ProductoConflictException e) {
        return new ResponseEntity<>(message(e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({ProductoNotFoundException.class, EmptyResultDataAccessException.class})
    public ResponseEntity<Map<String, String>> handleNotFound(Exception e) {
        return new ResponseEntity<>(message(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleNotReadable(HttpMessageNotReadableException e) {
        return new ResponseEntity<>(message("JSON invalido o tipo de dato no esperado."), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(Exception e) {
        return new ResponseEntity<>(message("Solicitud invalida: " + e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    private Map<String, String> message(String text) {
        Map<String, String> response = new HashMap<>();
        response.put("message", text);
        return response;
    }
}
