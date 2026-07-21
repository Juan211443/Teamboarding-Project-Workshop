package com.meli_juan.workshop.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDto {
    private long id;
    private long productId;
    private int quantity;
    private BigDecimal subtotal;
}
