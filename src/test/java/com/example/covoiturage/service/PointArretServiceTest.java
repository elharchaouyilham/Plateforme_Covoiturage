package com.example.covoiturage.service;

import com.example.covoiturage.dto.PointArretRequest;
import com.example.covoiturage.dto.PointArretResponse;
import com.example.covoiturage.entity.PointArret;
import com.example.covoiturage.entity.Trajet;
import com.example.covoiturage.exception.BadRequestException;
import com.example.covoiturage.exception.ResourceNotFoundException;
import com.example.covoiturage.repository.PointArretRepository;
import com.example.covoiturage.repository.TrajetRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointArretServiceTest {

    @Mock
    private PointArretRepository pointArretRepository;

    @Mock
    private TrajetRepository trajetRepository;

    @InjectMocks
    private PointArretService pointArretService;

    @Test
    void creer_retournePointArret() {
        Long trajetId = 1L;
        Trajet trajet = new Trajet();
        trajet.setId(trajetId);
        PointArretRequest request = new PointArretRequest("Casablanca", 0, LocalDateTime.now(), BigDecimal.ZERO);

        when(trajetRepository.existsById(trajetId)).thenReturn(true);
        when(pointArretRepository.findByTrajetIdOrderByOrdreAsc(trajetId)).thenReturn(List.of());
        when(trajetRepository.getReferenceById(trajetId)).thenReturn(trajet);
        when(pointArretRepository.save(any(PointArret.class))).thenAnswer(invocation -> {
            PointArret point = invocation.getArgument(0);
            point.setId(10L);
            return point;
        });

        PointArretResponse response = pointArretService.creer(trajetId, request);

        assertEquals(10L, response.id());
        assertEquals("Casablanca", response.ville());
    }

    @Test
    void creer_trajetIntrouvable_declencheException() {
        PointArretRequest request = new PointArretRequest("Casablanca", 0, LocalDateTime.now(), BigDecimal.ZERO);
        when(trajetRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> pointArretService.creer(1L, request));
    }

    @Test
    void creer_doublonOrdre_declencheException() {
        Long trajetId = 1L;
        PointArret existant = new PointArret();
        existant.setId(2L);
        existant.setOrdre(0);
        existant.setVille("Rabat");
        PointArretRequest request = new PointArretRequest("Casablanca", 0, LocalDateTime.now(), BigDecimal.ZERO);

        when(trajetRepository.existsById(trajetId)).thenReturn(true);
        when(pointArretRepository.findByTrajetIdOrderByOrdreAsc(trajetId)).thenReturn(List.of(existant));

        assertThrows(BadRequestException.class, () -> pointArretService.creer(trajetId, request));
    }

    @Test
    void obtenirParId_introuvable_declencheException() {
        when(pointArretRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> pointArretService.obtenirParId(99L));
    }
}
