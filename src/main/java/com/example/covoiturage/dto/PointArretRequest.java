package com.example.covoiturage.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PointArretRequest(
        @NotBlank(message = "La ville est obligatoire")
        String ville,

        @NotNull(message = "L'ordre est obligatoire")
        @Min(value = 0, message = "L'ordre doit être >= 0")
        Integer ordre,

        @NotNull(message = "L'heure de passage est obligatoire")
        LocalDateTime heurePassage,

        @NotNull(message = "Le prix depuis le départ est obligatoire")
        @DecimalMin(value = "0.00", message = "Le prix depuis le départ doit être >= 0")
        BigDecimal prixDepuisDepart
) {
}
