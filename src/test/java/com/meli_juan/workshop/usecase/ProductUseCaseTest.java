package com.meli_juan.workshop.usecase;

import com.meli_juan.workshop.domain.exception.ProductNotFoundException;
import com.meli_juan.workshop.domain.model.Product;
import com.meli_juan.workshop.domain.port.ProductRepository;
import com.meli_juan.workshop.domain.usecase.ProductUseCase;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductUseCaseTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductUseCase useCase;

    private Product car, phone, bed, monitor, laptop, pants;

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
        pants = createProduct("Pants", 20.00);
    }

    @Test
    void getAll_fiveProducts_returnFiveProducts(){
        Page<Product> products = new PageImpl<>(List.of(car, phone, bed, monitor, laptop));
        when(repository.findAll(0, 10)).thenReturn(products);

        Page<Product> result = useCase.getAll(0, 10);

        verify(repository).findAll(0, 10);
        assertNotNull(result);
        assertEquals(1, result.getTotalPages());
        assertEquals(5, result.getTotalElements());
    }

    @Test
    void getAll_noProducts_returnEmptyPage(){
        Page<Product> products = new PageImpl<>(List.of());
        when(repository.findAll(0, 10)).thenReturn(products);

        Page<Product> result = useCase.getAll(0, 10);

        verify(repository).findAll(0, 10);
        assertNotNull(result);
        assertEquals(1, result.getTotalPages());
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void getById_existingProduct_returnProduct(){
        when(repository.find(1L)).thenReturn(car);

        Product result = useCase.getById(1L);

        verify(repository).find(1L);
        assertNotNull(result);
        assertEquals("Car", result.getName());
        assertEquals(BigDecimal.valueOf(500.00), result.getPrice());
    }

    @Test
    void getById_noProducts_returnException(){
        when(repository.find(1L)).thenThrow(new ProductNotFoundException(1L));

        ProductNotFoundException ex = assertThrows(ProductNotFoundException.class, () -> useCase.getById(1L));

        verify(repository).find(1L);
        assertTrue(ex.getMessage().contains("1"));
    }

    @Test
    void create_validProduct_returnProduct() {
        when(repository.save(laptop)).thenReturn(laptop);

        Product result = useCase.create(laptop);

        verify(repository).save(laptop);
        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        assertEquals(BigDecimal.valueOf(38.99), result.getPrice());
    }

    @Test
    void create_twoProducts_returnProduct(){
        when(repository.save(car)).thenReturn(car);
        when(repository.save(phone)).thenReturn(phone);

        Product carProduct = useCase.create(car);
        Product phoneProduct = useCase.create(phone);

        verify(repository).save(car);
        verify(repository).save(phone);
        assertNotNull(carProduct);
        assertNotNull(phoneProduct);
        assertEquals("Car", carProduct.getName());
        assertEquals(BigDecimal.valueOf(500.00), carProduct.getPrice());
        assertEquals("Phone", phoneProduct.getName());
        assertEquals(BigDecimal.valueOf(30.00), phoneProduct.getPrice());
    }

    @Test
    void update_existingProduct_returnUpdatedProduct() {
        when(repository.update(pants, 1L)).thenReturn(pants);

        Product result = useCase.update(pants, 1L);

        verify(repository).update(pants, 1L);
        assertNotNull(result);
        assertEquals("Pants", result.getName());
        assertEquals(BigDecimal.valueOf(20.00), result.getPrice());
    }

    @Test
    void patch_existingProduct_returnPatchedProduct() {
        when(repository.patch(pants, 1L)).thenReturn(pants);

        Product result = useCase.patch(pants, 1L);

        verify(repository).patch(pants, 1L);
        assertNotNull(result);
        assertEquals("Pants", result.getName());
        assertEquals(BigDecimal.valueOf(20.00), result.getPrice());
    }

    @Test
    void patch_existingProductWithNullPrice_returnPatchedProduct() {
        Product productWithNullPrice = createProduct("Banana", 0.00);
        when(repository.patch(productWithNullPrice, 1L)).thenReturn(productWithNullPrice);

        Product result = useCase.patch(productWithNullPrice, 1L);

        verify(repository).patch(productWithNullPrice, 1L);
        assertNotNull(result);
        assertEquals("Banana", result.getName());
        assertEquals(BigDecimal.valueOf(0.00), result.getPrice());
    }

    @Test
    void delete_existingProduct_verifyDeleteCalled() {
        useCase.delete(1L);

        verify(repository).delete(1L);
    }

    @Test
    void delete_noProducts_verifyDeleteCalled() {
        useCase.delete(1L);

        verify(repository).delete(1L);
    }

    @Test
    void getByName_getPrice_returnProduct(){
        when(repository.getByName("Car")).thenReturn(car);

        Product result = useCase.getByName("Car");

        verify(repository).getByName("Car");
        assertNotNull(result);
        assertEquals("Car", result.getName());
        assertEquals(BigDecimal.valueOf(500.00), result.getPrice());
    }
}