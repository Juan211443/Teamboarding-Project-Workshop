package com.meli_juan.workshop.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    private long id;
    private List<OrderItemResponseDto> items;
    private BigDecimal totalPrice;
    private String status;
    private Instant createdAt;
}
