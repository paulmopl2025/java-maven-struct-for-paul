package com.example.vetclinic.presentation.controller;

import com.example.vetclinic.application.dto.specialty.CreateSpecialtyDTO;
import com.example.vetclinic.application.dto.specialty.SpecialtyDTO;
import com.example.vetclinic.application.dto.specialty.UpdateSpecialtyDTO;
import com.example.vetclinic.application.service.SpecialtyService;
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

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SpecialtyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SpecialtyService specialtyService;

    private SpecialtyDTO specialtyDTO;
    private CreateSpecialtyDTO createSpecialtyDTO;
    private UpdateSpecialtyDTO updateSpecialtyDTO;

    @BeforeEach
    void setUp() {
        specialtyDTO = new SpecialtyDTO();
        specialtyDTO.setId(1L);
        specialtyDTO.setName("Cirugía");

        createSpecialtyDTO = new CreateSpecialtyDTO();
        createSpecialtyDTO.setName("Cirugía");

        updateSpecialtyDTO = new UpdateSpecialtyDTO();
        updateSpecialtyDTO.setName("Cirugía General");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllSpecialties_ShouldReturnSpecialtiesList() throws Exception {
        // Given
        List<SpecialtyDTO> specialties = Arrays.asList(specialtyDTO);
        when(specialtyService.getAllSpecialties()).thenReturn(specialties);

        // When & Then
        mockMvc.perform(get("/api/specialties"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Cirugía"));
    }

    @Test
    @WithMockUser(roles = "VET")
    void getAllSpecialties_ShouldReturnSpecialtiesList_ForVetRole() throws Exception {
        // Given
        List<SpecialtyDTO> specialties = Arrays.asList(specialtyDTO);
        when(specialtyService.getAllSpecialties()).thenReturn(specialties);

        // When & Then
        mockMvc.perform(get("/api/specialties"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getSpecialtyById_ShouldReturnSpecialty() throws Exception {
        // Given
        when(specialtyService.getSpecialtyById(1L)).thenReturn(specialtyDTO);

        // When & Then
        mockMvc.perform(get("/api/specialties/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Cirugía"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createSpecialty_ShouldReturnCreatedSpecialty() throws Exception {
        // Given
        when(specialtyService.createSpecialty(any(CreateSpecialtyDTO.class))).thenReturn(specialtyDTO);

        // When & Then
        mockMvc.perform(post("/api/specialties")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createSpecialtyDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Cirugía"));
    }

    @Test
    @WithMockUser(roles = "VET")
    void createSpecialty_ShouldReturn403_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/specialties")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createSpecialtyDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateSpecialty_ShouldReturnUpdatedSpecialty() throws Exception {
        // Given
        specialtyDTO.setName("Cirugía General");
        when(specialtyService.updateSpecialty(eq(1L), any(UpdateSpecialtyDTO.class))).thenReturn(specialtyDTO);

        // When & Then
        mockMvc.perform(put("/api/specialties/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateSpecialtyDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cirugía General"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteSpecialty_ShouldReturnNoContent() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/specialties/1")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "VET")
    void deleteSpecialty_ShouldReturn403_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/specialties/1")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllSpecialties_ShouldReturn401_WhenNotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/specialties"))
                .andExpect(status().isUnauthorized());
    }
}

