package com.example.covoiturage.mapper;

import com.example.covoiturage.dto.ReservationRequest;
import com.example.covoiturage.dto.ReservationResponse;
import com.example.covoiturage.entity.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper(
        componentModel = "spring",
        uses = {PassagerMapper.class, PointArretMapper.class}
)
public interface ReservationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passager", ignore = true)
    @Mapping(target = "trajet", ignore = true)
    @Mapping(target = "pointMontee", ignore = true)
    @Mapping(target = "pointDescente", ignore = true)
    @Mapping(target = "montantTotal", ignore = true)
    @Mapping(target = "dateReservation", ignore = true)
    @Mapping(target = "statut", ignore = true)
    Reservation toEntity(ReservationRequest request);

    @Mapping(target = "prixParPlace", expression = "java(prixParPlace(reservation))")
    @Mapping(target = "trajetId", source = "reservation.trajet.id")
    @Mapping(target = "villeDepart", source = "reservation.trajet.villeDepart")
    @Mapping(target = "villeArrivee", source = "reservation.trajet.villeArrivee")
    @Mapping(target = "dateDepart", source = "reservation.trajet.dateDepart")
    ReservationResponse toResponse(Reservation reservation);

    default BigDecimal prixParPlace(Reservation reservation) {
        if (reservation == null
                || reservation.getPointMontee() == null
                || reservation.getPointDescente() == null
                || reservation.getPointMontee().getPrixDepuisDepart() == null
                || reservation.getPointDescente().getPrixDepuisDepart() == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return reservation.getPointDescente().getPrixDepuisDepart()
                .subtract(reservation.getPointMontee().getPrixDepuisDepart())
                .setScale(2, RoundingMode.HALF_UP);
    }
}
