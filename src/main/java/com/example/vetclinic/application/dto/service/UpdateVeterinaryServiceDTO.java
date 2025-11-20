package com.example.vetclinic.application.dto.service;

import com.example.vetclinic.domain.model.ServiceType;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateVeterinaryServiceDTO {
    private String name;
    private String description;
    private ServiceType serviceType;

    @DecimalMin(value = "0.0", inclusive = false, message = "Base cost must be greater than 0")
    private BigDecimal baseCost;

    private Integer estimatedDurationMinutes;
    private Boolean active;
}
