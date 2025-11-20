package com.example.vetclinic.presentation.controller;

import com.example.vetclinic.application.dto.owner.CreateOwnerDTO;
import com.example.vetclinic.application.dto.owner.OwnerDTO;
import com.example.vetclinic.application.dto.owner.UpdateOwnerDTO;
import com.example.vetclinic.application.service.OwnerService;
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
@RequestMapping("/api/owners")
@RequiredArgsConstructor
@Tag(name = "Owners", description = "Client (Pet Owner) management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class OwnerController {

    private final OwnerService ownerService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VET', 'RECEPCIONISTA')")
    @Operation(summary = "Get all owners", description = "Retrieve a list of all registered owners")
    public ResponseEntity<List<OwnerDTO>> getAllOwners() {
        return ResponseEntity.ok(ownerService.getAllOwners());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VET', 'RECEPCIONISTA')")
    @Operation(summary = "Get owner by ID", description = "Retrieve owner details by ID")
    public ResponseEntity<OwnerDTO> getOwnerById(@PathVariable Long id) {
        return ResponseEntity.ok(ownerService.getOwnerById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    @Operation(summary = "Create owner", description = "Register a new owner")
    public ResponseEntity<OwnerDTO> createOwner(@Valid @RequestBody CreateOwnerDTO createOwnerDTO) {
        return new ResponseEntity<>(ownerService.createOwner(createOwnerDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    @Operation(summary = "Update owner", description = "Update owner details")
    public ResponseEntity<OwnerDTO> updateOwner(@PathVariable Long id,
            @Valid @RequestBody UpdateOwnerDTO updateOwnerDTO) {
        return ResponseEntity.ok(ownerService.updateOwner(id, updateOwnerDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete owner", description = "Delete an owner by ID (Admin only)")
    public ResponseEntity<Void> deleteOwner(@PathVariable Long id) {
        ownerService.deleteOwner(id);
        return ResponseEntity.noContent().build();
    }
}
