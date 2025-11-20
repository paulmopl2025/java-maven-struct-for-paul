package com.example.vetclinic.application.service;

import com.example.vetclinic.application.dto.pet.CreatePetDTO;
import com.example.vetclinic.application.dto.pet.PetDTO;
import com.example.vetclinic.application.dto.pet.UpdatePetDTO;
import com.example.vetclinic.domain.model.Owner;
import com.example.vetclinic.domain.model.Pet;
import com.example.vetclinic.infrastructure.mapper.PetMapper;
import com.example.vetclinic.infrastructure.persistence.OwnerJpaRepository;
import com.example.vetclinic.infrastructure.persistence.PetJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetJpaRepository petRepository;
    private final OwnerJpaRepository ownerRepository;
    private final PetMapper petMapper;

    @Transactional(readOnly = true)
    public List<PetDTO> getAllPets() {
        return petRepository.findAll().stream()
                .map(petMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PetDTO getPetById(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found with id: " + id));
        return petMapper.toDTO(pet);
    }

    @Transactional(readOnly = true)
    public List<PetDTO> getPetsByOwnerId(Long ownerId) {
        if (!ownerRepository.existsById(ownerId)) {
            throw new EntityNotFoundException("Owner not found with id: " + ownerId);
        }
        return petRepository.findByOwnerId(ownerId).stream()
                .map(petMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PetDTO createPet(CreatePetDTO createPetDTO) {
        Owner owner = ownerRepository.findById(createPetDTO.getOwnerId())
                .orElseThrow(
                        () -> new EntityNotFoundException("Owner not found with id: " + createPetDTO.getOwnerId()));

        Pet pet = petMapper.toEntity(createPetDTO);
        pet.setOwner(owner);

        Pet savedPet = petRepository.save(pet);
        return petMapper.toDTO(savedPet);
    }

    @Transactional
    public PetDTO updatePet(Long id, UpdatePetDTO updatePetDTO) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found with id: " + id));

        petMapper.updateEntityFromDTO(updatePetDTO, pet);
        Pet updatedPet = petRepository.save(pet);
        return petMapper.toDTO(updatedPet);
    }

    @Transactional
    public void deletePet(Long id) {
        if (!petRepository.existsById(id)) {
            throw new EntityNotFoundException("Pet not found with id: " + id);
        }
        petRepository.deleteById(id);
    }
}
