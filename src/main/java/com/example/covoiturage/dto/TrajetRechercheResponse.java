package com.example.covoiturage.dto;

import com.example.covoiturage.enums.StatutTrajet;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TrajetRechercheResponse(
        Long trajetId,
        String villeDepartTrajet,
        String villeArriveeTrajet,
        String villeMontee,
        String villeDescente,
        LocalDateTime dateDepart,
        LocalDateTime heurePassageMontee,
        LocalDateTime heurePassageDescente,
        BigDecimal prixSegment,
        Integer placesProposees,
        Integer placesRestantes,
        StatutTrajet statut,
        String conducteurNomComplet,
        String vehiculeImmatriculation
) {
}
