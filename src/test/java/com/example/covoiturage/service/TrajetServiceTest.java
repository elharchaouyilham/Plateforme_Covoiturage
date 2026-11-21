package com.example.covoiturage.service;

import com.example.covoiturage.dto.PointArretRequest;
import com.example.covoiturage.dto.TrajetRequest;
import com.example.covoiturage.dto.TrajetResponse;
import com.example.covoiturage.entity.Conducteur;
import com.example.covoiturage.entity.Trajet;
import com.example.covoiturage.entity.Vehicule;
import com.example.covoiturage.enums.StatutReservation;
import com.example.covoiturage.exception.CapaciteVehiculeDepasseeException;
import com.example.covoiturage.exception.ResourceNotFoundException;
import com.example.covoiturage.repository.ConducteurRepository;
import com.example.covoiturage.repository.ReservationRepository;
import com.example.covoiturage.repository.TrajetRepository;
import com.example.covoiturage.repository.VehiculeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrajetServiceTest {

    @Mock
    private TrajetRepository trajetRepository;

    @Mock
    private ConducteurRepository conducteurRepository;

    @Mock
    private VehiculeRepository vehiculeRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private TrajetService trajetService;

    @Test
    void creer_retourneTrajet() {
        Conducteur conducteur = new Conducteur();
        conducteur.setId(1L);
        conducteur.setNom("Dupont");
        conducteur.setPrenom("Jean");
        conducteur.setEmail("jean@mail.com");
        conducteur.setTelephone("0600000000");
        conducteur.setNumeroPermis("PERM123");

        Vehicule vehicule = new Vehicule();
        vehicule.setId(2L);
        vehicule.setMarque("Peugeot");
        vehicule.setModele("208");
        vehicule.setImmatriculation("AA-123-AA");
        vehicule.setCouleur("Noir");
        vehicule.setCapacite(4);
        vehicule.setConducteur(conducteur);

        TrajetRequest request = new TrajetRequest(1L, 2L, 3, List.of(
                new PointArretRequest("Casablanca", 0, LocalDateTime.now(), BigDecimal.ZERO),
                new PointArretRequest("Rabat", 1, LocalDateTime.now().plusHours(1), new BigDecimal("50.00"))
        ));

        when(conducteurRepository.findById(1L)).thenReturn(Optional.of(conducteur));
        when(vehiculeRepository.findById(2L)).thenReturn(Optional.of(vehicule));
        when(trajetRepository.save(any(Trajet.class))).thenAnswer(invocation -> {
            Trajet trajet = invocation.getArgument(0);
            trajet.setId(10L);
            return trajet;
        });
        when(reservationRepository.sumNombrePlacesByTrajetIdAndStatut(eq(10L), eq(StatutReservation.CONFIRMEE)))
                .thenReturn(0);

        TrajetResponse response = trajetService.creer(request);

        assertEquals(10L, response.id());
        assertEquals("Casablanca", response.villeDepart());
        assertEquals("Rabat", response.villeArrivee());
        assertEquals(3, response.placesRestantes());
    }

    @Test
    void creer_placesDepassentCapacite_declencheException() {
        Conducteur conducteur = new Conducteur();
        conducteur.setId(1L);
        Vehicule vehicule = new Vehicule();
        vehicule.setId(2L);
        vehicule.setCapacite(2);
        vehicule.setConducteur(conducteur);

        TrajetRequest request = new TrajetRequest(1L, 2L, 3, List.of(
                new PointArretRequest("Casablanca", 0, LocalDateTime.now(), BigDecimal.ZERO),
                new PointArretRequest("Rabat", 1, LocalDateTime.now().plusHours(1), new BigDecimal("50.00"))
        ));

        when(conducteurRepository.findById(1L)).thenReturn(Optional.of(conducteur));
        when(vehiculeRepository.findById(2L)).thenReturn(Optional.of(vehicule));

        assertThrows(CapaciteVehiculeDepasseeException.class, () -> trajetService.creer(request));
    }

    @Test
    void obtenirParId_introuvable_declencheException() {
        when(trajetRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> trajetService.obtenirParId(99L));
    }
}
