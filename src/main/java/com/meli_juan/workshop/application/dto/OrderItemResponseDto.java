package com.meli_juan.workshop.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order item response DTO")
public class OrderItemResponseDto {

    @Schema(description = "Item ID", example = "1")
    private long id;

    @Schema(description = "Product ID", example = "1")
    private long productId;

    @Schema(description = "Quantity", example = "2")
    private int quantity;

    @Schema(description = "Item subtotal", example = "1999.98")
    private BigDecimal subtotal;
}
