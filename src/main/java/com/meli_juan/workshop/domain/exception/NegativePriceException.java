package com.meli_juan.workshop.domain.exception;

public class NegativePriceException extends RuntimeException{
    public NegativePriceException(String name) {
        super("Product with id: " + name + " has a negative price");
    }
}