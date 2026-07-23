package com.meli_juan.workshop.application.domain.exception;

public class OrderNotFoundException extends DomainException {
    public OrderNotFoundException(Long id) {
        super("Order with id: " + id + " not found");
    }
}
