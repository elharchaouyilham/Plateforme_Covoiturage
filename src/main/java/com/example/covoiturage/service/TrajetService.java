package com.example.covoiturage.service;

import com.example.covoiturage.dto.ConducteurResponse;
import com.example.covoiturage.dto.PointArretRequest;
import com.example.covoiturage.dto.PointArretResponse;
import com.example.covoiturage.dto.TrajetRequest;
import com.example.covoiturage.dto.TrajetResponse;
import com.example.covoiturage.dto.VehiculeResponse;
import com.example.covoiturage.entity.Conducteur;
import com.example.covoiturage.entity.PointArret;
import com.example.covoiturage.entity.Trajet;
import com.example.covoiturage.entity.Vehicule;
import com.example.covoiturage.enums.StatutReservation;
import com.example.covoiturage.enums.StatutTrajet;
import com.example.covoiturage.exception.BadRequestException;
import com.example.covoiturage.exception.CapaciteVehiculeDepasseeException;
import com.example.covoiturage.exception.ResourceNotFoundException;
import com.example.covoiturage.repository.ConducteurRepository;
import com.example.covoiturage.repository.ReservationRepository;
import com.example.covoiturage.repository.TrajetRepository;
import com.example.covoiturage.repository.VehiculeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class TrajetService {

    private final TrajetRepository trajetRepository;
    private final ConducteurRepository conducteurRepository;
    private final VehiculeRepository vehiculeRepository;
    private final ReservationRepository reservationRepository;

    public TrajetService(TrajetRepository trajetRepository,
                         ConducteurRepository conducteurRepository,
                         VehiculeRepository vehiculeRepository,
                         ReservationRepository reservationRepository) {
        this.trajetRepository = trajetRepository;
        this.conducteurRepository = conducteurRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.reservationRepository = reservationRepository;
    }

    public TrajetResponse creer(TrajetRequest request) {
        Conducteur conducteur = findConducteur(request.conducteurId());
        Vehicule vehicule = findVehicule(request.vehiculeId());
        verifierPlaces(request.placesProposees(), vehicule.getCapacite());

        Trajet trajet = new Trajet();
        remplir(trajet, request, conducteur, vehicule);
        return toResponse(trajetRepository.save(trajet));
    }

    public TrajetResponse obtenirParId(Long id) {
        Trajet trajet = trajetRepository.findById(id).orElse(null);
        if (trajet == null) {
            throw new ResourceNotFoundException("Trajet introuvable : id=" + id);
        }
        return toResponse(trajet);
    }

    public List<TrajetResponse> lister() {
        return trajetRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<TrajetResponse> rechercher(String villeDepart, String villeArrivee) {
        return trajetRepository.findAll().stream()
                .filter(trajet -> villeDepart == null
                        || trajet.getVilleDepart().toLowerCase().contains(villeDepart.toLowerCase()))
                .filter(trajet -> villeArrivee == null
                        || trajet.getVilleArrivee().toLowerCase().contains(villeArrivee.toLowerCase()))
                .map(this::toResponse)
                .toList();
    }

    public TrajetResponse modifier(Long id, TrajetRequest request) {
        Trajet trajet = trajetRepository.findById(id).orElse(null);
        if (trajet == null) {
            throw new ResourceNotFoundException("Trajet introuvable : id=" + id);
        }

        Conducteur conducteur = findConducteur(request.conducteurId());
        Vehicule vehicule = findVehicule(request.vehiculeId());
        verifierPlaces(request.placesProposees(), vehicule.getCapacite());

        remplir(trajet, request, conducteur, vehicule);
        return toResponse(trajetRepository.save(trajet));
    }

    private Conducteur findConducteur(Long id) {
        Conducteur conducteur = conducteurRepository.findById(id).orElse(null);
        if (conducteur == null) {
            throw new ResourceNotFoundException("Conducteur introuvable : id=" + id);
        }
        return conducteur;
    }

    private Vehicule findVehicule(Long id) {
        Vehicule vehicule = vehiculeRepository.findById(id).orElse(null);
        if (vehicule == null) {
            throw new ResourceNotFoundException("Vehicule introuvable : id=" + id);
        }
        return vehicule;
    }

    private void verifierPlaces(int placesProposees, int capacite) {
        if (placesProposees > capacite) {
            throw new CapaciteVehiculeDepasseeException(placesProposees, capacite);
        }
    }

    private void remplir(Trajet trajet, TrajetRequest request, Conducteur conducteur, Vehicule vehicule) {
        if (request.pointsArret().size() < 2) {
            throw new BadRequestException("Le trajet doit contenir au moins deux points d'arrêt");
        }

        List<PointArretRequest> pointsRequest = request.pointsArret().stream()
                .sorted(Comparator.comparing(PointArretRequest::ordre))
                .toList();

        trajet.setConducteur(conducteur);
        trajet.setVehicule(vehicule);
        trajet.setPlacesProposees(request.placesProposees());
        trajet.setStatut(StatutTrajet.PUBLIE);

        trajet.getPointsArret().clear();
        for (PointArretRequest p : pointsRequest) {
            PointArret point = new PointArret();
            point.setVille(p.ville());
            point.setOrdre(p.ordre());
            point.setHeurePassage(p.heurePassage());
            point.setPrixDepuisDepart(p.prixDepuisDepart());
            point.setTrajet(trajet);
            trajet.getPointsArret().add(point);
        }

        PointArret premier = trajet.getPointsArret().get(0);
        PointArret dernier = trajet.getPointsArret().get(trajet.getPointsArret().size() - 1);
        trajet.setVilleDepart(premier.getVille());
        trajet.setVilleArrivee(dernier.getVille());
        trajet.setDateDepart(premier.getHeurePassage());
    }

    private TrajetResponse toResponse(Trajet trajet) {
        int placesRestantes = trajet.getPlacesProposees()
                - reservationRepository.sumNombrePlacesByTrajetIdAndStatut(
                        trajet.getId(), StatutReservation.CONFIRMEE);

        BigDecimal prixTotal = trajet.getPointsArret().stream()
                .max(Comparator.comparing(PointArret::getOrdre))
                .map(PointArret::getPrixDepuisDepart)
                .orElse(BigDecimal.ZERO);

        List<PointArretResponse> points = trajet.getPointsArret().stream()
                .map(point -> new PointArretResponse(
                        point.getId(),
                        point.getVille(),
                        point.getOrdre(),
                        point.getHeurePassage(),
                        point.getPrixDepuisDepart()))
                .toList();

        ConducteurResponse conducteur = toConducteurResponse(trajet.getConducteur());
        VehiculeResponse vehicule = toVehiculeResponse(trajet.getVehicule());

        return new TrajetResponse(
                trajet.getId(),
                trajet.getVilleDepart(),
                trajet.getVilleArrivee(),
                trajet.getDateDepart(),
                trajet.getPlacesProposees(),
                placesRestantes,
                prixTotal,
                trajet.getStatut(),
                conducteur,
                vehicule,
                points
        );
    }

    private ConducteurResponse toConducteurResponse(Conducteur conducteur) {
        return new ConducteurResponse(
                conducteur.getId(),
                conducteur.getNom(),
                conducteur.getPrenom(),
                conducteur.getEmail(),
                conducteur.getTelephone(),
                conducteur.getNumeroPermis()
        );
    }

    private VehiculeResponse toVehiculeResponse(Vehicule vehicule) {
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
