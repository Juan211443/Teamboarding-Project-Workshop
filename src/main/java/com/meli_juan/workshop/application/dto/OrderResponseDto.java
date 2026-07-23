package com.meli_juan.workshop.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponseDto(
    long id,
    List<OrderItemResponseDto> items,
    BigDecimal totalPrice,
    String status,
    Instant createdAt
){}
