package com.example.vetclinic.cli.service;

import com.example.vetclinic.cli.client.ApiClient;
import com.example.vetclinic.cli.client.ClinicClient;
import com.example.vetclinic.cli.model.ClinicStatsDTO;
import retrofit2.Response;

import java.io.IOException;

public class ClinicService {

    private final ClinicClient clinicClient;
    private final AuthService authService;

    public ClinicService(AuthService authService) {
        this.authService = authService;
        this.clinicClient = ApiClient.createService(ClinicClient.class);
    }

    private String getToken() {
        return "Bearer " + authService.getSession().getToken();
    }

    public ClinicStatsDTO getStats() {
        try {
            Response<ClinicStatsDTO> response = clinicClient.getStats(getToken()).execute();
            if (response.isSuccessful()) {
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
