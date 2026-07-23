package com.meli_juan.workshop.application.mapper;

import com.meli_juan.workshop.application.dto.OrderResponseDto;
import com.meli_juan.workshop.application.domain.model.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderResponseMapper {
    OrderResponseDto toResponse(Order order);
}
