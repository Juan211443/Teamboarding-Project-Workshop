package com.meli_juan.workshop.infrastructure.persistence.repository;

import com.meli_juan.workshop.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {
}
