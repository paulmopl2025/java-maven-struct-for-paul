package com.example.vetclinic.cli.client;

import com.example.vetclinic.cli.model.LoginRequest;
import com.example.vetclinic.cli.model.LoginResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthClient {
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
}
