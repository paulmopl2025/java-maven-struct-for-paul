package com.example.vetclinic.cli.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClinicStatsDTO {
    private long totalVets;
    private long totalPatients;
    private long totalAppointments;
    private long activeServices;
    private Map<String, Long> appointmentsByStatus;
}
