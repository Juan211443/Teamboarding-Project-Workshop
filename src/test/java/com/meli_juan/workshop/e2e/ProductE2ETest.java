package com.meli_juan.workshop.e2e;

import com.meli_juan.workshop.application.dto.ProductRequestDto;
import com.meli_juan.workshop.application.dto.ProductResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ProductE2ETest {

    @LocalServerPort
    private int port;

    private RestClient getClient() {
        return RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    private ProductResponseDto createProduct(String name, double price) {
        ProductRequestDto request = new ProductRequestDto();
        request.setName(name);
        request.setPrice(BigDecimal.valueOf(price));

        return getClient().post()
                .uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(ProductResponseDto.class);
    }

    @Test
    void post_createProduct_returns201() {
        ProductResponseDto created = createProduct("Laptop E2E", 999.99);

        assertNotNull(created);
        assertEquals("Laptop E2E", created.getName());
        assertEquals(BigDecimal.valueOf(999.99), created.getPrice());
    }

    @Test
    void post_invalidProduct_returns400() {
        ProductRequestDto request = new ProductRequestDto();
        request.setName("");
        request.setPrice(BigDecimal.valueOf(-10.00));

        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class,
                () -> getClient().post()
                        .uri("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(request)
                        .retrieve()
                        .body(ProductResponseDto.class));

        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void get_existingProduct_returns200() {
        ProductResponseDto created = createProduct("Phone E2E", 599.99);

        ProductResponseDto found = getClient().get()
                .uri("/api/products/" + created.getId())
                .retrieve()
                .body(ProductResponseDto.class);

        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals("Phone E2E", found.getName());
    }

    @Test
    void get_nonExistingProduct_returns404() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class,
                () -> getClient().get()
                        .uri("/api/products/99999")
                        .retrieve()
                        .body(String.class));

        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void get_allProducts_returns200() {
        createProduct("PaginationTest", 10.00);

        String response = getClient().get()
                .uri("/api/products?page=0&size=10")
                .retrieve()
                .body(String.class);

        assertNotNull(response);
        assertTrue(response.contains("content"));
    }

    @Test
    void put_updateProduct_returns200() {
        ProductResponseDto created = createProduct("OldName", 100.00);

        ProductRequestDto updateRequest = new ProductRequestDto();
        updateRequest.setName("NewName");
        updateRequest.setPrice(BigDecimal.valueOf(200.00));

        ProductResponseDto updated = getClient().put()
                .uri("/api/products/" + created.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateRequest)
                .retrieve()
                .body(ProductResponseDto.class);

        assertNotNull(updated);
        assertEquals(created.getId(), updated.getId());
        assertEquals("NewName", updated.getName());
        assertEquals(BigDecimal.valueOf(200.00), updated.getPrice());
    }

    @Test
    void put_nonExistingProduct_returns404() {
        ProductRequestDto request = new ProductRequestDto();
        request.setName("Ghost");
        request.setPrice(BigDecimal.valueOf(50.00));

        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class,
                () -> getClient().put()
                        .uri("/api/products/99999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(request)
                        .retrieve()
                        .body(String.class));

        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void patch_updateName_returns200() {
        ProductResponseDto created = createProduct("PatchOriginal", 75.00);

        String patchBody = "{\"name\": \"PatchUpdated\"}";

        ProductResponseDto patched = getClient().patch()
                .uri("/api/products/" + created.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(patchBody)
                .retrieve()
                .body(ProductResponseDto.class);

        assertNotNull(patched);
        assertEquals("PatchUpdated", patched.getName());
        assertEquals(0, BigDecimal.valueOf(75.00).compareTo(patched.getPrice()));
    }

    @Test
    void patch_nonExistingProduct_returns404() {
        String patchBody = "{\"name\": \"Ghost\"}";

        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class,
                () -> getClient().patch()
                        .uri("/api/products/99999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(patchBody)
                        .retrieve()
                        .body(String.class));

        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void delete_existingProduct_returns204() {
        ProductResponseDto created = createProduct("ToDelete", 30.00);

        getClient().delete()
                .uri("/api/products/" + created.getId())
                .retrieve()
                .toBodilessEntity();

        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class,
                () -> getClient().get()
                        .uri("/api/products/" + created.getId())
                        .retrieve()
                        .body(String.class));

        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void delete_nonExistingProduct_returns404() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class,
                () -> getClient().delete()
                        .uri("/api/products/99999")
                        .retrieve()
                        .toBodilessEntity());

        assertEquals(404, ex.getStatusCode().value());
    }
}
