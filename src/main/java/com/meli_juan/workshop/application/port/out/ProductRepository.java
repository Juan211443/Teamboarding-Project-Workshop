package com.meli_juan.workshop.application.port.out;

import com.meli_juan.workshop.application.domain.model.Product;
import org.springframework.data.domain.Page;

public interface ProductRepository {
    Page<Product> findAll(int page, int size);
    Product save(Product entity);
    Product find(long id);
    Product update(Product product, long id);
    Product patch(Product currentProduct, long id);
    void delete(long id);
    Product getByName(String name);
}
