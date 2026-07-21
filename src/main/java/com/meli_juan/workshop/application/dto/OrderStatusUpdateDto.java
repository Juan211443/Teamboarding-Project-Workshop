package com.meli_juan.workshop.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderStatusUpdateDto {

    @NotNull(message = "The status must not be null")
    private String status;
}
