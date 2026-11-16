package com.example.covoiturage.repository;

import com.example.covoiturage.entity.PointArret;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PointArretRepository extends JpaRepository<PointArret, Long> {

    List<PointArret> findByTrajetIdOrderByOrdreAsc(Long trajetId);

    Optional<PointArret> findByIdAndTrajetId(Long id, Long trajetId);
}
