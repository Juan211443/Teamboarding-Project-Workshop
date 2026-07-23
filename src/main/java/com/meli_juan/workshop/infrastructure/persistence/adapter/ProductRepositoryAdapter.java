package com.meli_juan.workshop.infrastructure.persistence.adapter;

import com.meli_juan.workshop.application.domain.exception.ProductNotFoundException;
import com.meli_juan.workshop.application.domain.model.Product;
import com.meli_juan.workshop.application.port.out.ProductRepository;
import com.meli_juan.workshop.infrastructure.persistence.repository.ProductJpaRepository;
import com.meli_juan.workshop.infrastructure.persistence.mapper.ProductEntityMapper;
import com.meli_juan.workshop.infrastructure.util.PatchUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepository {

    private final ProductJpaRepository jpaRepository;
    private final ProductEntityMapper entityMapper;

    @Override
    public Page<Product> findAll(int page, int size) {
        log.debug("Fetching products - page={}, size={}", page, size);
        Pageable pageRequest = PageRequest.of(page, size);
        return jpaRepository.findAll(pageRequest).map(entityMapper::toDomain);
    }

    @Override
    public Product save(Product entity) {
        log.debug("Saving product: {}", entity);
        return entityMapper.toDomain(jpaRepository.save(entityMapper.toEntity(entity)));
    }

    @Override
    public Product find(long id) {
        log.debug("Finding product by id={}", id);
        return  jpaRepository.findById(id)
                .map(entityMapper::toDomain)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    public Product update(Product currentProduct, long id) {
        log.debug("Updating product id={}", id);
        if (!jpaRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        currentProduct.setId(id);
        return entityMapper.toDomain(jpaRepository.save(entityMapper.toEntity(currentProduct)));
    }

    @Override
    public Product patch(Product currentProduct, long id) {
        log.debug("Patching product id={}", id);
        return jpaRepository.findById(id)
                .map(productEntity -> {
                    Product product = entityMapper.toDomain(productEntity);
                    PatchUtils.copyNonNullProperties(currentProduct, product);
                    return entityMapper.toDomain(jpaRepository.save(entityMapper.toEntity(product)));
                })
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    public void delete(long id) {
        if (!jpaRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        jpaRepository.deleteById(id);
    }

    @Override
    public Product getByName(String name) {
        return Optional.ofNullable(jpaRepository.findByName(name))
                .map(entityMapper::toDomain)
                .orElseThrow(ProductNotFoundException::new);
    }
}