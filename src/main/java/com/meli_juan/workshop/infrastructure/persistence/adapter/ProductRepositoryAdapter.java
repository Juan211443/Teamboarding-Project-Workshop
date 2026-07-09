package com.meli_juan.workshop.infrastructure.persistence.adapter;

import com.meli_juan.workshop.domain.exception.NegativePriceException;
import com.meli_juan.workshop.domain.exception.ProductNotFoundException;
import com.meli_juan.workshop.domain.model.Product;
import com.meli_juan.workshop.domain.port.ProductRepository;
import com.meli_juan.workshop.infrastructure.persistence.entity.ProductEntity;
import com.meli_juan.workshop.infrastructure.persistence.jpa.ProductJpaRepository;
import com.meli_juan.workshop.infrastructure.persistence.mapper.ProductEntityMapper;
import com.meli_juan.workshop.infrastructure.util.PatchUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;

@Slf4j
@Repository
public class ProductRepositoryAdapter implements ProductRepository {

    private final ProductJpaRepository jpaRepository;
    private final ProductEntityMapper entityMapper;

    public ProductRepositoryAdapter(ProductJpaRepository jpaRepository, ProductEntityMapper entityMapper) {
        this.jpaRepository = jpaRepository;
        this.entityMapper = entityMapper;
    }

    @Override
    public Page<Product> findAll(int page, int size) {
        Pageable pageRequest = PageRequest.of(page, size);
        log.debug("Fetching products - page={}, size={}", page, size);
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
        return  jpaRepository.findById(id).stream()
                .map(entityMapper::toDomain)
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    public Product update(Product currentProduct, long id) {
        log.debug("Updating product id={}", id);
        return jpaRepository.findById(id)
                .map(productEntity ->
                        entityMapper.toDomain(jpaRepository.save(entityMapper.toEntity(currentProduct))))
                .orElseThrow(() -> new ProductNotFoundException(id));
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
    public String delete(Long id) {
        log.debug("Deleting product id={}", id);
        return jpaRepository.findById(id)
                .map(productEntity -> {
                    jpaRepository.deleteById(id);
                    return "Product with id: " + id + " deleted successfully";
                })
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    public Product getByName(String name) {
        log.debug("Finding product by name={}", name);
        ProductEntity product = jpaRepository.findByName(name);
        //TODO: Logica invertida;
        if(product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            return entityMapper.toDomain(product);
        }
        throw new NegativePriceException(product.getName());
    }
}