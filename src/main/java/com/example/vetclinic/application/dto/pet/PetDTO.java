package com.example.vetclinic.application.dto.pet;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PetDTO {
    private Long id;
    private String name;
    private String species;
    private String breed;
    private LocalDate birthDate;
    private Long ownerId;
    private String ownerName;
}
