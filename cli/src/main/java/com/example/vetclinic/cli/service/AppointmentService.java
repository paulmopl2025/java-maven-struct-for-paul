package com.example.vetclinic.cli.service;

import com.example.vetclinic.cli.client.ApiClient;
import com.example.vetclinic.cli.client.AppointmentClient;
import com.example.vetclinic.cli.model.Appointment;
import com.example.vetclinic.cli.model.CreateAppointmentRequest;
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

    public Appointment createAppointment(CreateAppointmentRequest request) {
        try {
            Response<Appointment> response = appointmentClient.createAppointment(getToken(), request).execute();
            if (response.isSuccessful()) {
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateStatus(Long id, String status) {
        try {
            Response<Appointment> response = appointmentClient.updateStatus(getToken(), id, status).execute();
            return response.isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
