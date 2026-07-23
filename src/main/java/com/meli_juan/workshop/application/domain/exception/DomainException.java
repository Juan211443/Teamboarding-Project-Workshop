package com.meli_juan.workshop.application.domain.exception;

public abstract class DomainException extends RuntimeException{
    public DomainException(String message){
        super(message);
    }
}
