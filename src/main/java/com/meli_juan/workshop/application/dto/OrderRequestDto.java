package com.meli_juan.workshop.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderRequestDto {

    @NotEmpty(message = "The order must have at least one item")
    @Valid
    private List<OrderItemRequestDto> items;
}
