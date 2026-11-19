package com.example.covoiturage.service;

import com.example.covoiturage.dto.ConducteurRequest;
import com.example.covoiturage.dto.ConducteurResponse;
import com.example.covoiturage.entity.Conducteur;
import com.example.covoiturage.exception.EmailDejaUtiliseException;
import com.example.covoiturage.exception.NumeroPermisDejaUtiliseException;
import com.example.covoiturage.exception.ResourceNotFoundException;
import com.example.covoiturage.repository.ConducteurRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConducteurService {

    private final ConducteurRepository conducteurRepository;

    public ConducteurService(ConducteurRepository conducteurRepository) {
        this.conducteurRepository = conducteurRepository;
    }

    public ConducteurResponse creer(ConducteurRequest request) {
        if (conducteurRepository.existsByEmail(request.email())) {
            throw new EmailDejaUtiliseException(request.email());
        }
        if (conducteurRepository.existsByNumeroPermis(request.numeroPermis())) {
            throw new NumeroPermisDejaUtiliseException(request.numeroPermis());
        }

        Conducteur conducteur = new Conducteur();
        conducteur.setNom(request.nom());
        conducteur.setPrenom(request.prenom());
        conducteur.setEmail(request.email());
        conducteur.setTelephone(request.telephone());
        conducteur.setNumeroPermis(request.numeroPermis());

        return toResponse(conducteurRepository.save(conducteur));
    }

    public ConducteurResponse obtenirParId(Long id) {
        Conducteur conducteur = conducteurRepository.findById(id).orElse(null);
        if (conducteur == null) {
            throw new ResourceNotFoundException("Conducteur introuvable : id=" + id);
        }
        return toResponse(conducteur);
    }

    public List<ConducteurResponse> lister() {
        return conducteurRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ConducteurResponse> rechercher(String query) {
        if (query == null || query.isBlank()) {
            return lister();
        }

        String texte = query.toLowerCase();
        return conducteurRepository.findAll().stream()
                .filter(conducteur -> conducteur.getNom().toLowerCase().contains(texte)
                        || conducteur.getEmail().toLowerCase().contains(texte))
                .map(this::toResponse)
                .toList();
    }

    public ConducteurResponse modifier(Long id, ConducteurRequest request) {
        Conducteur conducteur = conducteurRepository.findById(id).orElse(null);
        if (conducteur == null) {
            throw new ResourceNotFoundException("Conducteur introuvable : id=" + id);
        }

        Conducteur autre = conducteurRepository.findByEmail(request.email()).orElse(null);
        if (autre != null && !autre.getId().equals(id)) {
            throw new EmailDejaUtiliseException(request.email());
        }

        Conducteur autrePermis = conducteurRepository.findByNumeroPermis(request.numeroPermis()).orElse(null);
        if (autrePermis != null && !autrePermis.getId().equals(id)) {
            throw new NumeroPermisDejaUtiliseException(request.numeroPermis());
        }

        conducteur.setNom(request.nom());
        conducteur.setPrenom(request.prenom());
        conducteur.setEmail(request.email());
        conducteur.setTelephone(request.telephone());
        conducteur.setNumeroPermis(request.numeroPermis());

        return toResponse(conducteurRepository.save(conducteur));
    }

    private ConducteurResponse toResponse(Conducteur conducteur) {
        return new ConducteurResponse(
                conducteur.getId(),
                conducteur.getNom(),
                conducteur.getPrenom(),
                conducteur.getEmail(),
                conducteur.getTelephone(),
                conducteur.getNumeroPermis()
        );
    }
}
