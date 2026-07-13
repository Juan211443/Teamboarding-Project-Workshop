package com.meli_juan.workshop.useCase;

import com.meli_juan.workshop.domain.model.Product;
import com.meli_juan.workshop.domain.port.ProductRepository;
import com.meli_juan.workshop.domain.port.ProductUseCasePort;
import com.meli_juan.workshop.domain.useCase.ProductUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductUseCaseTests {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductUseCase useCase;

    private Product car, phone, bed, monitor, laptop;

    private Product createProduct(String name, double price){
        Product product = new Product();
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }

    @BeforeEach
    void setUp() {
        car = createProduct("Car", 500.00);
        phone = createProduct("Phone", 30.00);
        bed = createProduct("Bed", 50.20);
        monitor = createProduct("Monitor", 15.99);
        laptop = createProduct("Laptop", 38.99);
    }

    @Test
    void getAll_fiveProducts_returnFiveProducts(){
        Page<Product> products = new PageImpl<>(List.of(car, phone, bed, monitor, laptop));
        when(repository.findAll(0, 10)).thenReturn(products);

        Page<Product> result = useCase.getAll(0, 10);

        verify(repository).findAll(0, 10);
        assertNotNull(result);
        assertEquals(1, result.getTotalPages());

    }
}