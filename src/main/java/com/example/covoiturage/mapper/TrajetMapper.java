package com.example.covoiturage.mapper;

import com.example.covoiturage.dto.TrajetRechercheResponse;
import com.example.covoiturage.dto.TrajetRequest;
import com.example.covoiturage.dto.TrajetResponse;
import com.example.covoiturage.dto.TrajetSummaryResponse;
import com.example.covoiturage.entity.Conducteur;
import com.example.covoiturage.entity.PointArret;
import com.example.covoiturage.entity.Trajet;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;

@Mapper(
        componentModel = "spring",
        uses = {ConducteurMapper.class, VehiculeMapper.class, PointArretMapper.class}
)
public interface TrajetMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "statut", ignore = true)
    @Mapping(target = "conducteur", ignore = true)
    @Mapping(target = "vehicule", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    Trajet toEntity(TrajetRequest request);

    @AfterMapping
    default void lierPointsAuTrajet(@MappingTarget Trajet trajet) {
        if (trajet.getPointsArret() != null) {
            trajet.getPointsArret().forEach(point -> point.setTrajet(trajet));
        }
    }

    @Mapping(target = "id", source = "trajet.id")
    @Mapping(target = "villeDepart", source = "trajet.villeDepart")
    @Mapping(target = "villeArrivee", source = "trajet.villeArrivee")
    @Mapping(target = "dateDepart", source = "trajet.dateDepart")
    @Mapping(target = "placesProposees", source = "trajet.placesProposees")
    @Mapping(target = "placesRestantes", source = "placesRestantes")
    @Mapping(target = "prixTotal", expression = "java(prixTotal(trajet))")
    @Mapping(target = "statut", source = "trajet.statut")
    @Mapping(target = "conducteur", source = "trajet.conducteur")
    @Mapping(target = "vehicule", source = "trajet.vehicule")
    @Mapping(target = "pointsArret", source = "trajet.pointsArret")
    TrajetResponse toResponse(Trajet trajet, int placesRestantes);

    @Mapping(target = "id", source = "trajet.id")
    @Mapping(target = "villeDepart", source = "trajet.villeDepart")
    @Mapping(target = "villeArrivee", source = "trajet.villeArrivee")
    @Mapping(target = "dateDepart", source = "trajet.dateDepart")
    @Mapping(target = "placesProposees", source = "trajet.placesProposees")
    @Mapping(target = "placesRestantes", source = "placesRestantes")
    @Mapping(target = "prixTotal", expression = "java(prixTotal(trajet))")
    @Mapping(target = "statut", source = "trajet.statut")
    @Mapping(target = "conducteurNomComplet", expression = "java(nomComplet(trajet.getConducteur()))")
    @Mapping(target = "vehiculeImmatriculation", source = "trajet.vehicule.immatriculation")
    TrajetSummaryResponse toSummary(Trajet trajet, int placesRestantes);

    @Mapping(target = "trajetId", source = "trajet.id")
    @Mapping(target = "villeDepartTrajet", source = "trajet.villeDepart")
    @Mapping(target = "villeArriveeTrajet", source = "trajet.villeArrivee")
    @Mapping(target = "villeMontee", source = "montee.ville")
    @Mapping(target = "villeDescente", source = "descente.ville")
    @Mapping(target = "dateDepart", source = "trajet.dateDepart")
    @Mapping(target = "heurePassageMontee", source = "montee.heurePassage")
    @Mapping(target = "heurePassageDescente", source = "descente.heurePassage")
    @Mapping(target = "prixSegment", expression = "java(prixSegment(montee, descente))")
    @Mapping(target = "placesProposees", source = "trajet.placesProposees")
    @Mapping(target = "placesRestantes", source = "placesRestantes")
    @Mapping(target = "statut", source = "trajet.statut")
    @Mapping(target = "conducteurNomComplet", expression = "java(nomComplet(trajet == null ? null : trajet.getConducteur()))")
    @Mapping(target = "vehiculeImmatriculation", source = "trajet.vehicule.immatriculation")
    TrajetRechercheResponse toRecherche(Trajet trajet,
                                        PointArret montee,
                                        PointArret descente,
                                        int placesRestantes);

    default BigDecimal prixSegment(PointArret montee, PointArret descente) {
        if (montee == null || descente == null
                || montee.getPrixDepuisDepart() == null
                || descente.getPrixDepuisDepart() == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return descente.getPrixDepuisDepart()
                .subtract(montee.getPrixDepuisDepart())
                .setScale(2, RoundingMode.HALF_UP);
    }

    default BigDecimal prixTotal(Trajet trajet) {
        if (trajet == null || trajet.getPointsArret() == null || trajet.getPointsArret().isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return trajet.getPointsArret().stream()
                .max(Comparator.comparing(PointArret::getOrdre))
                .map(PointArret::getPrixDepuisDepart)
                .orElse(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
    }

    default String nomComplet(Conducteur conducteur) {
        if (conducteur == null) {
            return null;
        }
        return conducteur.getNom() + " " + conducteur.getPrenom();
    }
}
