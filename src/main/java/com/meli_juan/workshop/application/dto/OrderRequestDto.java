package com.meli_juan.workshop.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@Schema(description = "DTO for creating a new order")
public class OrderRequestDto {

    @NotEmpty(message = "The order must have at least one item")
    @Valid
    @Schema(description = "List of order items")
    private List<OrderItemRequestDto> items;
}
