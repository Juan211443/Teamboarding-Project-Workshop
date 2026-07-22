package com.meli_juan.workshop.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Product response DTO")
public class ProductResponseDto{

    @Schema(description = "Unique product ID", example = "1")
    private long id;

    @Schema(description = "Product name", example = "Laptop HP")
    private String name;

    @Schema(description = "Product price", example = "999.99")
    private BigDecimal price;
}
