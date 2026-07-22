package com.meli_juan.workshop.infrastructure.persistence.adapter;

import com.meli_juan.workshop.domain.exception.OrderNotFoundException;
import com.meli_juan.workshop.domain.model.Order;
import com.meli_juan.workshop.domain.model.OrderStatus;
import com.meli_juan.workshop.domain.port.OrderRepository;
import com.meli_juan.workshop.infrastructure.persistence.entity.OrderEntity;
import com.meli_juan.workshop.infrastructure.persistence.mapper.OrderEntityMapper;
import com.meli_juan.workshop.infrastructure.persistence.repository.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderJpaRepository jpaRepository;
    private final OrderEntityMapper entityMapper;

    @Override
    public Page<Order> findAll(int page, int size) {
        log.debug("Fetching orders - page={}, size={}", page, size);
        return jpaRepository.findAll(PageRequest.of(page, size)).map(entityMapper::toDomain);
    }

    @Override
    public Order save(Order order) {
        log.debug("Saving order: {}", order);
        OrderEntity entity = entityMapper.toEntity(order);
        entity.setItems(entityMapper.toItemEntities(order.getItems(), entity));
        return entityMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Order find(long id) {
        log.debug("Finding order by id={}", id);
        return jpaRepository.findById(id)
                .map(entityMapper::toDomain)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Override
    public Order updateStatus(long id, String status) {
        log.debug("Updating order status id={}, status={}", id, status);
        OrderEntity entity = jpaRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        entity.setStatus(OrderStatus.valueOf(status));
        return entityMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public void delete(long id) {
        if (!jpaRepository.existsById(id)) {
            throw new OrderNotFoundException(id);
        }
        jpaRepository.deleteById(id);
    }
}
