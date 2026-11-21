package com.example.covoiturage.service;

import com.example.covoiturage.dto.VehiculeRequest;
import com.example.covoiturage.dto.VehiculeResponse;
import com.example.covoiturage.entity.Conducteur;
import com.example.covoiturage.entity.Vehicule;
import com.example.covoiturage.exception.BadRequestException;
import com.example.covoiturage.exception.ImmatriculationDejaUtiliseeException;
import com.example.covoiturage.exception.ResourceNotFoundException;
import com.example.covoiturage.repository.ConducteurRepository;
import com.example.covoiturage.repository.TrajetRepository;
import com.example.covoiturage.repository.VehiculeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehiculeServiceTest {

    @Mock
    private VehiculeRepository vehiculeRepository;

    @Mock
    private ConducteurRepository conducteurRepository;

    @Mock
    private TrajetRepository trajetRepository;

    @InjectMocks
    private VehiculeService vehiculeService;

    @Test
    void creer_retourneVehicule() {
        Conducteur conducteur = new Conducteur();
        conducteur.setId(1L);
        VehiculeRequest request = new VehiculeRequest(1L, "Peugeot", "208", "AA-123-AA", "Noir", 4);

        when(conducteurRepository.findById(1L)).thenReturn(Optional.of(conducteur));
        when(vehiculeRepository.findByImmatriculation("AA-123-AA")).thenReturn(Optional.empty());
        when(vehiculeRepository.save(any(Vehicule.class))).thenAnswer(invocation -> {
            Vehicule vehicule = invocation.getArgument(0);
            vehicule.setId(10L);
            return vehicule;
        });

        VehiculeResponse response = vehiculeService.creer(request);

        assertEquals(10L, response.id());
        assertEquals("Peugeot", response.marque());
        assertEquals(1L, response.conducteurId());
    }

    @Test
    void creer_immatriculationDejaUtilisee_declencheException() {
        Conducteur conducteur = new Conducteur();
        conducteur.setId(1L);
        Vehicule existant = new Vehicule();
        existant.setId(2L);
        VehiculeRequest request = new VehiculeRequest(1L, "Peugeot", "208", "AA-123-AA", "Noir", 4);

        when(conducteurRepository.findById(1L)).thenReturn(Optional.of(conducteur));
        when(vehiculeRepository.findByImmatriculation("AA-123-AA")).thenReturn(Optional.of(existant));

        assertThrows(ImmatriculationDejaUtiliseeException.class, () -> vehiculeService.creer(request));
    }

    @Test
    void supprimer_vehiculeUtiliseParTrajet_declencheException() {
        Vehicule vehicule = new Vehicule();
        vehicule.setId(5L);
        when(vehiculeRepository.findById(5L)).thenReturn(Optional.of(vehicule));
        when(trajetRepository.existsByVehiculeId(5L)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> vehiculeService.supprimer(5L));
    }

    @Test
    void obtenirParId_introuvable_declencheException() {
        when(vehiculeRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> vehiculeService.obtenirParId(9L));
    }
}
