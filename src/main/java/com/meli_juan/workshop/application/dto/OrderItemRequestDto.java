package com.meli_juan.workshop.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemRequestDto(
    @NotNull(message = "The productId must not be null")
    @Positive(message = "The productId must be positive")
    Long productId,

    @NotNull(message = "The quantity must not be null")
    @Min(value = 1, message = "The quantity must be at least 1")
    Integer quantity
){}
