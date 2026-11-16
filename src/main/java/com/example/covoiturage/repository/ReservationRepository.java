package com.example.covoiturage.repository;

import com.example.covoiturage.entity.Reservation;
import com.example.covoiturage.enums.StatutReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByPassagerId(Long passagerId);

    List<Reservation> findByTrajetId(Long trajetId);

    List<Reservation> findByTrajetIdAndStatut(Long trajetId, StatutReservation statut);

    Optional<Reservation> findByPassagerIdAndTrajetIdAndStatut(Long passagerId, Long trajetId, StatutReservation statut);

    boolean existsByPassagerIdAndTrajetIdAndStatut(Long passagerId, Long trajetId, StatutReservation statut);

    @Query("""
            select coalesce(sum(r.nombrePlaces), 0)
            from Reservation r
            where r.trajet.id = :trajetId
              and r.statut = :statut
            """)
    int sumNombrePlacesByTrajetIdAndStatut(@Param("trajetId") Long trajetId,
                                           @Param("statut") StatutReservation statut);
}
