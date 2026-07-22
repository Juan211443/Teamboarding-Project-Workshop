package com.meli_juan.workshop.infrastructure.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Standard error response")
public record ErrorResponseDto(
        @Schema(description = "Error timestamp") Instant timestamp,
        @Schema(description = "HTTP status code", example = "404") int status,
        @Schema(description = "Descriptive error message", example = "Product not found with id: 1") String message
) {}
