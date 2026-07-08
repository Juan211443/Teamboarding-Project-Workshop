package com.meli_juan.workshop.domain.port;

import com.meli_juan.workshop.domain.model.Product;
import org.springframework.data.domain.Page;

public interface ProductUseCasePort {
    Page<Product> getAll(int page, int size);
    Product getById(long id);
    Product create(Product product);
    Product update(Product currentProduct, long id);
    Product patch(Product currentProduct, long id);
    String delete(Long id);
    Product getByName(String name);
}
