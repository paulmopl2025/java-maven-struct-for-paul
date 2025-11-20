package com.example.vetclinic.presentation.controller;

import com.example.vetclinic.application.dto.vet.CreateVetDTO;
import com.example.vetclinic.application.dto.vet.UpdateVetDTO;
import com.example.vetclinic.application.dto.vet.VetDTO;
import com.example.vetclinic.application.service.VetService;
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
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class VetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VetService vetService;

    private VetDTO vetDTO;
    private CreateVetDTO createVetDTO;
    private UpdateVetDTO updateVetDTO;

    @BeforeEach
    void setUp() {
        vetDTO = new VetDTO();
        vetDTO.setId(1L);
        vetDTO.setFirstName("María");
        vetDTO.setLastName("González");

        createVetDTO = new CreateVetDTO();
        createVetDTO.setFirstName("María");
        createVetDTO.setLastName("González");
        createVetDTO.setSpecialtyIds(Set.of(1L));

        updateVetDTO = new UpdateVetDTO();
        updateVetDTO.setFirstName("María Elena");
        updateVetDTO.setLastName("González");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllVets_ShouldReturnVetsList() throws Exception {
        // Given
        List<VetDTO> vets = Arrays.asList(vetDTO);
        when(vetService.getAllVets()).thenReturn(vets);

        // When & Then
        mockMvc.perform(get("/api/vets"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("María"))
                .andExpect(jsonPath("$[0].lastName").value("González"));
    }

    @Test
    @WithMockUser(roles = "VET")
    void getAllVets_ShouldReturnVetsList_ForVetRole() throws Exception {
        // Given
        List<VetDTO> vets = Arrays.asList(vetDTO);
        when(vetService.getAllVets()).thenReturn(vets);

        // When & Then
        mockMvc.perform(get("/api/vets"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "RECEPCIONISTA")
    void getAllVets_ShouldReturnVetsList_ForReceptionistRole() throws Exception {
        // Given
        List<VetDTO> vets = Arrays.asList(vetDTO);
        when(vetService.getAllVets()).thenReturn(vets);

        // When & Then
        mockMvc.perform(get("/api/vets"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getVetById_ShouldReturnVet() throws Exception {
        // Given
        when(vetService.getVetById(1L)).thenReturn(vetDTO);

        // When & Then
        mockMvc.perform(get("/api/vets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("María"))
                .andExpect(jsonPath("$.lastName").value("González"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createVet_ShouldReturnCreatedVet() throws Exception {
        // Given
        when(vetService.createVet(any(CreateVetDTO.class))).thenReturn(vetDTO);

        // When & Then
        mockMvc.perform(post("/api/vets")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createVetDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("María"));
    }

    @Test
    @WithMockUser(roles = "VET")
    void createVet_ShouldReturn403_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/vets")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createVetDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateVet_ShouldReturnUpdatedVet() throws Exception {
        // Given
        vetDTO.setFirstName("María Elena");
        when(vetService.updateVet(eq(1L), any(UpdateVetDTO.class))).thenReturn(vetDTO);

        // When & Then
        mockMvc.perform(put("/api/vets/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateVetDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("María Elena"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteVet_ShouldReturnNoContent() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/vets/1")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "VET")
    void deleteVet_ShouldReturn403_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/vets/1")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllVets_ShouldReturn401_WhenNotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/vets"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createVet_ShouldReturn400_WhenRequestIsInvalid() throws Exception {
        // Given
        CreateVetDTO invalidDTO = new CreateVetDTO();
        invalidDTO.setFirstName(""); // Empty first name
        invalidDTO.setLastName(""); // Empty last name

        // When & Then
        mockMvc.perform(post("/api/vets")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }
}

