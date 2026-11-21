package com.example.covoiturage.service;

import com.example.covoiturage.dto.PassagerRequest;
import com.example.covoiturage.dto.PassagerResponse;
import com.example.covoiturage.entity.Passager;
import com.example.covoiturage.exception.EmailDejaUtiliseException;
import com.example.covoiturage.exception.ResourceNotFoundException;
import com.example.covoiturage.repository.PassagerRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PassagerServiceTest {

    @Mock
    private PassagerRepository passagerRepository;

    @InjectMocks
    private PassagerService passagerService;

    @Test
    void creer_retournePassager() {
        PassagerRequest request = new PassagerRequest("Martin", "Alice", "alice@mail.com", "0611111111");
        when(passagerRepository.existsByEmail(request.email())).thenReturn(false);
        when(passagerRepository.save(any(Passager.class))).thenAnswer(invocation -> {
            Passager passager = invocation.getArgument(0);
            passager.setId(1L);
            return passager;
        });

        PassagerResponse response = passagerService.creer(request);

        assertEquals(1L, response.id());
        assertEquals("Alice", response.prenom());
    }

    @Test
    void creer_emailDejaUtilise_declencheException() {
        PassagerRequest request = new PassagerRequest("Martin", "Alice", "alice@mail.com", "0611111111");
        when(passagerRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(EmailDejaUtiliseException.class, () -> passagerService.creer(request));
    }

    @Test
    void obtenirParId_introuvable_declencheException() {
        when(passagerRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> passagerService.obtenirParId(5L));
    }

    @Test
    void rechercher_filtreParNomOuEmail() {
        Passager passager = new Passager();
        passager.setId(1L);
        passager.setNom("Martin");
        passager.setPrenom("Alice");
        passager.setEmail("alice@mail.com");
        passager.setTelephone("0611111111");
        when(passagerRepository.findAll()).thenReturn(List.of(passager));

        List<PassagerResponse> resultats = passagerService.rechercher("ali");

        assertEquals(1, resultats.size());
        assertEquals("Martin", resultats.get(0).nom());
    }
}
