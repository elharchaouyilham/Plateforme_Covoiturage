package com.example.covoiturage.dto;

import com.example.covoiturage.enums.StatutTrajet;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TrajetSummaryResponse(
        Long id,
        String villeDepart,
        String villeArrivee,
        LocalDateTime dateDepart,
        Integer placesProposees,
        Integer placesRestantes,
        BigDecimal prixTotal,
        StatutTrajet statut,
        String conducteurNomComplet,
        String vehiculeImmatriculation
) {
}
