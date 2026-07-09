package com.meli_juan.workshop.domain.exception;

public abstract class DomainException extends RuntimeException{
    public DomainException(String message){
        super(message);
    }
}