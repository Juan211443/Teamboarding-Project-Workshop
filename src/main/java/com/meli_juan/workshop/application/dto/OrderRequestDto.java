package com.meli_juan.workshop.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record OrderRequestDto(
    @NotEmpty(message = "The order must have at least one item")
    @Valid
    List<OrderItemRequestDto> items
) {}