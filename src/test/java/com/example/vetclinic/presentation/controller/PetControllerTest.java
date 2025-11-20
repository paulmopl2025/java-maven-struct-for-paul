package com.example.vetclinic.presentation.controller;

import com.example.vetclinic.application.dto.pet.CreatePetDTO;
import com.example.vetclinic.application.dto.pet.PetDTO;
import com.example.vetclinic.application.service.PetService;
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

import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PetService petService;

    private PetDTO petDTO;
    private CreatePetDTO createPetDTO;

    @BeforeEach
    void setUp() {
        petDTO = new PetDTO();
        petDTO.setId(1L);
        petDTO.setName("Max");
        petDTO.setSpecies("Perro");

        createPetDTO = new CreatePetDTO();
        createPetDTO.setName("Max");
        createPetDTO.setSpecies("Perro");
        createPetDTO.setBreed("Labrador");
        createPetDTO.setBirthDate(LocalDate.of(2020, 5, 15));
        createPetDTO.setOwnerId(1L);
    }

    @Test
    @WithMockUser(roles = "RECEPCIONISTA")
    void getAllPets_ShouldReturnList() throws Exception {
        // Given
        when(petService.getAllPets()).thenReturn(Arrays.asList(petDTO));

        // When & Then
        mockMvc.perform(get("/api/pets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Max"));
    }

    @Test
    @WithMockUser(roles = "RECEPCIONISTA")
    void createPet_ShouldReturnCreated() throws Exception {
        // Given
        when(petService.createPet(any(CreatePetDTO.class))).thenReturn(petDTO);

        // When & Then
        mockMvc.perform(post("/api/pets")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPetDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Max"));
    }

    @Test
    @WithMockUser(roles = "VET")
    void getPetsByOwner_ShouldReturnList() throws Exception {
        // Given
        when(petService.getPetsByOwnerId(1L)).thenReturn(Arrays.asList(petDTO));

        // When & Then
        mockMvc.perform(get("/api/pets/owner/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Max"));
    }

    @Test
    void createPet_ShouldReturn401_WhenNotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/pets")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPetDTO)))
                .andExpect(status().isUnauthorized());
    }
}
