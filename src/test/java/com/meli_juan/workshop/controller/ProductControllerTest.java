package com.meli_juan.workshop.controller;

import com.meli_juan.workshop.application.dto.ProductNullableRequestDto;
import com.meli_juan.workshop.application.dto.ProductRequestDto;
import com.meli_juan.workshop.application.dto.ProductResponseDto;
import com.meli_juan.workshop.application.mapper.ProductNullableMapper;
import com.meli_juan.workshop.application.mapper.ProductRequestMapper;
import com.meli_juan.workshop.application.mapper.ProductResponseMapper;
import com.meli_juan.workshop.domain.exception.ProductNotFoundException;
import com.meli_juan.workshop.domain.model.Product;
import com.meli_juan.workshop.domain.port.ProductUseCasePort;
import com.meli_juan.workshop.infrastructure.rest.ProductController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.math.BigDecimal;
import java.util.List;
import static org.mockito.Mockito.when;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    ProductNullableMapper productNullableMapper;

    @MockitoBean
    ProductResponseMapper productResponseMapper;

    @MockitoBean
    ProductRequestMapper productRequestMapper;


    @MockitoBean
    ProductUseCasePort productUseCasePort;

    @Test
    void Get_getAll_oneProduct_returns200() throws Exception {
        Product product = new Product(1L, "Test Product", new BigDecimal("10.00"));
        Page<Product> productPage = new PageImpl<>(List.of(product));
        ProductResponseDto dto = new ProductResponseDto(1L, "Test Product", new BigDecimal("10.00"));

        when(productUseCasePort.getAll(0, 10)).thenReturn(productPage);
        when(productResponseMapper.toResponse(product)).thenReturn(dto);

        mvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Test Product"))
                .andExpect(jsonPath("$.content[0].price").value(10.00));
    }

    @Test
    void Get_getAll_pageWithThreeProducts_return200() throws Exception {
        Product productA = new Product(1L, "Product A", new BigDecimal("10.00"));
        Product productB = new Product(2L, "Product B", new BigDecimal("20.00"));
        Product productC = new Product(3L, "Product C", new BigDecimal("30.00"));
        Page<Product> productPage = new PageImpl<>(List.of(productA, productB, productC));

        ProductResponseDto dto1 = new ProductResponseDto(1L, "Product A", new BigDecimal("10.00"));
        ProductResponseDto dto2 = new ProductResponseDto(2L, "Product B", new BigDecimal("20.00"));
        ProductResponseDto dto3 = new ProductResponseDto(3L, "Product C", new BigDecimal("30.00"));

        when(productUseCasePort.getAll(0, 10)).thenReturn(productPage);
        when(productResponseMapper.toResponse(productA)).thenReturn(dto1);
        when(productResponseMapper.toResponse(productB)).thenReturn(dto2);
        when(productResponseMapper.toResponse(productC)).thenReturn(dto3);

        mvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[2].id").value(3));
    }

    @Test
    void Get_id_getById_productExist_return200() throws Exception{
        Product product = new Product(1L, "Test Product", new BigDecimal("10.00"));
        ProductResponseDto dto = new ProductResponseDto(1L, "Test Product", new BigDecimal("10.00"));

        when(productUseCasePort.getById(1L)).thenReturn(product);
        when(productResponseMapper.toResponse(product)).thenReturn(dto);

        mvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(10.00));
    }

    @Test
    void Get_id_getById_productNotExist_return404() throws Exception {
        when(productUseCasePort.getById(1L)).thenThrow(new ProductNotFoundException(1L));

        mvc.perform(get("/api/products/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void Get_searchByName_productExist_return200() throws Exception {
        Product product = new Product(1L, "Test Product", new BigDecimal("10.00"));
        ProductResponseDto dto = new ProductResponseDto(1L, "Test Product", new BigDecimal("10.00"));

        when(productUseCasePort.getByName("Test Product")).thenReturn(product);
        when(productResponseMapper.toResponse(product)).thenReturn(dto);

        mvc.perform(get("/api/products/searchByName").param("name", "Test Product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void Get_searchByName_productNotExist_return404() throws Exception {
        when(productUseCasePort.getByName("Test Product")).thenThrow(new ProductNotFoundException(1L));

        mvc.perform(get("/api/products/searchByName").param("name", "Test Product"))
                .andExpect(status().isNotFound());
    }

    @Test
    void Post_create_twoProducts_return201() throws Exception {
        ProductRequestDto requestA = new ProductRequestDto();
        requestA.setName("Product A");
        requestA.setPrice(BigDecimal.valueOf(10.00));
        ProductRequestDto requestB = new ProductRequestDto();
        requestB.setName("Product B");
        requestB.setPrice(BigDecimal.valueOf(20.00));

        Product productA = new Product(1L, "Product A", new BigDecimal("10.00"));
        Product productB = new Product(2L, "Product B", new BigDecimal("20.00"));

        ProductResponseDto dtoA = new ProductResponseDto(1L, "Product A", new BigDecimal("10.00"));
        ProductResponseDto dtoB = new ProductResponseDto(2L, "Product B", new BigDecimal("20.00"));

        when(productRequestMapper.toDomain(requestA)).thenReturn(productA);
        when(productRequestMapper.toDomain(requestB)).thenReturn(productB);
        when(productUseCasePort.create(productA)).thenReturn(productA);
        when(productUseCasePort.create(productB)).thenReturn(productB);
        when(productResponseMapper.toResponse(productA)).thenReturn(dtoA);
        when(productResponseMapper.toResponse(productB)).thenReturn(dtoB);

        mvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestA)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Product A"));

        mvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestB)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Product B"));
    }

    @Test
    void Post_create_badRequest_return400() throws Exception{
        ProductRequestDto request = new ProductRequestDto();
        request.setName("");
        request.setPrice(BigDecimal.valueOf(-10.00));

        mvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void Post_create_nullName_return400() throws Exception{
        ProductRequestDto request = new ProductRequestDto();
        request.setName(null);
        request.setPrice(BigDecimal.valueOf(10.00));

        mvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void Put_update_productExist_return200() throws Exception {
        ProductRequestDto request = new ProductRequestDto();
        request.setName("Updated Product");
        request.setPrice(BigDecimal.valueOf(15.00));

        Product product = new Product(1L, "Updated Product", new BigDecimal("15.00"));
        ProductResponseDto dto = new ProductResponseDto(1L, "Updated Product", new BigDecimal("15.00"));

        when(productRequestMapper.toDomain(request)).thenReturn(product);
        when(productUseCasePort.update(product, 1L)).thenReturn(product);
        when(productResponseMapper.toResponse(product)).thenReturn(dto);

        mvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.price").value(15.00));
    }

    @Test
    void Put_update_productNotExist_return404() throws Exception {
        ProductRequestDto request = new ProductRequestDto();
        request.setName("Updated Product");
        request.setPrice(BigDecimal.valueOf(15.00));

        Product product = new Product(1L, "Updated Product", new BigDecimal("15.00"));

        when(productRequestMapper.toDomain(request)).thenReturn(product);
        when(productUseCasePort.update(product, 1L)).thenThrow(new ProductNotFoundException(1L));

        mvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void Patch_patch_productExist_return200() throws Exception {
        ProductNullableRequestDto request = new ProductNullableRequestDto();
        request.setName("Patched Product");
        request.setPrice(BigDecimal.valueOf(20.00));

        Product product = new Product(1L, "Patched Product", new BigDecimal("20.00"));
        ProductResponseDto dto = new ProductResponseDto(1L, "Patched Product", new BigDecimal("20.00"));

        when(productNullableMapper.toNullableDomain(request)).thenReturn(product);
        when(productUseCasePort.patch(product, 1L)).thenReturn(product);
        when(productResponseMapper.toResponse(product)).thenReturn(dto);

        mvc.perform(patch("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Patched Product"))
                .andExpect(jsonPath("$.price").value(20.00));
    }

    @Test
    void Patch_patch_productNotExist_return404() throws Exception {
        ProductNullableRequestDto request = new ProductNullableRequestDto();
        request.setName("Patched Product");
        request.setPrice(BigDecimal.valueOf(20.00));

        Product product = new Product(1L, "Patched Product", new BigDecimal("20.00"));

        when(productNullableMapper.toNullableDomain(request)).thenReturn(product);
        when(productUseCasePort.patch(product, 1L)).thenThrow(new ProductNotFoundException(1L));

        mvc.perform(patch("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void Delete_delete_productExist_return204() throws Exception {
        mvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void Delete_delete_productNotExist_return404() throws Exception {
        doThrow(new ProductNotFoundException(1L)).when(productUseCasePort).delete(1L);
        mvc.perform(delete("/api/products/1"))
                .andExpect(status().isNotFound());
    }

    private byte[] asJsonString(Object request) {
        try {
            return new ObjectMapper().writeValueAsBytes(request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}