package com.meli_juan.workshop.infrastructure.persistence.mapper;

import com.meli_juan.workshop.domain.model.Order;
import com.meli_juan.workshop.domain.model.OrderItem;
import com.meli_juan.workshop.infrastructure.persistence.entity.OrderEntity;
import com.meli_juan.workshop.infrastructure.persistence.entity.OrderItemEntity;
import com.meli_juan.workshop.infrastructure.persistence.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Context;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderEntityMapper {

    @Mapping(target = "items", ignore = true)
    OrderEntity toEntity(Order order);

    @Mapping(target = "items", source = "items")
    Order toDomain(OrderEntity entity);

    @Mapping(target = "productId", source = "product.id")
    OrderItem toItemDomain(OrderItemEntity entity);

    default List<OrderItemEntity> toItemEntities(List<OrderItem> items, OrderEntity orderEntity) {
        if (items == null) return null;
        return items.stream().map(item -> {
            OrderItemEntity entity = new OrderItemEntity();
            entity.setOrder(orderEntity);
            entity.setQuantity(item.getQuantity());
            entity.setSubtotal(item.getSubtotal());
            ProductEntity product = new ProductEntity();
            product.setId(item.getProductId());
            entity.setProduct(product);
            return entity;
        }).toList();
    }
}
