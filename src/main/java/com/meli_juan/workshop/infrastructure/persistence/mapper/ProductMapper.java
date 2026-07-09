package com.meli_juan.workshop.infrastructure.persistence.mapper;

import com.meli_juan.workshop.domain.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface ProductMapper {
    Product updateFormSource(Product source, @MappingTarget Product target);
}