package com.meli_juan.workshop.application.mapper;

import com.meli_juan.workshop.application.dto.ProductNullableRequestDto;
import com.meli_juan.workshop.application.domain.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductNullableMapper {
    @Mapping(target = "id", ignore = true)
    Product toNullableDomain(ProductNullableRequestDto productRequestDto);
}