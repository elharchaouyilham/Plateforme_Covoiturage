package com.example.covoiturage.controller;

import com.example.covoiturage.dto.PointArretRequest;
import com.example.covoiturage.dto.PointArretResponse;
import com.example.covoiturage.service.PointArretService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/points-arret")
@Validated
public class PointArretController {

    private final PointArretService pointArretService;

    public PointArretController(PointArretService pointArretService) {
        this.pointArretService = pointArretService;
    }

    @PostMapping
    public ResponseEntity<PointArretResponse> creer(@RequestParam @Min(1) Long trajetId,
                                                    @Valid @RequestBody PointArretRequest request) {
        PointArretResponse cree = pointArretService.creer(trajetId, request);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/points-arret/{id}")
                .buildAndExpand(cree.id())
                .toUri();

        return ResponseEntity.created(location).body(cree);
    }

    @GetMapping
    public ResponseEntity<List<PointArretResponse>> lister(
            @RequestParam(name = "trajetId", required = false) Long trajetId) {
        if (trajetId == null) {
            return ResponseEntity.ok(pointArretService.lister());
        }
        return ResponseEntity.ok(pointArretService.listerParTrajet(trajetId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PointArretResponse> obtenirParId(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(pointArretService.obtenirParId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PointArretResponse> modifier(@PathVariable @Min(1) Long id,
                                                       @Valid @RequestBody PointArretRequest request) {
        return ResponseEntity.ok(pointArretService.modifier(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable @Min(1) Long id) {
        pointArretService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
