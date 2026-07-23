package com.meli_juan.workshop.e2e;

import com.meli_juan.workshop.application.dto.OrderRequestDto;
import com.meli_juan.workshop.application.dto.OrderItemRequestDto;
import com.meli_juan.workshop.application.dto.OrderResponseDto;
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
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class OrderE2ETest {

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
    void post_createOrder_withMultipleItems_returns201() {
        ProductResponseDto laptop = createProduct("E2E Laptop", 1000.00);
        ProductResponseDto mouse = createProduct("E2E Mouse", 25.00);

        OrderItemRequestDto item1 = new OrderItemRequestDto(
                laptop.getId(),
                2
        );
        OrderItemRequestDto item2 = new OrderItemRequestDto(
                mouse.getId(),
                3
        );

        OrderRequestDto request = new OrderRequestDto(
                List.of(item1, item2)
        );

        OrderResponseDto created = getClient().post()
                .uri("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(OrderResponseDto.class);

        assertNotNull(created);
        assertEquals("PENDING", created.status());
        assertEquals(0, BigDecimal.valueOf(2075.00).compareTo(created.totalPrice()));
        assertEquals(2, created.items().size());
    }

    @Test
    void patch_updateStatus_returns200() {
        ProductResponseDto product = createProduct("E2E StatusProduct", 500.00);

        OrderItemRequestDto item = new OrderItemRequestDto(
                product.getId(),
                1
        );
        OrderRequestDto request = new OrderRequestDto(
                List.of(item)
        );

        OrderResponseDto created = getClient().post()
                .uri("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(OrderResponseDto.class);

        assertNotNull(created);

        OrderResponseDto updated = getClient().patch()
                .uri("/api/orders/" + created.id() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"status\":\"CONFIRMED\"}")
                .retrieve()
                .body(OrderResponseDto.class);

        assertNotNull(updated);
        assertEquals("CONFIRMED", updated.status());
        assertEquals(created.id(), updated.id());
    }

    @Test
    void patch_updateStatus_nonExistingOrder_returns404() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class,
                () -> getClient().patch()
                        .uri("/api/orders/99999/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"status\":\"CONFIRMED\"}")
                        .retrieve()
                        .body(String.class));

        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void get_getById_nonExistingOrder_returns404() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class,
                () -> getClient().get()
                        .uri("/api/orders/99999")
                        .retrieve()
                        .body(String.class));

        assertEquals(404, ex.getStatusCode().value());
    }
}
