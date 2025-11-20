package com.example.vetclinic.application.dto.service;

import com.example.vetclinic.domain.model.ServiceType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class VeterinaryServiceDTO {
    private Long id;
    private String name;
    private String description;
    private ServiceType serviceType;
    private BigDecimal baseCost;
    private Integer estimatedDurationMinutes;
    private Boolean active;
}
