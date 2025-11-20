package com.example.vetclinic.presentation.controller;

import com.example.vetclinic.application.dto.clinic.ClinicConfigDTO;
import com.example.vetclinic.application.dto.clinic.ClinicDTO;
import com.example.vetclinic.application.dto.clinic.ClinicStatsDTO;
import com.example.vetclinic.application.service.ClinicService;
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

import java.math.BigDecimal;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ClinicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClinicService clinicService;

    private ClinicDTO clinicDTO;
    private ClinicConfigDTO configDTO;
    private ClinicStatsDTO statsDTO;

    @BeforeEach
    void setUp() {
        clinicDTO = new ClinicDTO();
        clinicDTO.setId(1L);
        clinicDTO.setName("Veterinaria Central");
        clinicDTO.setOpeningTime(LocalTime.of(8, 0));
        clinicDTO.setClosingTime(LocalTime.of(18, 0));

        configDTO = new ClinicConfigDTO();
        configDTO.setName("Veterinaria Central");
        configDTO.setAddress("Calle Principal 123");
        configDTO.setPhone("555-1234");
        configDTO.setEmail("info@vetclinic.com");
        configDTO.setOpeningTime(LocalTime.of(8, 0));
        configDTO.setClosingTime(LocalTime.of(18, 0));
        configDTO.setMaxDailyAppointments(20);

        statsDTO = ClinicStatsDTO.builder()
                .totalAppointments(100L)
                .totalPets(50L)
                .totalOwners(30L)
                .estimatedRevenue(new BigDecimal("5000.00"))
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getClinicConfig_ShouldReturnConfig() throws Exception {
        // Given
        when(clinicService.getClinicConfig()).thenReturn(clinicDTO);

        // When & Then
        mockMvc.perform(get("/api/clinic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Veterinaria Central"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateClinicConfig_ShouldReturnUpdated() throws Exception {
        // Given
        when(clinicService.updateClinicConfig(any(ClinicConfigDTO.class))).thenReturn(clinicDTO);

        // When & Then
        mockMvc.perform(put("/api/clinic")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(configDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Veterinaria Central"));
    }

    @Test
    @WithMockUser(roles = "VET")
    void getClinicStats_ShouldReturnStats() throws Exception {
        // Given
        when(clinicService.getClinicStats()).thenReturn(statsDTO);

        // When & Then
        mockMvc.perform(get("/api/clinic/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAppointments").value(100))
                .andExpect(jsonPath("$.totalPets").value(50))
                .andExpect(jsonPath("$.estimatedRevenue").value(5000.00));
    }

    @Test
    void getClinicConfig_ShouldReturn401_WhenNotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/clinic"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "RECEPCIONISTA")
    void updateClinicConfig_ShouldReturn403_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/clinic")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(configDTO)))
                .andExpect(status().isForbidden());
    }
}
