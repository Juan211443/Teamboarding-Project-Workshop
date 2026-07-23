package com.meli_juan.workshop.application.port.out;

import com.meli_juan.workshop.application.domain.model.Order;
import org.springframework.data.domain.Page;

public interface OrderRepository {
    Page<Order> findAll(int page, int size);
    Order save(Order order);
    Order find(long id);
    Order updateStatus(long id, String status);
    void delete(long id);
}
