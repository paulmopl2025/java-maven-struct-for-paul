package com.example.vetclinic.presentation.controller;

import com.example.vetclinic.application.dto.service.CreateVeterinaryServiceDTO;
import com.example.vetclinic.application.dto.service.UpdateVeterinaryServiceDTO;
import com.example.vetclinic.application.dto.service.VeterinaryServiceDTO;
import com.example.vetclinic.application.service.VeterinaryServiceService;
import com.example.vetclinic.domain.model.ServiceType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
@Tag(name = "Veterinary Services", description = "Veterinary service management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class VeterinaryServiceController {

    private final VeterinaryServiceService veterinaryServiceService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VET', 'RECEPCIONISTA')")
    @Operation(summary = "Get all active services", description = "Retrieve a list of all active veterinary services")
    public ResponseEntity<List<VeterinaryServiceDTO>> getAllServices() {
        return ResponseEntity.ok(veterinaryServiceService.getAllServices());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VET', 'RECEPCIONISTA')")
    @Operation(summary = "Get service by ID", description = "Retrieve service details by ID")
    public ResponseEntity<VeterinaryServiceDTO> getServiceById(@PathVariable Long id) {
        return ResponseEntity.ok(veterinaryServiceService.getServiceById(id));
    }

    @GetMapping("/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VET', 'RECEPCIONISTA')")
    @Operation(summary = "Get services by type", description = "Retrieve services by service type")
    public ResponseEntity<List<VeterinaryServiceDTO>> getServicesByType(@PathVariable ServiceType type) {
        return ResponseEntity.ok(veterinaryServiceService.getServicesByType(type));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create service", description = "Create a new veterinary service (Admin only)")
    public ResponseEntity<VeterinaryServiceDTO> createService(
            @Valid @RequestBody CreateVeterinaryServiceDTO createDTO) {
        return new ResponseEntity<>(veterinaryServiceService.createService(createDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update service", description = "Update service details (Admin only)")
    public ResponseEntity<VeterinaryServiceDTO> updateService(
            @PathVariable Long id,
            @Valid @RequestBody UpdateVeterinaryServiceDTO updateDTO) {
        return ResponseEntity.ok(veterinaryServiceService.updateService(id, updateDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate service", description = "Soft delete a service by setting it as inactive (Admin only)")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        veterinaryServiceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}
