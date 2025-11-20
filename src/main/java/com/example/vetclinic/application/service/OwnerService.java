package com.example.vetclinic.application.service;

import com.example.vetclinic.application.dto.owner.CreateOwnerDTO;
import com.example.vetclinic.application.dto.owner.OwnerDTO;
import com.example.vetclinic.application.dto.owner.UpdateOwnerDTO;
import com.example.vetclinic.domain.model.Owner;
import com.example.vetclinic.infrastructure.mapper.OwnerMapper;
import com.example.vetclinic.infrastructure.persistence.OwnerJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerJpaRepository ownerRepository;
    private final OwnerMapper ownerMapper;

    @Transactional(readOnly = true)
    public List<OwnerDTO> getAllOwners() {
        return ownerRepository.findAll().stream()
                .map(ownerMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OwnerDTO getOwnerById(Long id) {
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Owner not found with id: " + id));
        return ownerMapper.toDTO(owner);
    }

    @Transactional
    public OwnerDTO createOwner(CreateOwnerDTO createOwnerDTO) {
        Owner owner = ownerMapper.toEntity(createOwnerDTO);
        Owner savedOwner = ownerRepository.save(owner);
        return ownerMapper.toDTO(savedOwner);
    }

    @Transactional
    public OwnerDTO updateOwner(Long id, UpdateOwnerDTO updateOwnerDTO) {
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Owner not found with id: " + id));

        ownerMapper.updateEntityFromDTO(updateOwnerDTO, owner);
        Owner updatedOwner = ownerRepository.save(owner);
        return ownerMapper.toDTO(updatedOwner);
    }

    @Transactional
    public void deleteOwner(Long id) {
        if (!ownerRepository.existsById(id)) {
            throw new EntityNotFoundException("Owner not found with id: " + id);
        }
        ownerRepository.deleteById(id);
    }
}
