package com.example.covoiturage.dto;

import com.example.covoiturage.enums.StatutReservation;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReservationResponse(
        Long id,
        Integer nombrePlaces,
        BigDecimal prixParPlace,
        BigDecimal montantTotal,
        LocalDateTime dateReservation,
        StatutReservation statut,
        PassagerResponse passager,
        Long trajetId,
        String villeDepart,
        String villeArrivee,
        LocalDateTime dateDepart,
        PointArretResponse pointMontee,
        PointArretResponse pointDescente
) {
}
