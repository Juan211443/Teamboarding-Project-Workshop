package com.meli_juan.workshop.usecase;

import com.meli_juan.workshop.domain.exception.OrderNotFoundException;
import com.meli_juan.workshop.domain.exception.OrderNotFoundException;
import com.meli_juan.workshop.domain.exception.ProductNotFoundException;
import com.meli_juan.workshop.domain.model.Order;
import com.meli_juan.workshop.domain.model.OrderItem;
import com.meli_juan.workshop.domain.model.OrderStatus;
import com.meli_juan.workshop.domain.model.Product;
import com.meli_juan.workshop.domain.port.OrderRepository;
import com.meli_juan.workshop.domain.port.ProductRepository;
import com.meli_juan.workshop.domain.usecase.OrderUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderUseCase useCase;

    @Test
    void create_validItems_calculatesTotalAndReturnsOrder() {
        Product laptop = new Product(1L, "Laptop", BigDecimal.valueOf(1000.00));
        Product mouse = new Product(2L, "Mouse", BigDecimal.valueOf(25.00));

        when(productRepository.find(1L)).thenReturn(laptop);
        when(productRepository.find(2L)).thenReturn(mouse);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });

        OrderItem item1 = new OrderItem();
        item1.setProductId(1L);
        item1.setQuantity(2);
        OrderItem item2 = new OrderItem();
        item2.setProductId(2L);
        item2.setQuantity(3);

        Order result = useCase.create(List.of(item1, item2));

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertEquals(0, BigDecimal.valueOf(2075.00).compareTo(result.getTotalPrice()));
        assertEquals(BigDecimal.valueOf(2000.00), item1.getSubtotal());
        assertEquals(BigDecimal.valueOf(75.00), item2.getSubtotal());
        verify(productRepository).find(1L);
        verify(productRepository).find(2L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void updateStatus_existingOrder_returnsUpdatedOrder() {
        Order order = new Order(1L, List.of(), BigDecimal.valueOf(2000.00), OrderStatus.CONFIRMED, Instant.now());
        when(orderRepository.updateStatus(1L, "CONFIRMED")).thenReturn(order);

        Order result = useCase.updateStatus(1L, OrderStatus.CONFIRMED);

        assertNotNull(result);
        assertEquals(OrderStatus.CONFIRMED, result.getStatus());
        verify(orderRepository).updateStatus(1L, "CONFIRMED");
    }

    @Test
    void updateStatus_nonExistingOrder_throwsOrderNotFoundException() {
        when(orderRepository.updateStatus(999L, "CONFIRMED"))
                .thenThrow(new OrderNotFoundException(999L));

        assertThrows(OrderNotFoundException.class,
                () -> useCase.updateStatus(999L, OrderStatus.CONFIRMED));
        verify(orderRepository).updateStatus(999L, "CONFIRMED");
    }

    @Test
    void create_nonExistentProduct_throwsProductNotFoundException() {
        when(productRepository.find(999L)).thenThrow(new ProductNotFoundException(999L));

        OrderItem item = new OrderItem();
        item.setProductId(999L);
        item.setQuantity(1);

        assertThrows(ProductNotFoundException.class, () -> useCase.create(List.of(item)));
        verify(productRepository).find(999L);
    }
}
