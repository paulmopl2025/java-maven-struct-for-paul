package com.example.vetclinic.cli.service;

import com.example.vetclinic.cli.client.ApiClient;
import com.example.vetclinic.cli.client.PetClient;
import com.example.vetclinic.cli.model.CreatePetRequest;
import com.example.vetclinic.cli.model.Pet;
import retrofit2.Response;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class PetService {

    private final PetClient petClient;
    private final AuthService authService;

    public PetService(AuthService authService) {
        this.authService = authService;
        this.petClient = ApiClient.createService(PetClient.class);
    }

    private String getToken() {
        return "Bearer " + authService.getSession().getToken();
    }

    public List<Pet> getAllPets() {
        try {
            Response<List<Pet>> response = petClient.getAllPets(getToken()).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public Pet createPet(CreatePetRequest request) {
        try {
            Response<Pet> response = petClient.createPet(getToken(), request).execute();
            if (response.isSuccessful()) {
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deletePet(Long id) {
        try {
            Response<Void> response = petClient.deletePet(getToken(), id).execute();
            return response.isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
