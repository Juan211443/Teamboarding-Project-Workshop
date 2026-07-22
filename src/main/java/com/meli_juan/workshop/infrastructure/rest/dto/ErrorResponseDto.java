package com.meli_juan.workshop.infrastructure.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Respuesta de error estándar")
public record ErrorResponseDto(
        @Schema(description = "Timestamp del error") Instant timestamp,
        @Schema(description = "Código HTTP de estado", example = "404") int status,
        @Schema(description = "Mensaje descriptivo del error", example = "Product not found with id: 1") String message
) {}
