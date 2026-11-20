package com.example.covoiturage.service;

import com.example.covoiturage.dto.PassagerResponse;
import com.example.covoiturage.dto.PointArretResponse;
import com.example.covoiturage.dto.ReservationRequest;
import com.example.covoiturage.dto.ReservationResponse;
import com.example.covoiturage.entity.Passager;
import com.example.covoiturage.entity.PointArret;
import com.example.covoiturage.entity.Reservation;
import com.example.covoiturage.entity.Trajet;
import com.example.covoiturage.enums.StatutReservation;
import com.example.covoiturage.exception.MauvaisSensException;
import com.example.covoiturage.exception.PassagerDejaReserveException;
import com.example.covoiturage.exception.PlacesInsuffisantesException;
import com.example.covoiturage.exception.PointArretHorsTrajetException;
import com.example.covoiturage.exception.ResourceNotFoundException;
import com.example.covoiturage.repository.PassagerRepository;
import com.example.covoiturage.repository.PointArretRepository;
import com.example.covoiturage.repository.ReservationRepository;
import com.example.covoiturage.repository.TrajetRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final PassagerRepository passagerRepository;
    private final TrajetRepository trajetRepository;
    private final PointArretRepository pointArretRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              PassagerRepository passagerRepository,
                              TrajetRepository trajetRepository,
                              PointArretRepository pointArretRepository) {
        this.reservationRepository = reservationRepository;
        this.passagerRepository = passagerRepository;
        this.trajetRepository = trajetRepository;
        this.pointArretRepository = pointArretRepository;
    }

    public ReservationResponse creer(ReservationRequest request) {
        Passager passager = passagerRepository.findById(request.passagerId()).orElse(null);
        if (passager == null) {
            throw new ResourceNotFoundException("Passager introuvable : id=" + request.passagerId());
        }

        Trajet trajet = trajetRepository.findById(request.trajetId()).orElse(null);
        if (trajet == null) {
            throw new ResourceNotFoundException("Trajet introuvable : id=" + request.trajetId());
        }

        PointArret pointMontee = pointArretRepository.findById(request.pointMonteeId()).orElse(null);
        if (pointMontee == null) {
            throw new ResourceNotFoundException("Point d'arrêt introuvable : id=" + request.pointMonteeId());
        }

        PointArret pointDescente = pointArretRepository.findById(request.pointDescenteId()).orElse(null);
        if (pointDescente == null) {
            throw new ResourceNotFoundException("Point d'arrêt introuvable : id=" + request.pointDescenteId());
        }

        if (!pointMontee.getTrajet().getId().equals(trajet.getId())) {
            throw new PointArretHorsTrajetException(pointMontee.getId(), trajet.getId());
        }
        if (!pointDescente.getTrajet().getId().equals(trajet.getId())) {
            throw new PointArretHorsTrajetException(pointDescente.getId(), trajet.getId());
        }
        if (pointMontee.getOrdre() >= pointDescente.getOrdre()) {
            throw new MauvaisSensException();
        }

        if (reservationRepository.existsByPassagerIdAndTrajetIdAndStatut(
                passager.getId(), trajet.getId(), StatutReservation.CONFIRMEE)) {
            throw new PassagerDejaReserveException(trajet.getId());
        }

        int placesReservees = reservationRepository.sumNombrePlacesByTrajetIdAndStatut(
                trajet.getId(), StatutReservation.CONFIRMEE);
        int placesRestantes = trajet.getPlacesProposees() - placesReservees;
        if (request.nombrePlaces() > placesRestantes) {
            throw new PlacesInsuffisantesException(request.nombrePlaces(), placesRestantes);
        }

        BigDecimal prixParPlace = pointDescente.getPrixDepuisDepart()
                .subtract(pointMontee.getPrixDepuisDepart());
        BigDecimal montantTotal = prixParPlace.multiply(BigDecimal.valueOf(request.nombrePlaces()));

        Reservation reservation = new Reservation();
        reservation.setPassager(passager);
        reservation.setTrajet(trajet);
        reservation.setPointMontee(pointMontee);
        reservation.setPointDescente(pointDescente);
        reservation.setNombrePlaces(request.nombrePlaces());
        reservation.setMontantTotal(montantTotal);
        reservation.setDateReservation(LocalDateTime.now());
        reservation.setStatut(StatutReservation.CONFIRMEE);

        return toResponse(reservationRepository.save(reservation));
    }

    public ReservationResponse obtenirParId(Long id) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation == null) {
            throw new ResourceNotFoundException("Réservation introuvable : id=" + id);
        }
        return toResponse(reservation);
    }

    public List<ReservationResponse> lister() {
        return reservationRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ReservationResponse> listerParPassager(Long passagerId) {
        if (!passagerRepository.existsById(passagerId)) {
            throw new ResourceNotFoundException("Passager introuvable : id=" + passagerId);
        }
        return reservationRepository.findByPassagerId(passagerId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ReservationResponse> listerParTrajet(Long trajetId) {
        if (!trajetRepository.existsById(trajetId)) {
            throw new ResourceNotFoundException("Trajet introuvable : id=" + trajetId);
        }
        return reservationRepository.findByTrajetId(trajetId).stream()
                .map(this::toResponse)
                .toList();
    }

    public ReservationResponse annuler(Long id) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation == null) {
            throw new ResourceNotFoundException("Réservation introuvable : id=" + id);
        }
        reservation.setStatut(StatutReservation.ANNULEE);
        return toResponse(reservationRepository.save(reservation));
    }

    private ReservationResponse toResponse(Reservation reservation) {
        PointArret montee = reservation.getPointMontee();
        PointArret descente = reservation.getPointDescente();
        BigDecimal prixParPlace = descente.getPrixDepuisDepart().subtract(montee.getPrixDepuisDepart());

        return new ReservationResponse(
                reservation.getId(),
                reservation.getNombrePlaces(),
                prixParPlace,
                reservation.getMontantTotal(),
                reservation.getDateReservation(),
                reservation.getStatut(),
                toPassagerResponse(reservation.getPassager()),
                reservation.getTrajet().getId(),
                reservation.getTrajet().getVilleDepart(),
                reservation.getTrajet().getVilleArrivee(),
                reservation.getTrajet().getDateDepart(),
                toPointResponse(montee),
                toPointResponse(descente)
        );
    }

    private PassagerResponse toPassagerResponse(Passager passager) {
        return new PassagerResponse(
                passager.getId(),
                passager.getNom(),
                passager.getPrenom(),
                passager.getEmail(),
                passager.getTelephone()
        );
    }

    private PointArretResponse toPointResponse(PointArret point) {
        return new PointArretResponse(
                point.getId(),
                point.getVille(),
                point.getOrdre(),
                point.getHeurePassage(),
                point.getPrixDepuisDepart()
        );
    }
}
