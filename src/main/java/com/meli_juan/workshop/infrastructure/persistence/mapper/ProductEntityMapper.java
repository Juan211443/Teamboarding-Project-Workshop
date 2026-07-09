package com.meli_juan.workshop.infrastructure.persistence.mapper;

import com.meli_juan.workshop.domain.model.Product;
import com.meli_juan.workshop.infrastructure.persistence.entity.ProductEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductEntityMapper {
    ProductEntity toEntity(Product product);
    Product toDomain(ProductEntity entity);
}