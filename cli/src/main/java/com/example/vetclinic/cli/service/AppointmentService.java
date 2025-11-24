package com.example.vetclinic.cli.service;

import com.example.vetclinic.cli.client.ApiClient;
import com.example.vetclinic.cli.client.AppointmentClient;
import com.example.vetclinic.cli.model.Appointment;
import com.example.vetclinic.cli.model.CreateAppointmentRequest;
import com.example.vetclinic.cli.model.UpdateAppointmentRequest;
import retrofit2.Response;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class AppointmentService {

    private final AppointmentClient appointmentClient;
    private final AuthService authService;

    public AppointmentService(AuthService authService) {
        this.authService = authService;
        this.appointmentClient = ApiClient.createService(AppointmentClient.class);
    }

    private String getToken() {
        return "Bearer " + authService.getSession().getToken();
    }

    public List<Appointment> getAllAppointments() {
        try {
            Response<List<Appointment>> response = appointmentClient.getAllAppointments(getToken()).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public boolean updateAppointment(Long id, UpdateAppointmentRequest request) {
        try {
            Response<Appointment> response = appointmentClient.updateAppointment(getToken(), id, request).execute();
            return response.isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Appointment createAppointment(CreateAppointmentRequest request) {
        try {
            Response<Appointment> response = appointmentClient.createAppointment(getToken(), request).execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                System.err.println("Failed to create appointment: " + response.code() + " " + response.message());
                if (response.errorBody() != null) {
                    String errorBody = response.errorBody().string();
                    System.err.println("Error details: " + errorBody);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean confirmAppointment(Long id) {
        try {
            Response<Appointment> response = appointmentClient.confirmAppointment(getToken(), id).execute();
            return response.isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean cancelAppointment(Long id) {
        try {
            Response<Appointment> response = appointmentClient.cancelAppointment(getToken(), id).execute();
            return response.isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Appointment> getAppointmentsByVetAndDate(Long vetId, java.time.LocalDate date) {
        try {
            Response<List<Appointment>> response = appointmentClient.getAppointmentsByVet(getToken(), vetId).execute();
            if (response.isSuccessful() && response.body() != null) {
                // Filter by date
                return response.body().stream()
                        .filter(appt -> appt.getAppointmentDate().toLocalDate().equals(date))
                        .filter(appt -> !"CANCELLED".equals(appt.getStatus()))
                        .collect(java.util.stream.Collectors.toList());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
