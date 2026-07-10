package com.meli_juan.workshop.infrastructure.rest;

import com.meli_juan.workshop.application.dto.ProductNullableRequestDto;
import com.meli_juan.workshop.application.dto.ProductRequestDto;
import com.meli_juan.workshop.application.dto.ProductResponseDto;
import com.meli_juan.workshop.application.mapper.ProductNullableMapper;
import com.meli_juan.workshop.application.mapper.ProductRequestMapper;
import com.meli_juan.workshop.application.mapper.ProductResponseMapper;
import com.meli_juan.workshop.domain.port.ProductUseCasePort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/products")
public class ProductController {

    private final ProductUseCasePort productUseCasePort;
    private final ProductNullableMapper requestNullableMapper;
    private final ProductRequestMapper requestMapper;
    private final ProductResponseMapper responseMapper;

    //TODO: Tests unitarios, integracion, repository;
    //TODO: Swagger;
    //TODO: Agregar entidades;
    //TODO: Levantar backend en docker;

    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        log.debug("GET /api/products?page={}&size={}", page, size);
        return ResponseEntity.ok().body(productUseCasePort.getAll(page, size).map(responseMapper::toResponse));
    }

    @GetMapping({"/{id}"})
    public ResponseEntity<ProductResponseDto> getById(@PathVariable Long id){
        log.debug("GET /api/products/{}", id);
        return ResponseEntity.ok(responseMapper.toResponse(productUseCasePort.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ProductResponseDto> create(@Valid @RequestBody ProductRequestDto request){
        log.debug("POST /api/products - request: {}", request);
        return ResponseEntity.ok(responseMapper.toResponse(productUseCasePort.create(requestMapper.toDomain(request))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> update(@PathVariable Long id, @Valid @RequestBody ProductRequestDto product) {
        log.debug("PUT /api/products/{} - request: {}", id, product);
        return ResponseEntity.ok().body(responseMapper.toResponse(productUseCasePort.update(requestMapper.toDomain(product), id)));
    }

    @PatchMapping("/{id}")
    ResponseEntity<ProductResponseDto> patch(@PathVariable Long id, @Valid @RequestBody ProductNullableRequestDto product){
        log.debug("PATCH /api/products/{} - request: {}", id, product);
        return ResponseEntity.ok().body(responseMapper.toResponse(productUseCasePort.patch(requestNullableMapper.toNullableDomain(product), id)));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id){
        log.debug("DELETE /api/products/{}", id);
        productUseCasePort.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/searchByName")
    public ResponseEntity<ProductResponseDto> getByName(@RequestParam String name){
        log.debug("GET /api/products/searchByName - request: {}", name);
        return ResponseEntity.ok().body(responseMapper.toResponse(productUseCasePort.getByName(name)));
    }
}