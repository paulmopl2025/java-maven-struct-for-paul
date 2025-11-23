package com.example.vetclinic.cli.service;

import com.example.vetclinic.cli.client.ApiClient;
import com.example.vetclinic.cli.client.VetClient;
import com.example.vetclinic.cli.model.VetDTO;
import retrofit2.Response;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class VetService {
    private final VetClient vetClient;
    private final AuthService authService;

    public VetService(AuthService authService) {
        this.authService = authService;
        this.vetClient = ApiClient.createService(VetClient.class);
    }

    private String getToken() {
        return "Bearer " + authService.getSession().getToken();
    }

    public List<VetDTO> getAllVets() {
        try {
            Response<List<VetDTO>> response = vetClient.getAllVets(getToken()).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
