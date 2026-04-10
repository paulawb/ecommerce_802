package com.ecommerce.crud.application.config;

import com.ecommerce.crud.domain.model.gateway.ProductoGateway;
import com.ecommerce.crud.domain.usecase.ProductoUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public ProductoUseCase productoUseCase(ProductoGateway productoGateway) {
        return new ProductoUseCase(productoGateway);
    }
}
