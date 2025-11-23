package com.example.vetclinic.cli.client;

import com.example.vetclinic.cli.model.Appointment;
import com.example.vetclinic.cli.model.CreateAppointmentRequest;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface AppointmentClient {
    @GET("appointments")
    Call<List<Appointment>> getAllAppointments(@Header("Authorization") String token);

    @GET("appointments/{id}")
    Call<Appointment> getAppointmentById(@Header("Authorization") String token, @Path("id") Long id);

    @POST("appointments")
    Call<Appointment> createAppointment(@Header("Authorization") String token, @Body CreateAppointmentRequest request);

    @PUT("appointments/{id}/status")
    Call<Appointment> updateStatus(@Header("Authorization") String token, @Path("id") Long id,
            @Query("status") String status);

    @DELETE("appointments/{id}")
    Call<Void> cancelAppointment(@Header("Authorization") String token, @Path("id") Long id);
}
