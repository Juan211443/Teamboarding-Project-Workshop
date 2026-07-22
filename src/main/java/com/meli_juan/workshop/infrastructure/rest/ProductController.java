package com.meli_juan.workshop.infrastructure.rest;

import com.meli_juan.workshop.application.dto.ProductNullableRequestDto;
import com.meli_juan.workshop.application.dto.ProductRequestDto;
import com.meli_juan.workshop.application.dto.ProductResponseDto;
import com.meli_juan.workshop.application.mapper.ProductNullableMapper;
import com.meli_juan.workshop.application.mapper.ProductRequestMapper;
import com.meli_juan.workshop.application.mapper.ProductResponseMapper;
import com.meli_juan.workshop.domain.port.ProductUseCasePort;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import java.net.URI;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/products")
@Tag(name = "Products", description = "CRUD operations for product management")
public class ProductController {

    private final ProductUseCasePort productUseCasePort;
    private final ProductNullableMapper requestNullableMapper;
    private final ProductRequestMapper requestMapper;
    private final ProductResponseMapper responseMapper;

    @Operation(summary = "List products with pagination",
            description = "Returns a page of products with configurable pagination")
    @ApiResponse(responseCode = "200", description = "Products page retrieved successfully")
    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> getAll(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size
    ){
        log.debug("GET /api/products?page={}&size={}", page, size);
        return ResponseEntity.ok().body(productUseCasePort.getAll(page, size).map(responseMapper::toResponse));
    }

    @Operation(summary = "Get product by ID")
    @ApiResponse(responseCode = "200", description = "Product found")
    @ApiResponse(responseCode = "404", description = "Product not found",
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getById(
            @Parameter(description = "Product ID") @PathVariable long id){
        log.debug("GET /api/products/{}", id);
        return ResponseEntity.ok(responseMapper.toResponse(productUseCasePort.getById(id)));
    }

    @Operation(summary = "Create a new product")
    @ApiResponse(responseCode = "201", description = "Product created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @PostMapping
    public ResponseEntity<ProductResponseDto> create(@Valid @RequestBody ProductRequestDto request){
        log.debug("POST /api/products - request: {}", request);
        ProductResponseDto response = responseMapper.toResponse(productUseCasePort.create(requestMapper.toDomain(request)));
        return ResponseEntity.created(URI.create("/api/products/" + response.getId()))
                .body(response);
    }

    @Operation(summary = "Fully update a product")
    @ApiResponse(responseCode = "200", description = "Product updated successfully")
    @ApiResponse(responseCode = "404", description = "Product not found",
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> update(
            @Parameter(description = "Product ID") @PathVariable long id,
            @Valid @RequestBody ProductRequestDto product) {
        log.debug("PUT /api/products/{} - request: {}", id, product);
        return ResponseEntity.ok().body(responseMapper.toResponse(productUseCasePort.update(requestMapper.toDomain(product), id)));
    }

    @Operation(summary = "Partially update a product",
            description = "Only non-null fields are updated")
    @ApiResponse(responseCode = "200", description = "Product partially updated")
    @ApiResponse(responseCode = "404", description = "Product not found",
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponseDto> patch(
            @Parameter(description = "Product ID") @PathVariable long id,
            @Valid @RequestBody ProductNullableRequestDto product){
        log.debug("PATCH /api/products/{} - request: {}", id, product);
        return ResponseEntity.ok().body(responseMapper.toResponse(productUseCasePort.patch(requestNullableMapper.toNullableDomain(product), id)));
    }

    @Operation(summary = "Delete a product")
    @ApiResponse(responseCode = "204", description = "Product deleted successfully")
    @ApiResponse(responseCode = "404", description = "Product not found",
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Product ID") @PathVariable long id){
        log.debug("DELETE /api/products/{}", id);
        productUseCasePort.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search product by exact name")
    @ApiResponse(responseCode = "200", description = "Product found")
    @ApiResponse(responseCode = "404", description = "Product not found",
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @ApiResponse(responseCode = "409", description = "Multiple results found",
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @GetMapping("/searchByName")
    public ResponseEntity<ProductResponseDto> getByName(
            @Parameter(description = "Product name to search") @RequestParam String name){
        log.debug("GET /api/products/searchByName - request: {}", name);
        return ResponseEntity.ok().body(responseMapper.toResponse(productUseCasePort.getByName(name)));
    }
}
