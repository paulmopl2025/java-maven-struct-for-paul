package com.example.vetclinic.infrastructure.mapper;

import com.example.vetclinic.application.dto.pet.CreatePetDTO;
import com.example.vetclinic.application.dto.pet.PetDTO;
import com.example.vetclinic.application.dto.pet.UpdatePetDTO;
import com.example.vetclinic.domain.model.Pet;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface PetMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "owner.firstName", target = "ownerName")
    PetDTO toDTO(Pet pet);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true) // Owner is set in Service
    Pet toEntity(CreatePetDTO createPetDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    void updateEntityFromDTO(UpdatePetDTO updatePetDTO, @MappingTarget Pet pet);
}
