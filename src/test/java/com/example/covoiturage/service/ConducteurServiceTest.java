package com.example.covoiturage.service;

import com.example.covoiturage.dto.ConducteurRequest;
import com.example.covoiturage.dto.ConducteurResponse;
import com.example.covoiturage.entity.Conducteur;
import com.example.covoiturage.exception.EmailDejaUtiliseException;
import com.example.covoiturage.exception.ResourceNotFoundException;
import com.example.covoiturage.repository.ConducteurRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConducteurServiceTest {

    @Mock
    private ConducteurRepository conducteurRepository;

    @InjectMocks
    private ConducteurService conducteurService;

    @Test
    void creer_retourneConducteur() {
        ConducteurRequest request = new ConducteurRequest("Dupont", "Jean", "jean@mail.com", "0600000000", "PERM123");
        when(conducteurRepository.existsByEmail(request.email())).thenReturn(false);
        when(conducteurRepository.existsByNumeroPermis(request.numeroPermis())).thenReturn(false);
        when(conducteurRepository.save(any(Conducteur.class))).thenAnswer(invocation -> {
            Conducteur conducteur = invocation.getArgument(0);
            conducteur.setId(1L);
            return conducteur;
        });

        ConducteurResponse response = conducteurService.creer(request);

        assertEquals(1L, response.id());
        assertEquals("Dupont", response.nom());
        assertEquals("jean@mail.com", response.email());
        verify(conducteurRepository).save(any(Conducteur.class));
    }

    @Test
    void creer_emailDejaUtilise_declencheException() {
        ConducteurRequest request = new ConducteurRequest("Dupont", "Jean", "jean@mail.com", "0600000000", "PERM123");
        when(conducteurRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(EmailDejaUtiliseException.class, () -> conducteurService.creer(request));
    }

    @Test
    void obtenirParId_introuvable_declencheException() {
        when(conducteurRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> conducteurService.obtenirParId(99L));
    }

    @Test
    void lister_retourneListe() {
        Conducteur conducteur = new Conducteur();
        conducteur.setId(1L);
        conducteur.setNom("Dupont");
        conducteur.setPrenom("Jean");
        conducteur.setEmail("jean@mail.com");
        conducteur.setTelephone("0600000000");
        conducteur.setNumeroPermis("PERM123");
        when(conducteurRepository.findAll()).thenReturn(List.of(conducteur));

        List<ConducteurResponse> resultats = conducteurService.lister();

        assertEquals(1, resultats.size());
        assertEquals("Dupont", resultats.get(0).nom());
    }
}
