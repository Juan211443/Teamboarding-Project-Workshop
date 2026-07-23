package com.meli_juan.workshop.application.dto;

import jakarta.validation.constraints.NotNull;

public record OrderStatusUpdateRequestDto(
    @NotNull(message = "The status must not be null")
    String status
){}
