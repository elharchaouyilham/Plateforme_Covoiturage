package com.example.covoiturage.repository;

import com.example.covoiturage.entity.Trajet;
import com.example.covoiturage.enums.StatutTrajet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface TrajetRepository extends JpaRepository<Trajet, Long> {

    Page<Trajet> findByConducteurId(Long conducteurId, Pageable pageable);

    boolean existsByVehiculeId(Long vehiculeId);

    @Query("""
            select distinct t
            from Trajet t
            left join fetch t.pointsArret pa
            left join fetch t.conducteur c
            left join fetch t.vehicule v
            where t.id = :id
            """)
    Optional<Trajet> findByIdWithDetails(@Param("id") Long id);

    @Query("""
            select t
            from Trajet t
            join t.pointsArret pm
            join t.pointsArret pd
            where t.statut = :statut
              and lower(pm.ville) = lower(:villeDepart)
              and lower(pd.ville) = lower(:villeArrivee)
              and pm.ordre < pd.ordre
              and cast(pm.heurePassage as date) = :date
              and (pd.prixDepuisDepart - pm.prixDepuisDepart) <= :prixMax
              and (t.placesProposees - coalesce((
                    select sum(r.nombrePlaces)
                    from Reservation r
                    where r.trajet = t and r.statut = com.example.covoiturage.enums.StatutReservation.CONFIRMEE
              ), 0)) >= :placesDemandees
            order by t.dateDepart asc
            """)
    Page<Trajet> rechercherTrajets(@Param("statut") StatutTrajet statut,
                                   @Param("villeDepart") String villeDepart,
                                   @Param("villeArrivee") String villeArrivee,
                                   @Param("date") LocalDate date,
                                   @Param("prixMax") BigDecimal prixMax,
                                   @Param("placesDemandees") int placesDemandees,
                                   Pageable pageable);
}
