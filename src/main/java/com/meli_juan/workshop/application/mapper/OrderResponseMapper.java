package com.meli_juan.workshop.application.mapper;

import com.meli_juan.workshop.application.dto.OrderItemResponseDto;
import com.meli_juan.workshop.application.dto.OrderResponseDto;
import com.meli_juan.workshop.domain.model.Order;
import com.meli_juan.workshop.domain.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderResponseMapper {
    @Mapping(target = "status", expression = "java(order.getStatus().name())")
    OrderResponseDto toResponse(Order order);

    OrderItemResponseDto toItemResponse(OrderItem item);
}
