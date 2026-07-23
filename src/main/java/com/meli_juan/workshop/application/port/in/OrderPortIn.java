package com.meli_juan.workshop.application.port.in;

import com.meli_juan.workshop.application.domain.model.Order;
import com.meli_juan.workshop.application.domain.model.OrderItem;
import com.meli_juan.workshop.application.domain.model.OrderStatus;
import org.springframework.data.domain.Page;
import java.util.List;

public interface OrderPortIn {
    Page<Order> getAll(int page, int size);
    Order getById(long id);
    Order create(List<OrderItem> items);
    Order updateStatus(long id, OrderStatus status);
    void delete(long id);
}
