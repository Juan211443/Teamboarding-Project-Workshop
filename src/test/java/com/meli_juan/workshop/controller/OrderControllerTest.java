package com.meli_juan.workshop.controller;

import com.meli_juan.workshop.application.dto.OrderResponseDto;
import com.meli_juan.workshop.application.dto.OrderItemResponseDto;
import com.meli_juan.workshop.application.mapper.OrderResponseMapper;
import com.meli_juan.workshop.application.domain.exception.OrderNotFoundException;
import com.meli_juan.workshop.application.domain.model.Order;
import com.meli_juan.workshop.application.domain.model.OrderItem;
import com.meli_juan.workshop.application.domain.model.OrderStatus;
import com.meli_juan.workshop.application.port.in.OrderPortIn;
import com.meli_juan.workshop.infrastructure.rest.OrderController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    OrderPortIn orderUseCasePort;

    @MockitoBean
    OrderResponseMapper orderResponseMapper;

    @Test
    void post_createOrder_validItems_returns201() throws Exception {
        Order order = new Order(1L,
                List.of(new OrderItem(1L, 1L, 2, BigDecimal.valueOf(2000.00))),
                BigDecimal.valueOf(2000.00), OrderStatus.PENDING, Instant.now());
        OrderResponseDto dto = new OrderResponseDto(1L,
                List.of(new OrderItemResponseDto(1L, 1L, 2, BigDecimal.valueOf(2000.00))),
                BigDecimal.valueOf(2000.00), "PENDING", Instant.now());

        when(orderUseCasePort.create(anyList())).thenReturn(order);
        when(orderResponseMapper.toResponse(order)).thenReturn(dto);

        mvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"items\":[{\"productId\":1,\"quantity\":2}]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.totalPrice").value(2000.00))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.items[0].productId").value(1))
                .andExpect(jsonPath("$.items[0].quantity").value(2));
    }

    @Test
    void post_createOrder_emptyItems_returns400() throws Exception {
        mvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"items\":[]}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void get_getById_orderExists_returns200() throws Exception {
        Order order = new Order(1L,
                List.of(new OrderItem(1L, 1L, 2, BigDecimal.valueOf(2000.00))),
                BigDecimal.valueOf(2000.00), OrderStatus.PENDING, Instant.now());
        OrderResponseDto dto = new OrderResponseDto(1L,
                List.of(new OrderItemResponseDto(1L, 1L, 2, BigDecimal.valueOf(2000.00))),
                BigDecimal.valueOf(2000.00), "PENDING", Instant.now());

        when(orderUseCasePort.getById(1L)).thenReturn(order);
        when(orderResponseMapper.toResponse(order)).thenReturn(dto);

        mvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.totalPrice").value(2000.00));
    }

    @Test
    void get_getById_orderNotExists_returns404() throws Exception {
        final long TEST_ID = 999L;
        when(orderUseCasePort.getById(TEST_ID)).thenThrow(new OrderNotFoundException(999L));

        mvc.perform(get("/api/orders/" + TEST_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void patch_updateStatus_validStatus_returns200() throws Exception {
        Order order = new Order(1L,
                List.of(new OrderItem(1L, 1L, 2, BigDecimal.valueOf(2000.00))),
                BigDecimal.valueOf(2000.00), OrderStatus.CONFIRMED, Instant.now());
        OrderResponseDto dto = new OrderResponseDto(1L,
                List.of(new OrderItemResponseDto(1L, 1L, 2, BigDecimal.valueOf(2000.00))),
                BigDecimal.valueOf(2000.00), "CONFIRMED", Instant.now());

        when(orderUseCasePort.updateStatus(1L, OrderStatus.CONFIRMED)).thenReturn(order);
        when(orderResponseMapper.toResponse(order)).thenReturn(dto);

        mvc.perform(patch("/api/orders/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"CONFIRMED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void patch_updateStatus_orderNotExists_returns404() throws Exception {
        long TEST_ID = 999L;
        when(orderUseCasePort.updateStatus(TEST_ID, OrderStatus.CONFIRMED))
                .thenThrow(new OrderNotFoundException(TEST_ID));

        mvc.perform(patch("/api/orders/" + TEST_ID + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"CONFIRMED\"}"))
                .andExpect(status().isNotFound());
    }
}
