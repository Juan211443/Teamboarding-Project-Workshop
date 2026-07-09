package com.meli_juan.workshop.infrastructure.rest.dto;

import java.time.Instant;

public record ErrorResponseDto(Instant timestamp, int status, String message) {}