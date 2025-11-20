package com.example.vetclinic.infrastructure.mapper;

import com.example.vetclinic.application.dto.owner.CreateOwnerDTO;
import com.example.vetclinic.application.dto.owner.OwnerDTO;
import com.example.vetclinic.application.dto.owner.UpdateOwnerDTO;
import com.example.vetclinic.domain.model.Owner;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface OwnerMapper {
    OwnerDTO toDTO(Owner owner);

    Owner toEntity(CreateOwnerDTO createOwnerDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(UpdateOwnerDTO updateOwnerDTO, @MappingTarget Owner owner);
}
