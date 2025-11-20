package com.example.vetclinic.application.service;

import com.example.vetclinic.application.dto.medicalrecord.CreateMedicalRecordDTO;
import com.example.vetclinic.application.dto.medicalrecord.MedicalRecordDTO;
import com.example.vetclinic.domain.model.MedicalRecord;
import com.example.vetclinic.domain.model.Pet;
import com.example.vetclinic.domain.model.Vet;
import com.example.vetclinic.infrastructure.mapper.MedicalRecordMapper;
import com.example.vetclinic.infrastructure.persistence.MedicalRecordJpaRepository;
import com.example.vetclinic.infrastructure.persistence.PetJpaRepository;
import com.example.vetclinic.infrastructure.persistence.VetJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalRecordServiceTest {

    @Mock
    private MedicalRecordJpaRepository medicalRecordRepository;

    @Mock
    private PetJpaRepository petRepository;

    @Mock
    private VetJpaRepository vetRepository;

    @Mock
    private MedicalRecordMapper medicalRecordMapper;

    @InjectMocks
    private MedicalRecordService medicalRecordService;

    private MedicalRecord medicalRecord;
    private MedicalRecordDTO medicalRecordDTO;
    private CreateMedicalRecordDTO createMedicalRecordDTO;
    private Pet pet;
    private Vet vet;

    @BeforeEach
    void setUp() {
        pet = Pet.builder()
                .id(1L)
                .name("Max")
                .build();

        vet = Vet.builder()
                .id(1L)
                .firstName("María")
                .lastName("González")
                .build();

        medicalRecord = MedicalRecord.builder()
                .id(1L)
                .recordDate(LocalDateTime.now())
                .diagnosis("Healthy")
                .treatment("None")
                .weight(new BigDecimal("25.5"))
                .temperature(new BigDecimal("38.5"))
                .pet(pet)
                .vet(vet)
                .build();

        medicalRecordDTO = new MedicalRecordDTO();
        medicalRecordDTO.setId(1L);
        medicalRecordDTO.setDiagnosis("Healthy");

        createMedicalRecordDTO = new CreateMedicalRecordDTO();
        createMedicalRecordDTO.setPetId(1L);
        createMedicalRecordDTO.setVetId(1L);
        createMedicalRecordDTO.setDiagnosis("Healthy");
        createMedicalRecordDTO.setTreatment("None");
    }

    @Test
    void getRecordById_ShouldReturnRecord_WhenExists() {
        // Given
        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.of(medicalRecord));
        when(medicalRecordMapper.toDTO(medicalRecord)).thenReturn(medicalRecordDTO);

        // When
        MedicalRecordDTO result = medicalRecordService.getRecordById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(medicalRecordRepository).findById(1L);
    }

    @Test
    void getRecordById_ShouldThrowException_WhenNotFound() {
        // Given
        when(medicalRecordRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> medicalRecordService.getRecordById(999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void createRecord_ShouldReturnCreated() {
        // Given
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        when(vetRepository.findById(1L)).thenReturn(Optional.of(vet));
        when(medicalRecordMapper.toEntity(createMedicalRecordDTO)).thenReturn(medicalRecord);
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(medicalRecord);
        when(medicalRecordMapper.toDTO(medicalRecord)).thenReturn(medicalRecordDTO);

        // When
        MedicalRecordDTO result = medicalRecordService.createRecord(createMedicalRecordDTO);

        // Then
        assertThat(result).isNotNull();
        verify(petRepository).findById(1L);
        verify(vetRepository).findById(1L);
        verify(medicalRecordRepository).save(any(MedicalRecord.class));
    }

    @Test
    void getRecordsByPet_ShouldReturnList() {
        // Given
        when(petRepository.existsById(1L)).thenReturn(true);
        when(medicalRecordRepository.findByPetIdOrderByRecordDateDesc(1L)).thenReturn(Arrays.asList(medicalRecord));
        when(medicalRecordMapper.toDTO(medicalRecord)).thenReturn(medicalRecordDTO);

        // When
        List<MedicalRecordDTO> result = medicalRecordService.getRecordsByPet(1L);

        // Then
        assertThat(result).hasSize(1);
        verify(medicalRecordRepository).findByPetIdOrderByRecordDateDesc(1L);
    }

    @Test
    void deleteRecord_ShouldCallRepository() {
        // Given
        when(medicalRecordRepository.existsById(1L)).thenReturn(true);
        doNothing().when(medicalRecordRepository).deleteById(1L);

        // When
        medicalRecordService.deleteRecord(1L);

        // Then
        verify(medicalRecordRepository).existsById(1L);
        verify(medicalRecordRepository).deleteById(1L);
    }
}
