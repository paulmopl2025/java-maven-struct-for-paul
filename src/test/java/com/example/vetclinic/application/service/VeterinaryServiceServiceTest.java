package com.example.vetclinic.application.service;

import com.example.vetclinic.application.dto.service.CreateVeterinaryServiceDTO;
import com.example.vetclinic.application.dto.service.VeterinaryServiceDTO;
import com.example.vetclinic.domain.model.ServiceType;
import com.example.vetclinic.domain.model.VeterinaryService;
import com.example.vetclinic.infrastructure.mapper.VeterinaryServiceMapper;
import com.example.vetclinic.infrastructure.persistence.VeterinaryServiceJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VeterinaryServiceServiceTest {

    @Mock
    private VeterinaryServiceJpaRepository serviceRepository;

    @Mock
    private VeterinaryServiceMapper serviceMapper;

    @InjectMocks
    private VeterinaryServiceService veterinaryServiceService;

    private VeterinaryService service;
    private VeterinaryServiceDTO serviceDTO;
    private CreateVeterinaryServiceDTO createServiceDTO;

    @BeforeEach
    void setUp() {
        service = VeterinaryService.builder()
                .id(1L)
                .name("Vacunación")
                .serviceType(ServiceType.VACCINATION)
                .baseCost(new BigDecimal("50.00"))
                .estimatedDurationMinutes(15)
                .active(true)
                .build();

        serviceDTO = new VeterinaryServiceDTO();
        serviceDTO.setId(1L);
        serviceDTO.setName("Vacunación");
        serviceDTO.setServiceType(ServiceType.VACCINATION);

        createServiceDTO = new CreateVeterinaryServiceDTO();
        createServiceDTO.setName("Vacunación");
        createServiceDTO.setServiceType(ServiceType.VACCINATION);
        createServiceDTO.setBaseCost(new BigDecimal("50.00"));
    }

    @Test
    void getServiceById_ShouldReturnService_WhenExists() {
        // Given
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));
        when(serviceMapper.toDTO(service)).thenReturn(serviceDTO);

        // When
        VeterinaryServiceDTO result = veterinaryServiceService.getServiceById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Vacunación");
        verify(serviceRepository).findById(1L);
    }

    @Test
    void getServiceById_ShouldThrowException_WhenNotFound() {
        // Given
        when(serviceRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> veterinaryServiceService.getServiceById(999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void createService_ShouldReturnCreated() {
        // Given
        when(serviceMapper.toEntity(createServiceDTO)).thenReturn(service);
        when(serviceRepository.save(any(VeterinaryService.class))).thenReturn(service);
        when(serviceMapper.toDTO(service)).thenReturn(serviceDTO);

        // When
        VeterinaryServiceDTO result = veterinaryServiceService.createService(createServiceDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Vacunación");
        verify(serviceRepository).save(any(VeterinaryService.class));
    }

    @Test
    void getServicesByType_ShouldReturnList() {
        // Given
        when(serviceRepository.findByServiceType(ServiceType.VACCINATION))
                .thenReturn(Arrays.asList(service));
        when(serviceMapper.toDTO(service)).thenReturn(serviceDTO);

        // When
        List<VeterinaryServiceDTO> result = veterinaryServiceService.getServicesByType(ServiceType.VACCINATION);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getServiceType()).isEqualTo(ServiceType.VACCINATION);
        verify(serviceRepository).findByServiceType(ServiceType.VACCINATION);
    }

    @Test
    void deleteService_ShouldDeactivateService() {
        // Given
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));
        when(serviceRepository.save(service)).thenReturn(service);

        // When
        veterinaryServiceService.deleteService(1L);

        // Then
        verify(serviceRepository).findById(1L);
        verify(serviceRepository).save(service);
        assertThat(service.getActive()).isFalse();
    }
}
