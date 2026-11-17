package com.example.covoiturage.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VehiculeRequest(
        Long conducteurId,

        @NotBlank(message = "La marque est obligatoire")
        String marque,

        @NotBlank(message = "Le modèle est obligatoire")
        String modele,

        @NotBlank(message = "L'immatriculation est obligatoire")
        String immatriculation,

        String couleur,

        @NotNull(message = "La capacité est obligatoire")
        @Min(value = 1, message = "La capacité doit être d'au moins 1 place passager")
        @Max(value = 8, message = "La capacité ne peut pas dépasser 8 places passagers")
        Integer capacite
) {
}
