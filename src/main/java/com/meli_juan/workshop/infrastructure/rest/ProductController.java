package com.meli_juan.workshop.infrastructure.rest;

import com.meli_juan.workshop.application.dto.ProductNullableRequestDto;
import com.meli_juan.workshop.application.dto.ProductRequestDto;
import com.meli_juan.workshop.application.dto.ProductResponseDto;
import com.meli_juan.workshop.application.mapper.ProductNullableMapper;
import com.meli_juan.workshop.application.mapper.ProductRequestMapper;
import com.meli_juan.workshop.application.mapper.ProductResponseMapper;
import com.meli_juan.workshop.domain.model.Product;
import com.meli_juan.workshop.domain.port.ProductUseCasePort;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestController
@RequestMapping("api/products")
public class ProductController {

    private final ProductUseCasePort productUseCasePort;
    private final ProductNullableMapper requestNullableMapper;
    private final ProductRequestMapper requestMapper;
    private final ProductResponseMapper responseMapper;

    //TODO: Logs;
    //TODO: Tests;

    public ProductController(
            ProductUseCasePort productUseCasePort,
            ProductNullableMapper productNullableMapper,
            ProductRequestMapper requestMapper,
            ProductResponseMapper responseMapper){
        this.productUseCasePort = productUseCasePort;
        this.requestNullableMapper = productNullableMapper;
        this.requestMapper = requestMapper;
        this.responseMapper = responseMapper;
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Page<Product> products = productUseCasePort.getAll(page, size);
        return products.isEmpty()
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok().body(products.map(responseMapper::toResponse));
    }

    @GetMapping({"/{id}"})
    public ResponseEntity<ProductResponseDto> getById(@PathVariable Long id){
        ProductResponseDto response = responseMapper.toResponse(productUseCasePort.getById(id));
        return response == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<ProductResponseDto> create(@Valid @RequestBody ProductRequestDto request){
        Product product = requestMapper.toDomain(request);
        Product response = productUseCasePort.create(product);
        return response == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.created(URI.create("api/products/" + response.getId()))
                    .body(responseMapper.toResponse(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> update(@PathVariable Long id, @Valid @RequestBody ProductRequestDto product) {
        ProductResponseDto response = responseMapper.toResponse(productUseCasePort
                .update(requestMapper.toDomain(product), id));
        return response == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok().body(response);
    }

    @PatchMapping("/{id}")
    ResponseEntity<ProductResponseDto> patch(@PathVariable Long id, @Valid @RequestBody ProductNullableRequestDto product){
        ProductResponseDto response = responseMapper.toResponse(productUseCasePort.patch(requestNullableMapper.toNullableDomain(product), id));
        return response == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<String> delete(@PathVariable Long id){
        String response = productUseCasePort.delete(id);
        return response == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok().body(response);
    }

    @GetMapping("/searchByName")
    public ResponseEntity<ProductResponseDto> getByName(@RequestParam String name){
        ProductResponseDto response = responseMapper.toResponse(productUseCasePort.getByName(name));
        return response == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok().body(response);
    }
}