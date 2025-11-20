package com.example.vetclinic.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "veterinary_services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VeterinaryService extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType serviceType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal baseCost;

    @Column(name = "estimated_duration_minutes")
    private Integer estimatedDurationMinutes;

    @Column(nullable = false)
    private Boolean active = true;
}
