package com.meli_juan.workshop.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Schema(description = "DTO for partial product update (optional fields)")
public class ProductNullableRequestDto {

    @Size(min = 1, max = 100, message = "The name must be between 1 and 100 characters")
    @Schema(description = "Product name (optional)", example = "Laptop HP", nullable = true)
    private String name;

    @DecimalMin(value = "0.01", message = "The price must be greater than 0")
    @Schema(description = "Product price (optional)", example = "999.99", nullable = true)
    private BigDecimal price;
}
