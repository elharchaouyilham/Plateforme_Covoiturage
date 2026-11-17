package com.example.covoiturage.mapper;

import com.example.covoiturage.dto.ConducteurRequest;
import com.example.covoiturage.dto.ConducteurResponse;
import com.example.covoiturage.entity.Conducteur;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConducteurMapper {

    Conducteur toEntity(ConducteurRequest request);

    ConducteurResponse toResponse(Conducteur conducteur);

    List<ConducteurResponse> toResponseList(List<Conducteur> conducteurs);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(ConducteurRequest request, @MappingTarget Conducteur conducteur);
}
