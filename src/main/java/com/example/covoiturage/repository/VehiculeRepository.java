package com.example.covoiturage.repository;

import com.example.covoiturage.entity.Vehicule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {

    Optional<Vehicule> findByImmatriculation(String immatriculation);

    boolean existsByImmatriculation(String immatriculation);

    List<Vehicule> findByConducteurId(Long conducteurId);

    boolean existsByIdAndConducteurId(Long id, Long conducteurId);
}
