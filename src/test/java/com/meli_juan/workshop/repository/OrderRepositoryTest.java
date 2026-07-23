package com.meli_juan.workshop.repository;

import com.meli_juan.workshop.application.domain.model.OrderStatus;
import com.meli_juan.workshop.infrastructure.persistence.entity.OrderEntity;
import com.meli_juan.workshop.infrastructure.persistence.entity.OrderItemEntity;
import com.meli_juan.workshop.infrastructure.persistence.entity.ProductEntity;
import com.meli_juan.workshop.infrastructure.persistence.repository.OrderJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class OrderRepositoryTest {

    @Autowired
    private OrderJpaRepository orderRepository;

    @Autowired
    private TestEntityManager entityManager;

    private ProductEntity product;

    @BeforeEach
    void setUp() {
        ProductEntity p = new ProductEntity();
        p.setName("Laptop");
        p.setPrice(BigDecimal.valueOf(1000.00));
        product = entityManager.persist(p);
        entityManager.flush();
    }

    private OrderEntity createOrder() {
        OrderEntity order = new OrderEntity();
        order.setTotalPrice(BigDecimal.valueOf(2000.00));
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(Instant.now());
        order.setItems(new ArrayList<>());

        OrderItemEntity item = new OrderItemEntity();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(2);
        item.setSubtotal(BigDecimal.valueOf(2000.00));
        order.getItems().add(item);

        return order;
    }

    @Test
    void save_validOrder_returnsSavedOrder() {
        OrderEntity order = createOrder();
        OrderEntity saved = orderRepository.save(order);
        entityManager.flush();

        assertEquals(OrderStatus.PENDING, saved.getStatus());
        assertEquals(1, saved.getItems().size());
        assertEquals(0, BigDecimal.valueOf(2000.00).compareTo(saved.getTotalPrice()));
        assertEquals(product.getId(), saved.getItems().get(0).getProduct().getId());
    }

    @Test
    void save_updateStatus_returnsUpdatedOrder() {
        OrderEntity order = createOrder();
        OrderEntity saved = orderRepository.save(order);
        entityManager.flush();

        saved.setStatus(OrderStatus.CONFIRMED);
        OrderEntity updated = orderRepository.save(saved);
        entityManager.flush();

        assertEquals(OrderStatus.CONFIRMED, updated.getStatus());
        assertEquals(saved.getId(), updated.getId());
    }

    @Test
    void findById_nonExistingOrder_returnsEmpty() {
        Optional<OrderEntity> found = orderRepository.findById(99999L);
        assertTrue(found.isEmpty());
    }
}
