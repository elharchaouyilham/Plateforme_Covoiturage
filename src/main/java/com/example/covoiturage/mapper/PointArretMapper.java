package com.example.covoiturage.mapper;

import com.example.covoiturage.dto.PointArretRequest;
import com.example.covoiturage.dto.PointArretResponse;
import com.example.covoiturage.entity.PointArret;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PointArretMapper {

    @Mapping(target = "trajet", ignore = true)
    PointArret toEntity(PointArretRequest request);

    PointArretResponse toResponse(PointArret pointArret);

    List<PointArretResponse> toResponseList(List<PointArret> pointsArret);
}
