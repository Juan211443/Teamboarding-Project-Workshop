package com.meli_juan.workshop.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Schema(description = "DTO for creating or updating a product")
public class ProductRequestDto {

    @NotBlank(message = "The name must not be blank")
    @Size(min = 1, max = 100, message = "The name must be between 1 and 100 characters")
    @Schema(description = "Product name", example = "Laptop HP", minLength = 1, maxLength = 100)
    private String name;

    @NotNull
    @DecimalMin(value = "0.01", message = "The price must be greater than 0")
    @Schema(description = "Product price", example = "999.99", minimum = "0.01")
    private BigDecimal price;
}
