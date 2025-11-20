package com.example.vetclinic.application.dto.pet;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CreatePetDTO {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Species is required")
    private String species;

    private String breed;

    private LocalDate birthDate;

    @NotNull(message = "Owner ID is required")
    private Long ownerId;
}
