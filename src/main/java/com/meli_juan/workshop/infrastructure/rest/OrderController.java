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
@Tag(name = "Orders", description = "Operaciones para gestión de órdenes de compra")
public class OrderController {

    private final OrderUseCasePort orderUseCasePort;
    private final OrderResponseMapper responseMapper;

    @Operation(summary = "Listar órdenes paginadas",
            description = "Retorna una página de órdenes con paginación configurable")
    @ApiResponse(responseCode = "200", description = "Página de órdenes obtenida exitosamente")
    @GetMapping
    public ResponseEntity<Page<OrderResponseDto>> getAll(
            @Parameter(description = "Número de página (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size
    ) {
        log.debug("GET /api/orders?page={}&size={}", page, size);
        return ResponseEntity.ok(orderUseCasePort.getAll(page, size).map(responseMapper::toResponse));
    }

    @Operation(summary = "Obtener orden por ID")
    @ApiResponse(responseCode = "200", description = "Orden encontrada")
    @ApiResponse(responseCode = "404", description = "Orden no encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getById(
            @Parameter(description = "ID de la orden") @PathVariable long id) {
        log.debug("GET /api/orders/{}", id);
        return ResponseEntity.ok(responseMapper.toResponse(orderUseCasePort.getById(id)));
    }

    @Operation(summary = "Crear una nueva orden",
            description = "Crea una orden con los items especificados. Calcula el precio total automáticamente.")
    @ApiResponse(responseCode = "201", description = "Orden creada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
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

    @Operation(summary = "Actualizar estado de una orden",
            description = "Permite cambiar el estado de la orden (PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED)")
    @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente")
    @ApiResponse(responseCode = "404", description = "Orden no encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDto> updateStatus(
            @Parameter(description = "ID de la orden") @PathVariable long id,
            @Valid @RequestBody OrderStatusUpdateDto statusUpdate
    ) {
        log.debug("PATCH /api/orders/{}/status - status: {}", id, statusUpdate.getStatus());
        OrderStatus status = OrderStatus.valueOf(statusUpdate.getStatus().toUpperCase());
        return ResponseEntity.ok(responseMapper.toResponse(orderUseCasePort.updateStatus(id, status)));
    }

    @Operation(summary = "Eliminar una orden")
    @ApiResponse(responseCode = "204", description = "Orden eliminada exitosamente")
    @ApiResponse(responseCode = "404", description = "Orden no encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la orden") @PathVariable long id) {
        log.debug("DELETE /api/orders/{}", id);
        orderUseCasePort.delete(id);
        return ResponseEntity.noContent().build();
    }
}
