package com.example.vetclinic.cli.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pet {
    private Long id;
    private String name;
    private String species;
    private String breed;
    private LocalDate birthDate;
    private Long ownerId;
    private String ownerName;
}
