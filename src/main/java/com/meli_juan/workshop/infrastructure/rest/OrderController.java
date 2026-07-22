package com.meli_juan.workshop.infrastructure.rest;

import com.meli_juan.workshop.application.dto.OrderRequestDto;
import com.meli_juan.workshop.application.dto.OrderResponseDto;
import com.meli_juan.workshop.application.dto.OrderStatusUpdateDto;
import com.meli_juan.workshop.application.mapper.OrderResponseMapper;
import com.meli_juan.workshop.domain.model.OrderItem;
import com.meli_juan.workshop.domain.model.OrderStatus;
import com.meli_juan.workshop.domain.port.OrderUseCasePort;
import com.meli_juan.workshop.infrastructure.rest.dto.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Orders", description = "Operations for purchase order management")
public class OrderController {

    private final OrderUseCasePort orderUseCasePort;
    private final OrderResponseMapper responseMapper;

    @Operation(summary = "List orders with pagination",
            description = "Returns a page of orders with configurable pagination")
    @ApiResponse(responseCode = "200", description = "Orders page retrieved successfully")
    @GetMapping
    public ResponseEntity<Page<OrderResponseDto>> getAll(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size
    ) {
        log.debug("GET /api/orders?page={}&size={}", page, size);
        return ResponseEntity.ok(orderUseCasePort.getAll(page, size).map(responseMapper::toResponse));
    }

    @Operation(summary = "Get order by ID")
    @ApiResponse(responseCode = "200", description = "Order found")
    @ApiResponse(responseCode = "404", description = "Order not found",
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getById(
            @Parameter(description = "Order ID") @PathVariable long id) {
        log.debug("GET /api/orders/{}", id);
        return ResponseEntity.ok(responseMapper.toResponse(orderUseCasePort.getById(id)));
    }

    @Operation(summary = "Create a new order",
            description = "Creates an order with the specified items. Total price is calculated automatically.")
    @ApiResponse(responseCode = "201", description = "Order created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @PostMapping
    public ResponseEntity<OrderResponseDto> create(@Valid @RequestBody OrderRequestDto request) {
        log.debug("POST /api/orders - request: {}", request);
        List<OrderItem> items = request.getItems().stream()
                .map(item -> {
                    var orderItem = new OrderItem();
                    orderItem.setProductId(item.getProductId());
                    orderItem.setQuantity(item.getQuantity());
                    return orderItem;
                })
                .toList();
        OrderResponseDto response = responseMapper.toResponse(orderUseCasePort.create(items));
        return ResponseEntity.created(URI.create("/api/orders/" + response.getId())).body(response);
    }

    @Operation(summary = "Update order status",
            description = "Allows changing the order status (PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED)")
    @ApiResponse(responseCode = "200", description = "Status updated successfully")
    @ApiResponse(responseCode = "404", description = "Order not found",
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDto> updateStatus(
            @Parameter(description = "Order ID") @PathVariable long id,
            @Valid @RequestBody OrderStatusUpdateDto statusUpdate
    ) {
        log.debug("PATCH /api/orders/{}/status - status: {}", id, statusUpdate.getStatus());
        OrderStatus status = OrderStatus.valueOf(statusUpdate.getStatus().toUpperCase());
        return ResponseEntity.ok(responseMapper.toResponse(orderUseCasePort.updateStatus(id, status)));
    }

    @Operation(summary = "Delete an order")
    @ApiResponse(responseCode = "204", description = "Order deleted successfully")
    @ApiResponse(responseCode = "404", description = "Order not found",
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Order ID") @PathVariable long id) {
        log.debug("DELETE /api/orders/{}", id);
        orderUseCasePort.delete(id);
        return ResponseEntity.noContent().build();
    }
}
