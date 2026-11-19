package com.example.covoiturage.service;

import com.example.covoiturage.dto.PassagerRequest;
import com.example.covoiturage.dto.PassagerResponse;
import com.example.covoiturage.entity.Passager;
import com.example.covoiturage.exception.EmailDejaUtiliseException;
import com.example.covoiturage.exception.ResourceNotFoundException;
import com.example.covoiturage.repository.PassagerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PassagerService {

    private final PassagerRepository passagerRepository;

    public PassagerService(PassagerRepository passagerRepository) {
        this.passagerRepository = passagerRepository;
    }

    public PassagerResponse creer(PassagerRequest request) {
        if (passagerRepository.existsByEmail(request.email())) {
            throw new EmailDejaUtiliseException(request.email());
        }

        Passager passager = new Passager();
        passager.setNom(request.nom());
        passager.setPrenom(request.prenom());
        passager.setEmail(request.email());
        passager.setTelephone(request.telephone());

        return toResponse(passagerRepository.save(passager));
    }

    public PassagerResponse obtenirParId(Long id) {
        Passager passager = passagerRepository.findById(id).orElse(null);
        if (passager == null) {
            throw new ResourceNotFoundException("Passager introuvable : id=" + id);
        }
        return toResponse(passager);
    }

    public List<PassagerResponse> lister() {
        return passagerRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<PassagerResponse> rechercher(String query) {
        if (query == null || query.isBlank()) {
            return lister();
        }

        String texte = query.toLowerCase();
        return passagerRepository.findAll().stream()
                .filter(passager -> passager.getNom().toLowerCase().contains(texte)
                        || passager.getEmail().toLowerCase().contains(texte))
                .map(this::toResponse)
                .toList();
    }

    public PassagerResponse modifier(Long id, PassagerRequest request) {
        Passager passager = passagerRepository.findById(id).orElse(null);
        if (passager == null) {
            throw new ResourceNotFoundException("Passager introuvable : id=" + id);
        }

        Passager autre = passagerRepository.findByEmail(request.email()).orElse(null);
        if (autre != null && !autre.getId().equals(id)) {
            throw new EmailDejaUtiliseException(request.email());
        }

        passager.setNom(request.nom());
        passager.setPrenom(request.prenom());
        passager.setEmail(request.email());
        passager.setTelephone(request.telephone());

        return toResponse(passagerRepository.save(passager));
    }

    private PassagerResponse toResponse(Passager passager) {
        return new PassagerResponse(
                passager.getId(),
                passager.getNom(),
                passager.getPrenom(),
                passager.getEmail(),
                passager.getTelephone()
        );
    }
}
