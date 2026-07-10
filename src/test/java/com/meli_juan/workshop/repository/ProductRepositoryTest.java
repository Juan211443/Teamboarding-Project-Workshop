package com.meli_juan.workshop.repository;

import com.meli_juan.workshop.infrastructure.persistence.entity.ProductEntity;
import com.meli_juan.workshop.infrastructure.persistence.repository.ProductJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private ProductJpaRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    private ProductEntity createProductWithoutId(String name, double price) {
        ProductEntity product = new ProductEntity();
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }

    private ProductEntity createCompleteProduct(Long id, String name, double price) {
        ProductEntity product = new ProductEntity();
        product.setId(id);
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }

    private Long laptopId, phoneId, carId, bedId, monitorId;

    @BeforeEach
    void setUp() {
        laptopId = entityManager.persist(createProductWithoutId("Laptop", 20.0)).getId();
        phoneId = entityManager.persist(createProductWithoutId("Phone", 20.0)).getId();
        carId = entityManager.persist(createProductWithoutId("Car", 30.0)).getId();
        bedId = entityManager.persist(createProductWithoutId("Bed", 40.0)).getId();
        monitorId = entityManager.persist(createProductWithoutId("Monitor", 50.0)).getId();
        entityManager.flush();
    }

    @Test
    void findAll_productsExist_returnsAll() {
        Page<ProductEntity> products = productRepository.findAll(PageRequest.of(0, 5));
        assertEquals(5, products.getTotalElements());
    }

    @Test
    void findAll_productsExist_returnsThirdPage() {
        Page<ProductEntity> products = productRepository.findAll(PageRequest.of(0, 3));
        assertEquals(3, products.getContent().size());
        assertTrue(products.stream().anyMatch(p -> p.getName().equals("Car")));
    }

    @Test
    void save_saveProduct_returnProductInserted() {
        ProductEntity product = createProductWithoutId("Table", 60.0);
        ProductEntity savedProduct = entityManager.persist(product);
        entityManager.flush();
        assertEquals("Table", savedProduct.getName());
        assertEquals(BigDecimal.valueOf(60.0), savedProduct.getPrice());
    }

    @Test
    void save_nullName_throwsException() {
        ProductEntity product = new ProductEntity();
        product.setPrice(BigDecimal.valueOf(10.0));
        assertThrows(Exception.class, () -> {
            entityManager.persist(product);
            entityManager.flush();
        });
    }

    @Test
    void save_nullPrice_throwsException() {
        ProductEntity product = new ProductEntity();
        product.setName("Apple");
        assertThrows(Exception.class, () -> {
            entityManager.persist(product);
            entityManager.flush();
        });
    }

    @Test
    void findById_productExists_returnsProduct() {
        ProductEntity foundProduct = entityManager.find(ProductEntity.class, bedId);
        assertNotNull(foundProduct);
        assertEquals("Bed", foundProduct.getName());
        assertEquals(BigDecimal.valueOf(40.0), foundProduct.getPrice());
    }

    @Test
    void update_productDoesNotExist_throwsException() {
        assertThrows(Exception.class, () -> {
            ProductEntity product = createCompleteProduct(
                    999999L, "Nonexistent", 101.0);
            entityManager.merge(product);
            entityManager.flush();
        });
    }

    @Test
    void update_productExists_returnProduct() {
        ProductEntity product = createCompleteProduct(phoneId,"existent", 100.0);
        entityManager.merge(product);
        entityManager.flush();
        ProductEntity found = entityManager.find(ProductEntity.class, phoneId);
        assertNotNull(found);
        assertEquals(product.getName(), found.getName());
    }

    @Test
    void delete_unmanagedEntity_throwsException(){
        ProductEntity product = new ProductEntity();
        product.setId(999999L);
        assertThrows(Exception.class, () -> {
            entityManager.remove(product);
            entityManager.flush();
        });
    }

    @Test
    void delete_productExists_deletesProduct() {
        ProductEntity product = entityManager.find(ProductEntity.class, laptopId);
        assertNotNull(product);
        entityManager.remove(product);
        entityManager.flush();
        ProductEntity deletedProduct = entityManager.find(ProductEntity.class, laptopId);
        assertNull(deletedProduct);
    }

    @Test
    void getByName_productExists_returnProduct(){
        ProductEntity product = productRepository.findByName("Monitor");
        assertNotNull(product);
        assertEquals("Monitor", product.getName());
        assertEquals(BigDecimal.valueOf(50.0), product.getPrice());
    }

    @Test
    void getByName_ifNotExist_returnsNull(){
        ProductEntity product = productRepository.findByName("Icon");
        assertNull(product);
    }
}