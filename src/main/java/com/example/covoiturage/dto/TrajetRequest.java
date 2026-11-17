package com.example.covoiturage.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record TrajetRequest(
        @NotNull(message = "Le conducteur est obligatoire")
        Long conducteurId,

        @NotNull(message = "Le véhicule est obligatoire")
        Long vehiculeId,

        @NotNull(message = "Le nombre de places proposées est obligatoire")
        @Min(value = 1, message = "Au moins 1 place doit être proposée")
        Integer placesProposees,

        @NotEmpty(message = "Le trajet doit contenir au moins deux points d'arrêt")
        @Valid
        List<PointArretRequest> pointsArret
) {
}
