package com.meli_juan.workshop.infrastructure.rest;

import com.meli_juan.workshop.application.dto.OrderRequestDto;
import com.meli_juan.workshop.application.dto.OrderResponseDto;
import com.meli_juan.workshop.application.dto.OrderStatusUpdateRequestDto;
import com.meli_juan.workshop.application.mapper.OrderResponseMapper;
import com.meli_juan.workshop.application.domain.model.OrderItem;
import com.meli_juan.workshop.application.domain.model.OrderStatus;
import com.meli_juan.workshop.application.port.in.OrderPortIn;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/orders")
public class OrderController {

    private final OrderPortIn orderUseCasePort;
    private final OrderResponseMapper responseMapper;

    @GetMapping
    public ResponseEntity<Page<OrderResponseDto>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.debug("GET /api/orders?page={}&size={}", page, size);
        return ResponseEntity.ok(orderUseCasePort.getAll(page, size).map(responseMapper::toResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getById(@PathVariable long id) {
        log.debug("GET /api/orders/{}", id);
        return ResponseEntity.ok(responseMapper.toResponse(orderUseCasePort.getById(id)));
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> create(@Valid @RequestBody OrderRequestDto request) {
        log.debug("POST /api/orders - request: {}", request);
        List<OrderItem> items = request.items().stream()
                .map(item -> {
                    var orderItem = new OrderItem();
                    orderItem.setProductId(item.productId());
                    orderItem.setQuantity(item.quantity());
                    return orderItem;
                })
                .toList();
        OrderResponseDto response = responseMapper.toResponse(orderUseCasePort.create(items));
        return ResponseEntity.created(URI.create("/api/orders/" + response.id())).body(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDto> updateStatus(
            @PathVariable long id,
            @Valid @RequestBody OrderStatusUpdateRequestDto statusUpdate
    ) {
        log.debug("PATCH /api/orders/{}/status - status: {}", id, statusUpdate.status());
        OrderStatus status = OrderStatus.valueOf(statusUpdate.status().toUpperCase());
        return ResponseEntity.ok(responseMapper.toResponse(orderUseCasePort.updateStatus(id, status)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        log.debug("DELETE /api/orders/{}", id);
        orderUseCasePort.delete(id);
        return ResponseEntity.noContent().build();
    }
}
