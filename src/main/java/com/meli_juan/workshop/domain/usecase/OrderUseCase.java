package com.meli_juan.workshop.domain.usecase;

import com.meli_juan.workshop.domain.model.Order;
import com.meli_juan.workshop.domain.model.OrderItem;
import com.meli_juan.workshop.domain.model.OrderStatus;
import com.meli_juan.workshop.domain.model.Product;
import com.meli_juan.workshop.domain.port.OrderRepository;
import com.meli_juan.workshop.domain.port.OrderUseCasePort;
import com.meli_juan.workshop.domain.port.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderUseCase implements OrderUseCasePort {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Override
    public Page<Order> getAll(int page, int size) {
        log.debug("Viewing orders - page={}, size={}", page, size);
        return orderRepository.findAll(page, size);
    }

    @Override
    public Order getById(long id) {
        log.debug("Viewing order with id={}", id);
        return orderRepository.find(id);
    }

    @Override
    @Transactional
    public Order create(List<OrderItem> items) {
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderItem item : items) {
            Product product = productRepository.find(item.getProductId());
            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            item.setSubtotal(subtotal);
            totalPrice = totalPrice.add(subtotal);
        }

        Order order = new Order();
        order.setItems(items);
        order.setTotalPrice(totalPrice);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(Instant.now());

        Order saved = orderRepository.save(order);
        log.info("Order created: id={}, items={}, totalPrice={}", saved.getId(), items.size(), totalPrice);
        return saved;
    }

    @Override
    @Transactional
    public Order updateStatus(long id, OrderStatus status) {
        Order order = orderRepository.updateStatus(id, status.name());
        log.info("Order status updated: id={}, status={}", id, status);
        return order;
    }

    @Override
    @Transactional
    public void delete(long id) {
        orderRepository.delete(id);
        log.info("Order deleted: id={}", id);
    }
}

