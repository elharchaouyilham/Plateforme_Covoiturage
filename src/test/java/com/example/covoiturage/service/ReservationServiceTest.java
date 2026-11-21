package com.example.covoiturage.service;

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
import com.example.covoiturage.exception.ResourceNotFoundException;
import com.example.covoiturage.repository.PassagerRepository;
import com.example.covoiturage.repository.PointArretRepository;
import com.example.covoiturage.repository.ReservationRepository;
import com.example.covoiturage.repository.TrajetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private PassagerRepository passagerRepository;

    @Mock
    private TrajetRepository trajetRepository;

    @Mock
    private PointArretRepository pointArretRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    void creer_retourneReservation() {
        Passager passager = passager();
        Trajet trajet = trajet();
        PointArret montee = point(1L, "Casablanca", 0, BigDecimal.ZERO, trajet);
        PointArret descente = point(2L, "Rabat", 1, new BigDecimal("50.00"), trajet);
        ReservationRequest request = new ReservationRequest(1L, 10L, 1L, 2L, 2);

        when(passagerRepository.findById(1L)).thenReturn(Optional.of(passager));
        when(trajetRepository.findById(10L)).thenReturn(Optional.of(trajet));
        when(pointArretRepository.findById(1L)).thenReturn(Optional.of(montee));
        when(pointArretRepository.findById(2L)).thenReturn(Optional.of(descente));
        when(reservationRepository.existsByPassagerIdAndTrajetIdAndStatut(1L, 10L, StatutReservation.CONFIRMEE))
                .thenReturn(false);
        when(reservationRepository.sumNombrePlacesByTrajetIdAndStatut(10L, StatutReservation.CONFIRMEE))
                .thenReturn(0);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation reservation = invocation.getArgument(0);
            reservation.setId(100L);
            return reservation;
        });

        ReservationResponse response = reservationService.creer(request);

        assertEquals(100L, response.id());
        assertEquals(new BigDecimal("50.00"), response.prixParPlace());
        assertEquals(new BigDecimal("100.00"), response.montantTotal());
        assertEquals(StatutReservation.CONFIRMEE, response.statut());
    }

    @Test
    void creer_mauvaisSens_declencheException() {
        Passager passager = passager();
        Trajet trajet = trajet();
        PointArret montee = point(1L, "Rabat", 1, new BigDecimal("50.00"), trajet);
        PointArret descente = point(2L, "Casablanca", 0, BigDecimal.ZERO, trajet);
        ReservationRequest request = new ReservationRequest(1L, 10L, 1L, 2L, 1);

        when(passagerRepository.findById(1L)).thenReturn(Optional.of(passager));
        when(trajetRepository.findById(10L)).thenReturn(Optional.of(trajet));
        when(pointArretRepository.findById(1L)).thenReturn(Optional.of(montee));
        when(pointArretRepository.findById(2L)).thenReturn(Optional.of(descente));

        assertThrows(MauvaisSensException.class, () -> reservationService.creer(request));
    }

    @Test
    void creer_placesInsuffisantes_declencheException() {
        Passager passager = passager();
        Trajet trajet = trajet();
        PointArret montee = point(1L, "Casablanca", 0, BigDecimal.ZERO, trajet);
        PointArret descente = point(2L, "Rabat", 1, new BigDecimal("50.00"), trajet);
        ReservationRequest request = new ReservationRequest(1L, 10L, 1L, 2L, 3);

        when(passagerRepository.findById(1L)).thenReturn(Optional.of(passager));
        when(trajetRepository.findById(10L)).thenReturn(Optional.of(trajet));
        when(pointArretRepository.findById(1L)).thenReturn(Optional.of(montee));
        when(pointArretRepository.findById(2L)).thenReturn(Optional.of(descente));
        when(reservationRepository.existsByPassagerIdAndTrajetIdAndStatut(1L, 10L, StatutReservation.CONFIRMEE))
                .thenReturn(false);
        when(reservationRepository.sumNombrePlacesByTrajetIdAndStatut(10L, StatutReservation.CONFIRMEE))
                .thenReturn(2);

        assertThrows(PlacesInsuffisantesException.class, () -> reservationService.creer(request));
    }

    @Test
    void creer_dejaReservee_declencheException() {
        Passager passager = passager();
        Trajet trajet = trajet();
        PointArret montee = point(1L, "Casablanca", 0, BigDecimal.ZERO, trajet);
        PointArret descente = point(2L, "Rabat", 1, new BigDecimal("50.00"), trajet);
        ReservationRequest request = new ReservationRequest(1L, 10L, 1L, 2L, 1);

        when(passagerRepository.findById(1L)).thenReturn(Optional.of(passager));
        when(trajetRepository.findById(10L)).thenReturn(Optional.of(trajet));
        when(pointArretRepository.findById(1L)).thenReturn(Optional.of(montee));
        when(pointArretRepository.findById(2L)).thenReturn(Optional.of(descente));
        when(reservationRepository.existsByPassagerIdAndTrajetIdAndStatut(1L, 10L, StatutReservation.CONFIRMEE))
                .thenReturn(true);

        assertThrows(PassagerDejaReserveException.class, () -> reservationService.creer(request));
    }

    @Test
    void annuler_changeStatut() {
        Passager passager = passager();
        Trajet trajet = trajet();
        PointArret montee = point(1L, "Casablanca", 0, BigDecimal.ZERO, trajet);
        PointArret descente = point(2L, "Rabat", 1, new BigDecimal("50.00"), trajet);

        Reservation reservation = new Reservation();
        reservation.setId(100L);
        reservation.setPassager(passager);
        reservation.setTrajet(trajet);
        reservation.setPointMontee(montee);
        reservation.setPointDescente(descente);
        reservation.setNombrePlaces(1);
        reservation.setMontantTotal(new BigDecimal("50.00"));
        reservation.setDateReservation(LocalDateTime.now());
        reservation.setStatut(StatutReservation.CONFIRMEE);

        when(reservationRepository.findById(100L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReservationResponse response = reservationService.annuler(100L);

        assertEquals(StatutReservation.ANNULEE, response.statut());
    }

    @Test
    void obtenirParId_introuvable_declencheException() {
        when(reservationRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reservationService.obtenirParId(9L));
    }

    private Passager passager() {
        Passager passager = new Passager();
        passager.setId(1L);
        passager.setNom("Martin");
        passager.setPrenom("Alice");
        passager.setEmail("alice@mail.com");
        passager.setTelephone("0611111111");
        return passager;
    }

    private Trajet trajet() {
        Trajet trajet = new Trajet();
        trajet.setId(10L);
        trajet.setVilleDepart("Casablanca");
        trajet.setVilleArrivee("Rabat");
        trajet.setDateDepart(LocalDateTime.now().plusDays(1));
        trajet.setPlacesProposees(3);
        return trajet;
    }

    private PointArret point(Long id, String ville, int ordre, BigDecimal prix, Trajet trajet) {
        PointArret point = new PointArret();
        point.setId(id);
        point.setVille(ville);
        point.setOrdre(ordre);
        point.setHeurePassage(LocalDateTime.now().plusHours(ordre));
        point.setPrixDepuisDepart(prix);
        point.setTrajet(trajet);
        return point;
    }
}
