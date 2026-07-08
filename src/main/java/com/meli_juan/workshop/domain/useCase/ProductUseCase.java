package com.meli_juan.workshop.domain.useCase;

import com.meli_juan.workshop.domain.model.Product;
import com.meli_juan.workshop.domain.port.ProductUseCasePort;
import com.meli_juan.workshop.infrastructure.persistence.adapter.ProductRepositoryAdapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductUseCase implements ProductUseCasePort {
    private final ProductRepositoryAdapter repository;

    public ProductUseCase(ProductRepositoryAdapter repository){
        this.repository = repository;
    }

    public Page<Product> getAll(int page, int size){
        return repository.findAll(page, size);
    }

    public Product getById(long id){
        return repository.find(id);
    }

    @Transactional
    public Product create(Product product) {
        return repository.save(product);
    }

    @Transactional
    public Product update(Product currentProduct, long id){
        return repository.update(currentProduct, id);
    }

    @Transactional
    public Product patch(Product currentProduct, long id){
        return repository.patch(currentProduct, id);
    }

    @Transactional
    public String delete(Long id) {
        return repository.delete(id);
    }

    @Override
    public Product getByName(String name) {
        return repository.getByName(name);
    }
}
