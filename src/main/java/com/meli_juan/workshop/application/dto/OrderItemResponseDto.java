package com.meli_juan.workshop.application.dto;

import java.math.BigDecimal;

public record OrderItemResponseDto(
    long id,
    long productId,
    int quantity,
    BigDecimal subtotal
){}
