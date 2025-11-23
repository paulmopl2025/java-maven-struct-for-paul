package com.example.vetclinic.cli.client;

import com.example.vetclinic.cli.model.ClinicStatsDTO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface ClinicClient {
    @GET("clinic/stats")
    Call<ClinicStatsDTO> getStats(@Header("Authorization") String token);
}
