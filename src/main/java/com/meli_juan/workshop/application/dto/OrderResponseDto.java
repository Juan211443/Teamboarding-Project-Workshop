package com.meli_juan.workshop.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order response DTO")
public class OrderResponseDto {

    @Schema(description = "Unique order ID", example = "1")
    private long id;

    @Schema(description = "Order items")
    private List<OrderItemResponseDto> items;

    @Schema(description = "Total order price", example = "1999.98")
    private BigDecimal totalPrice;

    @Schema(description = "Order status", example = "PENDING")
    private String status;

    @Schema(description = "Creation date")
    private Instant createdAt;
}
