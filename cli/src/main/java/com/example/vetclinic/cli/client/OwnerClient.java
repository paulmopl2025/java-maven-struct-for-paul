package com.example.vetclinic.cli.client;

import com.example.vetclinic.cli.model.CreateOwnerRequest;
import com.example.vetclinic.cli.model.Owner;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface OwnerClient {
    @GET("owners")
    Call<List<Owner>> getAllOwners(@Header("Authorization") String token);

    @GET("owners/{id}")
    Call<Owner> getOwnerById(@Header("Authorization") String token, @Path("id") Long id);

    @POST("owners")
    Call<Owner> createOwner(@Header("Authorization") String token, @Body CreateOwnerRequest request);

    @PUT("owners/{id}")
    Call<Owner> updateOwner(@Header("Authorization") String token, @Path("id") Long id,
            @Body CreateOwnerRequest request);

    @DELETE("owners/{id}")
    Call<Void> deleteOwner(@Header("Authorization") String token, @Path("id") Long id);
}
