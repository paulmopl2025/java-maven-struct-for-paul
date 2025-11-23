package com.example.vetclinic.cli.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAppointmentRequest {
    private LocalDateTime appointmentDate;
    private String notes;
    private Long petId;
    private Long vetId;
    private Long serviceId;
}
