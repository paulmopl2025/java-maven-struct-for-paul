package com.example.vetclinic.cli.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Request payload for updating an existing appointment.
 * Mirrors the fields that can be modified: date/time, notes, pet, vet and
 * service.
 */
@Data
public class UpdateAppointmentRequest {
    private LocalDateTime appointmentDate;
    private String notes;
    private Long petId;
    private Long vetId;
    private Long serviceId;
}
