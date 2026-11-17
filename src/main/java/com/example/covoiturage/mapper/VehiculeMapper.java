package com.example.covoiturage.mapper;

import com.example.covoiturage.dto.VehiculeRequest;
import com.example.covoiturage.dto.VehiculeResponse;
import com.example.covoiturage.entity.Vehicule;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VehiculeMapper {

    @Mapping(target = "conducteur", ignore = true)
    Vehicule toEntity(VehiculeRequest request);

    @Mapping(target = "conducteurId", source = "conducteur.id")
    VehiculeResponse toResponse(Vehicule vehicule);

    List<VehiculeResponse> toResponseList(List<Vehicule> vehicules);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "conducteur", ignore = true)
    void updateEntity(VehiculeRequest request, @MappingTarget Vehicule vehicule);
}
