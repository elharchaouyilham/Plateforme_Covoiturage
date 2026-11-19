package com.example.covoiturage.service;

import com.example.covoiturage.dto.PointArretRequest;
import com.example.covoiturage.dto.PointArretResponse;
import com.example.covoiturage.entity.PointArret;
import com.example.covoiturage.exception.BadRequestException;
import com.example.covoiturage.exception.ResourceNotFoundException;
import com.example.covoiturage.repository.PointArretRepository;
import com.example.covoiturage.repository.TrajetRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointArretService {

    private final PointArretRepository pointArretRepository;
    private final TrajetRepository trajetRepository;

    public PointArretService(PointArretRepository pointArretRepository,
                             TrajetRepository trajetRepository) {
        this.pointArretRepository = pointArretRepository;
        this.trajetRepository = trajetRepository;
    }

    public PointArretResponse creer(Long trajetId, PointArretRequest request) {
        verifierTrajet(trajetId);
        verifierDoublon(trajetId, request, null);

        PointArret point = new PointArret();
        remplir(point, request);
        point.setTrajet(trajetRepository.getReferenceById(trajetId));

        return toResponse(pointArretRepository.save(point));
    }

    public PointArretResponse obtenirParId(Long id) {
        PointArret point = pointArretRepository.findById(id).orElse(null);
        if (point == null) {
            throw new ResourceNotFoundException("Point d'arrêt introuvable : id=" + id);
        }
        return toResponse(point);
    }

    public List<PointArretResponse> lister() {
        return pointArretRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<PointArretResponse> listerParTrajet(Long trajetId) {
        verifierTrajet(trajetId);
        return pointArretRepository.findByTrajetIdOrderByOrdreAsc(trajetId).stream()
                .map(this::toResponse)
                .toList();
    }

    public PointArretResponse modifier(Long id, PointArretRequest request) {
        PointArret point = pointArretRepository.findById(id).orElse(null);
        if (point == null) {
            throw new ResourceNotFoundException("Point d'arrêt introuvable : id=" + id);
        }

        verifierDoublon(point.getTrajet().getId(), request, id);
        remplir(point, request);

        return toResponse(pointArretRepository.save(point));
    }

    public void supprimer(Long id) {
        PointArret point = pointArretRepository.findById(id).orElse(null);
        if (point == null) {
            throw new ResourceNotFoundException("Point d'arrêt introuvable : id=" + id);
        }
        pointArretRepository.delete(point);
    }

    private void verifierTrajet(Long trajetId) {
        if (!trajetRepository.existsById(trajetId)) {
            throw new ResourceNotFoundException("Trajet introuvable : id=" + trajetId);
        }
    }

    private void verifierDoublon(Long trajetId, PointArretRequest request, Long idCourant) {
        boolean existe = pointArretRepository.findByTrajetIdOrderByOrdreAsc(trajetId).stream()
                .filter(point -> idCourant == null || !point.getId().equals(idCourant))
                .anyMatch(point -> point.getOrdre().equals(request.ordre())
                        || point.getVille().equalsIgnoreCase(request.ville()));
        if (existe) {
            throw new BadRequestException("Ordre ou ville déjà utilisé dans ce trajet");
        }
    }

    private void remplir(PointArret point, PointArretRequest request) {
        point.setVille(request.ville());
        point.setOrdre(request.ordre());
        point.setHeurePassage(request.heurePassage());
        point.setPrixDepuisDepart(request.prixDepuisDepart());
    }

    private PointArretResponse toResponse(PointArret point) {
        return new PointArretResponse(
                point.getId(),
                point.getVille(),
                point.getOrdre(),
                point.getHeurePassage(),
                point.getPrixDepuisDepart()
        );
    }
}
