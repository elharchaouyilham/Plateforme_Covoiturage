package com.example.covoiturage.controller;

import com.example.covoiturage.dto.VehiculeRequest;
import com.example.covoiturage.dto.VehiculeResponse;
import com.example.covoiturage.service.VehiculeService;
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
@RequestMapping("/api/vehicules")
@Validated
public class VehiculeController {

    private final VehiculeService vehiculeService;

    public VehiculeController(VehiculeService vehiculeService) {
        this.vehiculeService = vehiculeService;
    }

    @PostMapping
    public ResponseEntity<VehiculeResponse> creer(@Valid @RequestBody VehiculeRequest request) {
        VehiculeResponse cree = vehiculeService.creer(request);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/vehicules/{id}")
                .buildAndExpand(cree.id())
                .toUri();

        return ResponseEntity.created(location).body(cree);
    }

    @GetMapping
    public ResponseEntity<List<VehiculeResponse>> lister(
            @RequestParam(name = "conducteurId", required = false) Long conducteurId) {
        if (conducteurId == null) {
            return ResponseEntity.ok(vehiculeService.lister());
        }
        return ResponseEntity.ok(vehiculeService.listerParConducteur(conducteurId));
    }

    @GetMapping("/recherche")
    public ResponseEntity<List<VehiculeResponse>> rechercher(
            @RequestParam(name = "q", required = false) String query) {
        return ResponseEntity.ok(vehiculeService.rechercher(query));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehiculeResponse> obtenirParId(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(vehiculeService.obtenirParId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehiculeResponse> modifier(@PathVariable @Min(1) Long id,
                                                     @Valid @RequestBody VehiculeRequest request) {
        return ResponseEntity.ok(vehiculeService.modifier(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable @Min(1) Long id) {
        vehiculeService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
