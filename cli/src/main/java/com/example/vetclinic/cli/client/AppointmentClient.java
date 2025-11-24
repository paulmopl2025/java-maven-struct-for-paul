package com.example.vetclinic.cli.client;

import com.example.vetclinic.cli.model.Appointment;
import com.example.vetclinic.cli.model.CreateAppointmentRequest;
import com.example.vetclinic.cli.model.UpdateAppointmentRequest;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface AppointmentClient {
    @GET("appointments")
    Call<List<Appointment>> getAllAppointments(@Header("Authorization") String token);

    @GET("appointments/{id}")
    Call<Appointment> getAppointmentById(@Header("Authorization") String token, @Path("id") Long id);

    @GET("appointments/vet/{vetId}")
    Call<List<Appointment>> getAppointmentsByVet(@Header("Authorization") String token, @Path("vetId") Long vetId);

    @POST("appointments")
    Call<Appointment> createAppointment(@Header("Authorization") String token, @Body CreateAppointmentRequest request);

    @PUT("appointments/{id}")
    Call<Appointment> updateAppointment(@Header("Authorization") String token, @Path("id") Long id,
            @Body UpdateAppointmentRequest request);

    @PATCH("appointments/{id}/confirm")
    Call<Appointment> confirmAppointment(@Header("Authorization") String token, @Path("id") Long id);

    @PATCH("appointments/{id}/cancel")
    Call<Appointment> cancelAppointment(@Header("Authorization") String token, @Path("id") Long id);
}
