package com.meli_juan.workshop.infrastructure.rest.exception;

import com.meli_juan.workshop.domain.exception.NegativePriceException;
import com.meli_juan.workshop.domain.exception.ProductNotFoundException;
import com.meli_juan.workshop.infrastructure.rest.dto.ErrorResponseDto;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(ProductNotFoundException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDto(Instant.now(), 404 , exception.getMessage()));
    }

    @ExceptionHandler(NegativePriceException.class)
    public ResponseEntity<ErrorResponseDto> handlerBadRequest(NegativePriceException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto(Instant.now(), 400 , exception.getMessage()));
    }

    @ExceptionHandler(IncorrectResultSizeDataAccessException.class)
    public ResponseEntity<ErrorResponseDto> handleNonUniqueResult(IncorrectResultSizeDataAccessException exception){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDto(Instant.now(), 409, "Multiple results found for the given query"));
    }
}