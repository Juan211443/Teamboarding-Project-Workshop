package com.meli_juan.workshop.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ProductNullableRequestDto {

    @Size(min = 1, max = 100, message = "The name must be between 1 and 100 characters")
    private String name;

    @DecimalMin(value = "0.01", message = "The price must be greater than 0")
    private BigDecimal price;
}
