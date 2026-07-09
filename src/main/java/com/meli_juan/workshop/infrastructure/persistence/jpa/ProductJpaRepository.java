package com.meli_juan.workshop.infrastructure.persistence.jpa;

import com.meli_juan.workshop.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {
    ProductEntity findByName(String name);
}