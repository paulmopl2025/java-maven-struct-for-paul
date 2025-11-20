package com.example.vetclinic.presentation.controller;

import com.example.vetclinic.application.dto.pet.CreatePetDTO;
import com.example.vetclinic.application.dto.pet.PetDTO;
import com.example.vetclinic.application.dto.pet.UpdatePetDTO;
import com.example.vetclinic.application.service.PetService;
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
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Pets", description = "Pet management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class PetController {

    private final PetService petService;

    @GetMapping("/pets")
    @PreAuthorize("hasAnyRole('ADMIN', 'VET', 'RECEPCIONISTA')")
    @Operation(summary = "Get all pets", description = "Retrieve a list of all registered pets")
    public ResponseEntity<List<PetDTO>> getAllPets() {
        return ResponseEntity.ok(petService.getAllPets());
    }

    @GetMapping("/pets/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VET', 'RECEPCIONISTA')")
    @Operation(summary = "Get pet by ID", description = "Retrieve pet details by ID")
    public ResponseEntity<PetDTO> getPetById(@PathVariable Long id) {
        return ResponseEntity.ok(petService.getPetById(id));
    }

    @GetMapping("/owners/{ownerId}/pets")
    @PreAuthorize("hasAnyRole('ADMIN', 'VET', 'RECEPCIONISTA')")
    @Operation(summary = "Get pets by owner", description = "Retrieve all pets belonging to a specific owner")
    public ResponseEntity<List<PetDTO>> getPetsByOwnerId(@PathVariable Long ownerId) {
        return ResponseEntity.ok(petService.getPetsByOwnerId(ownerId));
    }

    @PostMapping("/pets")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    @Operation(summary = "Create pet", description = "Register a new pet")
    public ResponseEntity<PetDTO> createPet(@Valid @RequestBody CreatePetDTO createPetDTO) {
        return new ResponseEntity<>(petService.createPet(createPetDTO), HttpStatus.CREATED);
    }

    @PutMapping("/pets/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    @Operation(summary = "Update pet", description = "Update pet details")
    public ResponseEntity<PetDTO> updatePet(@PathVariable Long id, @Valid @RequestBody UpdatePetDTO updatePetDTO) {
        return ResponseEntity.ok(petService.updatePet(id, updatePetDTO));
    }

    @DeleteMapping("/pets/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete pet", description = "Delete a pet by ID (Admin only)")
    public ResponseEntity<Void> deletePet(@PathVariable Long id) {
        petService.deletePet(id);
        return ResponseEntity.noContent().build();
    }
}
