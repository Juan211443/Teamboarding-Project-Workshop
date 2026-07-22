package com.meli_juan.workshop.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de respuesta de producto")
public class ProductResponseDto{

    @Schema(description = "ID único del producto", example = "1")
    private long id;

    @Schema(description = "Nombre del producto", example = "Laptop HP")
    private String name;

    @Schema(description = "Precio del producto", example = "999.99")
    private BigDecimal price;
}
