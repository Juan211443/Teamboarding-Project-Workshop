package com.meli_juan.workshop.domain.port;

import com.meli_juan.workshop.domain.model.Order;
import com.meli_juan.workshop.domain.model.OrderItem;
import com.meli_juan.workshop.domain.model.OrderStatus;
import org.springframework.data.domain.Page;
import java.util.List;

public interface OrderUseCasePort {
    Page<Order> getAll(int page, int size);
    Order getById(long id);
    Order create(List<OrderItem> items);
    Order updateStatus(long id, OrderStatus status);
    void delete(long id);
}
