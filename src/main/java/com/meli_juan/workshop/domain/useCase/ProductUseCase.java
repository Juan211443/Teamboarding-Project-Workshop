package com.meli_juan.workshop.domain.useCase;

import com.meli_juan.workshop.domain.model.Product;
import com.meli_juan.workshop.domain.port.ProductUseCasePort;
import com.meli_juan.workshop.infrastructure.persistence.adapter.ProductRepositoryAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ProductUseCase implements ProductUseCasePort {
    private final ProductRepositoryAdapter repository;

    public ProductUseCase(ProductRepositoryAdapter repository){
        this.repository = repository;
    }

    public Page<Product> getAll(int page, int size){
        Page<Product> products = repository.findAll(page, size);
        log.debug("Viewing products - page={}, size={}", page, size);
        return products;
    }

    public Product getById(long id){
        Product product = repository.find(id);
        log.debug("Viewing product with id={}", id);
        return product;
    }

    @Transactional
    public Product create(Product product) {
        Product newProduct = repository.save(product);
        log.info("Product created: id={}, name={}", newProduct.getId(), newProduct.getName());
        return newProduct;
    }

    @Transactional
    public Product update(Product currentProduct, long id){
        Product product = repository.update(currentProduct, id);
        log.info("Product updated: id={}", id);
        return product;
    }

    @Transactional
    public Product patch(Product currentProduct, long id){
        Product product = repository.patch(currentProduct, id);
        log.info("Product patched: id={}", id);
        return product;
    }

    @Transactional
    public String delete(Long id) {
        String result = repository.delete(id);
        log.info("Product deleted: id={}", id);
        return result;
    }

    @Override
    public Product getByName(String name) {
        Product product = repository.getByName(name);
        log.info("Product with name={} was found: {}", name, product);
        return product;
    }
}