package com.example.vetclinic.cli.service;

import com.example.vetclinic.cli.client.ApiClient;
import com.example.vetclinic.cli.client.OwnerClient;
import com.example.vetclinic.cli.model.CreateOwnerRequest;
import com.example.vetclinic.cli.model.Owner;
import retrofit2.Response;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class OwnerService {

    private final OwnerClient ownerClient;
    private final AuthService authService;

    public OwnerService(AuthService authService) {
        this.authService = authService;
        this.ownerClient = ApiClient.createService(OwnerClient.class);
    }

    private String getToken() {
        return "Bearer " + authService.getSession().getToken();
    }

    public List<Owner> getAllOwners() {
        try {
            Response<List<Owner>> response = ownerClient.getAllOwners(getToken()).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public Owner createOwner(CreateOwnerRequest request) {
        try {
            Response<Owner> response = ownerClient.createOwner(getToken(), request).execute();
            if (response.isSuccessful()) {
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteOwner(Long id) {
        try {
            Response<Void> response = ownerClient.deleteOwner(getToken(), id).execute();
            return response.isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
