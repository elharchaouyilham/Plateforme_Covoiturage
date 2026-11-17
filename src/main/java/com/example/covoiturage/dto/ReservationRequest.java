package com.example.covoiturage.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReservationRequest(
        @NotNull(message = "Le passager est obligatoire")
        Long passagerId,

        @NotNull(message = "Le trajet est obligatoire")
        Long trajetId,

        @NotNull(message = "Le point de montée est obligatoire")
        Long pointMonteeId,

        @NotNull(message = "Le point de descente est obligatoire")
        Long pointDescenteId,

        @NotNull(message = "Le nombre de places est obligatoire")
        @Min(value = 1, message = "Au moins 1 place doit être réservée")
        Integer nombrePlaces
) {
}
