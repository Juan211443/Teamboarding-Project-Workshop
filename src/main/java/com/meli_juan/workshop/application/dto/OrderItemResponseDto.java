package com.meli_juan.workshop.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de respuesta de item de orden")
public class OrderItemResponseDto {

    @Schema(description = "ID del item", example = "1")
    private long id;

    @Schema(description = "ID del producto", example = "1")
    private long productId;

    @Schema(description = "Cantidad", example = "2")
    private int quantity;

    @Schema(description = "Subtotal del item", example = "1999.98")
    private BigDecimal subtotal;
}
