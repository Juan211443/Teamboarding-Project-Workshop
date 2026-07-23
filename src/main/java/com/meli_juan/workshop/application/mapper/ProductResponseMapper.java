package com.meli_juan.workshop.application.mapper;

import com.meli_juan.workshop.application.dto.ProductResponseDto;
import com.meli_juan.workshop.application.domain.model.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductResponseMapper {
    ProductResponseDto toResponse(Product product);
}