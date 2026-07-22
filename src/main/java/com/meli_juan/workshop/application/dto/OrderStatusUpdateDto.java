package com.meli_juan.workshop.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "DTO para actualizar el estado de una orden")
public class OrderStatusUpdateDto {

    @NotNull(message = "The status must not be null")
    @Schema(description = "Nuevo estado de la orden", example = "CONFIRMED",
            allowableValues = {"PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"})
    private String status;
}
