package com.example.vetclinic.application.service;

import com.example.vetclinic.application.dto.specialty.CreateSpecialtyDTO;
import com.example.vetclinic.application.dto.specialty.SpecialtyDTO;
import com.example.vetclinic.domain.exception.BusinessException;
import com.example.vetclinic.domain.model.Specialty;
import com.example.vetclinic.infrastructure.mapper.SpecialtyMapper;
import com.example.vetclinic.infrastructure.persistence.SpecialtyJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecialtyServiceTest {

    @Mock
    private SpecialtyJpaRepository specialtyRepository;

    @Mock
    private SpecialtyMapper specialtyMapper;

    @InjectMocks
    private SpecialtyService specialtyService;

    private Specialty specialty;
    private SpecialtyDTO specialtyDTO;
    private CreateSpecialtyDTO createSpecialtyDTO;

    @BeforeEach
    void setUp() {
        specialty = Specialty.builder()
                .id(1L)
                .name("Cirugía")
                .build();

        specialtyDTO = new SpecialtyDTO();
        specialtyDTO.setId(1L);
        specialtyDTO.setName("Cirugía");

        createSpecialtyDTO = new CreateSpecialtyDTO();
        createSpecialtyDTO.setName("Cirugía");
    }

    @Test
    void getSpecialtyById_ShouldReturnSpecialty_WhenExists() {
        // Given
        when(specialtyRepository.findById(1L)).thenReturn(Optional.of(specialty));
        when(specialtyMapper.toDTO(specialty)).thenReturn(specialtyDTO);

        // When
        SpecialtyDTO result = specialtyService.getSpecialtyById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Cirugía");
        verify(specialtyRepository).findById(1L);
    }

    @Test
    void getSpecialtyById_ShouldThrowException_WhenNotFound() {
        // Given
        when(specialtyRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> specialtyService.getSpecialtyById(999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void createSpecialty_ShouldReturnCreated() {
        // Given
        when(specialtyRepository.existsByName("Cirugía")).thenReturn(false);
        when(specialtyMapper.toEntity(createSpecialtyDTO)).thenReturn(specialty);
        when(specialtyRepository.save(specialty)).thenReturn(specialty);
        when(specialtyMapper.toDTO(specialty)).thenReturn(specialtyDTO);

        // When
        SpecialtyDTO result = specialtyService.createSpecialty(createSpecialtyDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Cirugía");
        verify(specialtyRepository).existsByName("Cirugía");
        verify(specialtyRepository).save(specialty);
    }

    @Test
    void createSpecialty_ShouldThrowException_WhenNameExists() {
        // Given
        when(specialtyRepository.existsByName("Cirugía")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> specialtyService.createSpecialty(createSpecialtyDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already exists");
        verify(specialtyRepository).existsByName("Cirugía");
        verify(specialtyRepository, never()).save(any());
    }

    @Test
    void deleteSpecialty_ShouldCallRepository() {
        // Given
        when(specialtyRepository.existsById(1L)).thenReturn(true);
        doNothing().when(specialtyRepository).deleteById(1L);

        // When
        specialtyService.deleteSpecialty(1L);

        // Then
        verify(specialtyRepository).existsById(1L);
        verify(specialtyRepository).deleteById(1L);
    }
}
