package com.example.vetclinic.cli.client;

import com.example.vetclinic.cli.model.VetDTO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

import java.util.List;

public interface VetClient {
    @GET("vets")
    Call<List<VetDTO>> getAllVets(@Header("Authorization") String token);
}
