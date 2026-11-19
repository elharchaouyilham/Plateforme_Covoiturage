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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehiculeService {

    private final VehiculeRepository vehiculeRepository;
    private final ConducteurRepository conducteurRepository;
    private final TrajetRepository trajetRepository;

    public VehiculeService(VehiculeRepository vehiculeRepository,
                           ConducteurRepository conducteurRepository,
                           TrajetRepository trajetRepository) {
        this.vehiculeRepository = vehiculeRepository;
        this.conducteurRepository = conducteurRepository;
        this.trajetRepository = trajetRepository;
    }

    public VehiculeResponse creer(VehiculeRequest request) {
        Conducteur conducteur = findConducteur(request.conducteurId());

        Vehicule autre = vehiculeRepository.findByImmatriculation(request.immatriculation()).orElse(null);
        if (autre != null) {
            throw new ImmatriculationDejaUtiliseeException(request.immatriculation());
        }

        Vehicule vehicule = new Vehicule();
        remplir(vehicule, request, conducteur);

        return toResponse(vehiculeRepository.save(vehicule));
    }

    public VehiculeResponse obtenirParId(Long id) {
        Vehicule vehicule = vehiculeRepository.findById(id).orElse(null);
        if (vehicule == null) {
            throw new ResourceNotFoundException("Véhicule introuvable : id=" + id);
        }
        return toResponse(vehicule);
    }

    public List<VehiculeResponse> lister() {
        return vehiculeRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<VehiculeResponse> listerParConducteur(Long conducteurId) {
        return vehiculeRepository.findByConducteurId(conducteurId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<VehiculeResponse> rechercher(String query) {
        if (query == null || query.isBlank()) {
            return lister();
        }

        String texte = query.toLowerCase();
        return vehiculeRepository.findAll().stream()
                .filter(vehicule -> vehicule.getMarque().toLowerCase().contains(texte)
                        || vehicule.getModele().toLowerCase().contains(texte)
                        || vehicule.getImmatriculation().toLowerCase().contains(texte))
                .map(this::toResponse)
                .toList();
    }

    public VehiculeResponse modifier(Long id, VehiculeRequest request) {
        Vehicule vehicule = vehiculeRepository.findById(id).orElse(null);
        if (vehicule == null) {
            throw new ResourceNotFoundException("Véhicule introuvable : id=" + id);
        }

        Conducteur conducteur = findConducteur(request.conducteurId());

        Vehicule autre = vehiculeRepository.findByImmatriculation(request.immatriculation()).orElse(null);
        if (autre != null && !autre.getId().equals(id)) {
            throw new ImmatriculationDejaUtiliseeException(request.immatriculation());
        }

        remplir(vehicule, request, conducteur);
        return toResponse(vehiculeRepository.save(vehicule));
    }

    public void supprimer(Long id) {
        Vehicule vehicule = vehiculeRepository.findById(id).orElse(null);
        if (vehicule == null) {
            throw new ResourceNotFoundException("Véhicule introuvable : id=" + id);
        }
        if (trajetRepository.existsByVehiculeId(id)) {
            throw new BadRequestException("Ce véhicule est utilisé par un trajet");
        }
        vehiculeRepository.delete(vehicule);
    }

    private Conducteur findConducteur(Long id) {
        if (id == null) {
            throw new BadRequestException("Le conducteur est obligatoire");
        }
        Conducteur conducteur = conducteurRepository.findById(id).orElse(null);
        if (conducteur == null) {
            throw new ResourceNotFoundException("Conducteur introuvable : id=" + id);
        }
        return conducteur;
    }

    private void remplir(Vehicule vehicule, VehiculeRequest request, Conducteur conducteur) {
        vehicule.setMarque(request.marque());
        vehicule.setModele(request.modele());
        vehicule.setImmatriculation(request.immatriculation());
        vehicule.setCouleur(request.couleur());
        vehicule.setCapacite(request.capacite());
        vehicule.setConducteur(conducteur);
    }

    private VehiculeResponse toResponse(Vehicule vehicule) {
        return new VehiculeResponse(
                vehicule.getId(),
                vehicule.getMarque(),
                vehicule.getModele(),
                vehicule.getImmatriculation(),
                vehicule.getCouleur(),
                vehicule.getCapacite(),
                vehicule.getConducteur().getId()
        );
    }
}
