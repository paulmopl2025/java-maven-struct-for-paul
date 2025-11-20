package com.example.vetclinic.application.dto.service;

import com.example.vetclinic.domain.model.ServiceType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateVeterinaryServiceDTO {
    @NotBlank(message = "Service name is required")
    private String name;

    private String description;

    @NotNull(message = "Service type is required")
    private ServiceType serviceType;

    @NotNull(message = "Base cost is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Base cost must be greater than 0")
    private BigDecimal baseCost;

    private Integer estimatedDurationMinutes;
}
