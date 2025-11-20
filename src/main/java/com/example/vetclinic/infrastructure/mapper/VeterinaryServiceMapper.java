package com.example.vetclinic.infrastructure.mapper;

import com.example.vetclinic.application.dto.service.CreateVeterinaryServiceDTO;
import com.example.vetclinic.application.dto.service.UpdateVeterinaryServiceDTO;
import com.example.vetclinic.application.dto.service.VeterinaryServiceDTO;
import com.example.vetclinic.domain.model.VeterinaryService;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface VeterinaryServiceMapper {

    VeterinaryServiceDTO toDTO(VeterinaryService veterinaryService);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    VeterinaryService toEntity(CreateVeterinaryServiceDTO createDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(UpdateVeterinaryServiceDTO updateDTO, @MappingTarget VeterinaryService veterinaryService);
}
