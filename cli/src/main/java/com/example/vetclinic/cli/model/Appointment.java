package com.example.vetclinic.cli.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {
    private Long id;
    private LocalDateTime appointmentDate;
    private String notes;
    private String status;
    private Long petId;
    private String petName;
    private Long vetId;
    private String vetName;
    private Long serviceId;
    private String serviceName;
}
