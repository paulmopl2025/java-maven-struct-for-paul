package com.example.vetclinic.cli.service;

import com.example.vetclinic.cli.client.ApiClient;
import com.example.vetclinic.cli.client.AuthClient;
import com.example.vetclinic.cli.model.LoginRequest;
import com.example.vetclinic.cli.model.LoginResponse;
import com.example.vetclinic.cli.model.Session;
import com.example.vetclinic.cli.storage.StorageService;
import retrofit2.Response;

import java.io.IOException;

public class AuthService {

    private final StorageService storageService;
    private final AuthClient authClient;
    private Session currentSession;

    public AuthService(StorageService storageService) {
        this.storageService = storageService;
        this.authClient = ApiClient.createService(AuthClient.class);
        this.currentSession = storageService.loadSession();
    }

    public boolean isAuthenticated() {
        return currentSession != null && currentSession.getToken() != null;
    }

    public String login(String username, String password) {
        try {
            Response<LoginResponse> response = authClient.login(new LoginRequest(username, password)).execute();
            if (response.isSuccessful() && response.body() != null) {
                LoginResponse body = response.body();
                // Assuming the backend returns token, username and role.
                // If backend only returns token, we might need to decode JWT or fetch user
                // info.
                // For now, let's assume we get the token and we store it.
                // We might need to fetch "me" info if the login response doesn't have it.

                // NOTE: Based on backend implementation, LoginResponse might just have "token".
                // Let's verify the backend LoginResponse structure.

                this.currentSession = new Session(body.getAccessToken(), username, "UNKNOWN"); // Role to be fetched
                storageService.saveSession(currentSession);
                return null; // Success
            } else {
                return "Login failed: " + response.code() + " " + response.message();
            }
        } catch (IOException e) {
            return "Connection error: " + e.getMessage();
        }
    }

    public void logout() {
        storageService.clearSession();
        currentSession = null;
    }

    public Session getSession() {
        return currentSession;
    }
}
