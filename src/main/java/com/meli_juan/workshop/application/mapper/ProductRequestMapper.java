package com.meli_juan.workshop.application.mapper;

import com.meli_juan.workshop.application.dto.ProductRequestDto;
import com.meli_juan.workshop.domain.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductRequestMapper {
    @Mapping(target = "id", ignore = true)
    Product toDomain(ProductRequestDto productRequestDto);
}
