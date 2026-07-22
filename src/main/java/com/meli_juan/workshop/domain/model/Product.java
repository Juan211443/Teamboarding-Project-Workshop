package com.meli_juan.workshop.domain.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Positive(message = "The ID must not be negative or 0")
    private long id;
    @NotBlank
    private String name;
    @Min(value = 0, message = "The price must be positive")
    @NotNull(message = "The price must not be null")
    private BigDecimal price;
}