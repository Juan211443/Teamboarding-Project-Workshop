package com.meli_juan.workshop.application.domain.exception;

public class ProductNotFoundException extends DomainException{
    public ProductNotFoundException(Long id) {
        super("Product with id: " + id + " not found");
    }
    public ProductNotFoundException() {super("Product not found");}
}
