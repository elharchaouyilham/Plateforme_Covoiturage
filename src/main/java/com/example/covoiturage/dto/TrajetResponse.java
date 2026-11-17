package com.example.covoiturage.dto;

import com.example.covoiturage.enums.StatutTrajet;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record TrajetResponse(
        Long id,
        String villeDepart,
        String villeArrivee,
        LocalDateTime dateDepart,
        Integer placesProposees,
        Integer placesRestantes,
        BigDecimal prixTotal,
        StatutTrajet statut,
        ConducteurResponse conducteur,
        VehiculeResponse vehicule,
        List<PointArretResponse> pointsArret
) {
}
