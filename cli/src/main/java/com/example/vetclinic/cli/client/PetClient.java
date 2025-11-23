package com.example.vetclinic.cli.client;

import com.example.vetclinic.cli.model.CreatePetRequest;
import com.example.vetclinic.cli.model.Pet;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface PetClient {
    @GET("pets")
    Call<List<Pet>> getAllPets(@Header("Authorization") String token);

    @GET("pets/{id}")
    Call<Pet> getPetById(@Header("Authorization") String token, @Path("id") Long id);

    @POST("pets")
    Call<Pet> createPet(@Header("Authorization") String token, @Body CreatePetRequest request);

    @PUT("pets/{id}")
    Call<Pet> updatePet(@Header("Authorization") String token, @Path("id") Long id, @Body CreatePetRequest request);

    @DELETE("pets/{id}")
    Call<Void> deletePet(@Header("Authorization") String token, @Path("id") Long id);
}
