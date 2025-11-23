package com.example.vetclinic.cli.storage;

import com.example.vetclinic.cli.model.Session;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class StorageService {

    private static final String SESSION_FILE = "session.json";
    private final ObjectMapper objectMapper;
    private final File sessionFile;

    public StorageService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.sessionFile = Paths.get(SESSION_FILE).toFile();
    }

    public void saveSession(Session session) {
        try {
            objectMapper.writeValue(sessionFile, session);
        } catch (IOException e) {
            System.err.println("Error saving session: " + e.getMessage());
        }
    }

    public Session loadSession() {
        if (!sessionFile.exists()) {
            return null;
        }
        try {
            return objectMapper.readValue(sessionFile, Session.class);
        } catch (IOException e) {
            System.err.println("Error loading session: " + e.getMessage());
            return null;
        }
    }

    public void clearSession() {
        if (sessionFile.exists()) {
            sessionFile.delete();
        }
    }
}
