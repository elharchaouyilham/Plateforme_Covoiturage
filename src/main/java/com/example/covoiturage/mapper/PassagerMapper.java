package com.example.covoiturage.mapper;

import com.example.covoiturage.dto.PassagerRequest;
import com.example.covoiturage.dto.PassagerResponse;
import com.example.covoiturage.entity.Passager;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PassagerMapper {

    Passager toEntity(PassagerRequest request);

    PassagerResponse toResponse(Passager passager);

    List<PassagerResponse> toResponseList(List<Passager> passagers);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(PassagerRequest request, @MappingTarget Passager passager);
}
