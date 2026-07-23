package com.meli_juan.workshop.application.port.in;

import com.meli_juan.workshop.application.domain.model.Product;
import org.springframework.data.domain.Page;

public interface ProductPortIn {
    Page<Product> getAll(int page, int size);
    Product getById(long id);
    Product create(Product product);
    Product update(Product currentProduct, long id);
    Product patch(Product currentProduct, long id);
    void delete(long id);
    Product getByName(String name);
}
