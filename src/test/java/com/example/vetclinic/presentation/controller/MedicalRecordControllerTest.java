package com.example.vetclinic.presentation.controller;

import com.example.vetclinic.application.dto.medicalrecord.CreateMedicalRecordDTO;
import com.example.vetclinic.application.dto.medicalrecord.MedicalRecordDTO;
import com.example.vetclinic.application.service.MedicalRecordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MedicalRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MedicalRecordService medicalRecordService;

    private MedicalRecordDTO recordDTO;
    private CreateMedicalRecordDTO createRecordDTO;

    @BeforeEach
    void setUp() {
        recordDTO = new MedicalRecordDTO();
        recordDTO.setId(1L);
        recordDTO.setDiagnosis("Healthy");
        recordDTO.setRecordDate(LocalDateTime.now());

        createRecordDTO = new CreateMedicalRecordDTO();
        createRecordDTO.setPetId(1L);
        createRecordDTO.setVetId(1L);
        createRecordDTO.setDiagnosis("Healthy");
    }

    @Test
    @WithMockUser(roles = "VET")
    void getAllRecords_ShouldReturnList() throws Exception {
        // Given
        when(medicalRecordService.getAllRecords()).thenReturn(Arrays.asList(recordDTO));

        // When & Then
        mockMvc.perform(get("/api/medical-records"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].diagnosis").value("Healthy"));
    }

    @Test
    @WithMockUser(roles = "VET")
    void createRecord_ShouldReturnCreated() throws Exception {
        // Given
        when(medicalRecordService.createRecord(any(CreateMedicalRecordDTO.class)))
                .thenReturn(recordDTO);

        // When & Then
        mockMvc.perform(post("/api/medical-records")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRecordDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "VET")
    void getRecordsByPet_ShouldReturnList() throws Exception {
        // Given
        when(medicalRecordService.getRecordsByPet(1L)).thenReturn(Arrays.asList(recordDTO));

        // When & Then
        mockMvc.perform(get("/api/medical-records/pet/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].diagnosis").value("Healthy"));
    }

    @Test
    void createRecord_ShouldReturn401_WhenNotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/medical-records")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRecordDTO)))
                .andExpect(status().isUnauthorized());
    }
}
