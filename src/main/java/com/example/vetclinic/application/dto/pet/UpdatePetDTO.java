package com.example.vetclinic.application.dto.pet;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdatePetDTO {
    private String name;
    private String species;
    private String breed;
    private LocalDate birthDate;
}
