package com.meli_juan.workshop.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private long id;
    private List<OrderItem> items;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private Instant createdAt;
}
