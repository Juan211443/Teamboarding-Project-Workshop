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
@Schema(description = "DTO de respuesta de orden")
public class OrderResponseDto {

    @Schema(description = "ID único de la orden", example = "1")
    private long id;

    @Schema(description = "Items de la orden")
    private List<OrderItemResponseDto> items;

    @Schema(description = "Precio total de la orden", example = "1999.98")
    private BigDecimal totalPrice;

    @Schema(description = "Estado de la orden", example = "PENDING")
    private String status;

    @Schema(description = "Fecha de creación")
    private Instant createdAt;
}
