package com.example.vetclinic.application.service;

import com.example.vetclinic.application.dto.service.CreateVeterinaryServiceDTO;
import com.example.vetclinic.application.dto.service.UpdateVeterinaryServiceDTO;
import com.example.vetclinic.application.dto.service.VeterinaryServiceDTO;
import com.example.vetclinic.domain.model.ServiceType;
import com.example.vetclinic.domain.model.VeterinaryService;
import com.example.vetclinic.infrastructure.mapper.VeterinaryServiceMapper;
import com.example.vetclinic.infrastructure.persistence.VeterinaryServiceJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VeterinaryServiceService {

    private final VeterinaryServiceJpaRepository serviceRepository;
    private final VeterinaryServiceMapper serviceMapper;

    @Transactional(readOnly = true)
    public List<VeterinaryServiceDTO> getAllServices() {
        return serviceRepository.findByActiveTrue().stream()
                .map(serviceMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VeterinaryServiceDTO getServiceById(Long id) {
        VeterinaryService service = serviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service not found with id: " + id));
        return serviceMapper.toDTO(service);
    }

    @Transactional(readOnly = true)
    public List<VeterinaryServiceDTO> getServicesByType(ServiceType serviceType) {
        return serviceRepository.findByServiceType(serviceType).stream()
                .map(serviceMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public VeterinaryServiceDTO createService(CreateVeterinaryServiceDTO createDTO) {
        VeterinaryService service = serviceMapper.toEntity(createDTO);
        VeterinaryService savedService = serviceRepository.save(service);
        return serviceMapper.toDTO(savedService);
    }

    @Transactional
    public VeterinaryServiceDTO updateService(Long id, UpdateVeterinaryServiceDTO updateDTO) {
        VeterinaryService service = serviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service not found with id: " + id));

        serviceMapper.updateEntityFromDTO(updateDTO, service);
        VeterinaryService updatedService = serviceRepository.save(service);
        return serviceMapper.toDTO(updatedService);
    }

    @Transactional
    public void deleteService(Long id) {
        VeterinaryService service = serviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service not found with id: " + id));
        service.setActive(false);
        serviceRepository.save(service);
    }
}
