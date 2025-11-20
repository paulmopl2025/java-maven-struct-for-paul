package com.example.vetclinic.presentation.controller;

import com.example.vetclinic.application.dto.service.CreateVeterinaryServiceDTO;
import com.example.vetclinic.application.dto.service.UpdateVeterinaryServiceDTO;
import com.example.vetclinic.application.dto.service.VeterinaryServiceDTO;
import com.example.vetclinic.application.service.VeterinaryServiceService;
import com.example.vetclinic.domain.model.ServiceType;
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
class VeterinaryServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VeterinaryServiceService veterinaryServiceService;

    private VeterinaryServiceDTO serviceDTO;
    private CreateVeterinaryServiceDTO createServiceDTO;
    private UpdateVeterinaryServiceDTO updateServiceDTO;

    @BeforeEach
    void setUp() {
        serviceDTO = new VeterinaryServiceDTO();
        serviceDTO.setId(1L);
        serviceDTO.setName("Vacunación");
        serviceDTO.setDescription("Vacunación general");
        serviceDTO.setServiceType(ServiceType.VACCINATION);
        serviceDTO.setBaseCost(new BigDecimal("50.00"));
        serviceDTO.setEstimatedDurationMinutes(15);

        createServiceDTO = new CreateVeterinaryServiceDTO();
        createServiceDTO.setName("Vacunación");
        createServiceDTO.setDescription("Vacunación general");
        createServiceDTO.setServiceType(ServiceType.VACCINATION);
        createServiceDTO.setBaseCost(new BigDecimal("50.00"));
        createServiceDTO.setEstimatedDurationMinutes(15);

        updateServiceDTO = new UpdateVeterinaryServiceDTO();
        updateServiceDTO.setName("Vacunación Premium");
        updateServiceDTO.setBaseCost(new BigDecimal("75.00"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllServices_ShouldReturnServicesList() throws Exception {
        // Given
        List<VeterinaryServiceDTO> services = Arrays.asList(serviceDTO);
        when(veterinaryServiceService.getAllServices()).thenReturn(services);

        // When & Then
        mockMvc.perform(get("/api/services"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Vacunación"))
                .andExpect(jsonPath("$[0].serviceType").value("VACCINATION"));
    }

    @Test
    @WithMockUser(roles = "VET")
    void getAllServices_ShouldReturnServicesList_ForVetRole() throws Exception {
        // Given
        List<VeterinaryServiceDTO> services = Arrays.asList(serviceDTO);
        when(veterinaryServiceService.getAllServices()).thenReturn(services);

        // When & Then
        mockMvc.perform(get("/api/services"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "RECEPCIONISTA")
    void getAllServices_ShouldReturnServicesList_ForReceptionistRole() throws Exception {
        // Given
        List<VeterinaryServiceDTO> services = Arrays.asList(serviceDTO);
        when(veterinaryServiceService.getAllServices()).thenReturn(services);

        // When & Then
        mockMvc.perform(get("/api/services"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getServiceById_ShouldReturnService() throws Exception {
        // Given
        when(veterinaryServiceService.getServiceById(1L)).thenReturn(serviceDTO);

        // When & Then
        mockMvc.perform(get("/api/services/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Vacunación"))
                .andExpect(jsonPath("$.serviceType").value("VACCINATION"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getServicesByType_ShouldReturnServicesList() throws Exception {
        // Given
        List<VeterinaryServiceDTO> services = Arrays.asList(serviceDTO);
        when(veterinaryServiceService.getServicesByType(ServiceType.VACCINATION)).thenReturn(services);

        // When & Then
        mockMvc.perform(get("/api/services/type/VACCINATION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].serviceType").value("VACCINATION"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createService_ShouldReturnCreatedService() throws Exception {
        // Given
        when(veterinaryServiceService.createService(any(CreateVeterinaryServiceDTO.class))).thenReturn(serviceDTO);

        // When & Then
        mockMvc.perform(post("/api/services")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createServiceDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Vacunación"));
    }

    @Test
    @WithMockUser(roles = "VET")
    void createService_ShouldReturn403_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/services")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createServiceDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateService_ShouldReturnUpdatedService() throws Exception {
        // Given
        serviceDTO.setName("Vacunación Premium");
        serviceDTO.setBaseCost(new BigDecimal("75.00"));
        when(veterinaryServiceService.updateService(eq(1L), any(UpdateVeterinaryServiceDTO.class))).thenReturn(serviceDTO);

        // When & Then
        mockMvc.perform(put("/api/services/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateServiceDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Vacunación Premium"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteService_ShouldReturnNoContent() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/services/1")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "VET")
    void deleteService_ShouldReturn403_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/services/1")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllServices_ShouldReturn401_WhenNotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/services"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createService_ShouldReturn400_WhenRequestIsInvalid() throws Exception {
        // Given
        CreateVeterinaryServiceDTO invalidDTO = new CreateVeterinaryServiceDTO();
        invalidDTO.setName(""); // Empty name
        invalidDTO.setServiceType(null); // Null service type
        invalidDTO.setBaseCost(new BigDecimal("-10.00")); // Negative cost

        // When & Then
        mockMvc.perform(post("/api/services")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }
}

