package com.meli_juan.workshop.infrastructure.rest.exception;

import com.meli_juan.workshop.application.domain.exception.NegativePriceException;
import com.meli_juan.workshop.application.domain.exception.OrderNotFoundException;
import com.meli_juan.workshop.application.domain.exception.ProductNotFoundException;
import com.meli_juan.workshop.infrastructure.rest.dto.ErrorResponseDto;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(MethodArgumentNotValidException exception){
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((firstMessage, nextMessage) -> firstMessage + "; " + nextMessage)
                .orElse("Validation failed");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto(Instant.now(), HttpStatus.BAD_REQUEST.value(), message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception exception){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto(Instant.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage()));
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleProductNotFound(ProductNotFoundException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDto(Instant.now(), HttpStatus.NOT_FOUND.value(), exception.getMessage()));
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleOrderNotFound(OrderNotFoundException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDto(Instant.now(), HttpStatus.NOT_FOUND.value(), exception.getMessage()));
    }

    @ExceptionHandler(NegativePriceException.class)
    public ResponseEntity<ErrorResponseDto> handleBadRequest(NegativePriceException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto(Instant.now(), HttpStatus.BAD_REQUEST.value(), exception.getMessage()));
    }

    @ExceptionHandler(IncorrectResultSizeDataAccessException.class)
    public ResponseEntity<ErrorResponseDto> handleNonUniqueResult(IncorrectResultSizeDataAccessException exception){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDto(Instant.now(), HttpStatus.CONFLICT.value(), "Multiple results found for the given query"));
    }
}