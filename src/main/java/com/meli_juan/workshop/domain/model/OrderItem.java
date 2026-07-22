package com.meli_juan.workshop.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    private long id;
    private long productId;
    private int quantity;
    private BigDecimal subtotal;
}
