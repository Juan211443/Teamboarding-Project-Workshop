package com.meli_juan.workshop.infrastructure.rest.dto;

import lombok.Getter;
import java.time.Instant;

@Getter
public class ErrorResponseDto {
    private final String message;
    private final int status;
    private final Instant timestamp;

    public ErrorResponseDto(Instant timestamp, int status, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
    }
}