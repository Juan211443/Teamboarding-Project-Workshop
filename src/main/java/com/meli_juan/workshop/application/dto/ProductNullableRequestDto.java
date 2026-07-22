package com.meli_juan.workshop.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Schema(description = "DTO para actualización parcial de producto (campos opcionales)")
public class ProductNullableRequestDto {

    @Size(min = 1, max = 100, message = "The name must be between 1 and 100 characters")
    @Schema(description = "Nombre del producto (opcional)", example = "Laptop HP", nullable = true)
    private String name;

    @DecimalMin(value = "0.01", message = "The price must be greater than 0")
    @Schema(description = "Precio del producto (opcional)", example = "999.99", nullable = true)
    private BigDecimal price;
}
