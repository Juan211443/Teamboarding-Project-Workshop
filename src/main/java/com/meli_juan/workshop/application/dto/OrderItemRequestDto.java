package com.meli_juan.workshop.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Item de una orden")
public class OrderItemRequestDto {

    @NotNull(message = "The productId must not be null")
    @Positive(message = "The productId must be positive")
    @Schema(description = "ID del producto", example = "1")
    private Long productId;

    @NotNull(message = "The quantity must not be null")
    @Min(value = 1, message = "The quantity must be at least 1")
    @Schema(description = "Cantidad del producto", example = "2", minimum = "1")
    private Integer quantity;
}
