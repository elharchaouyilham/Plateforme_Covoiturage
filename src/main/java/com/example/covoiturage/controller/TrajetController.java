package com.example.covoiturage.controller;

import com.example.covoiturage.dto.TrajetRequest;
import com.example.covoiturage.dto.TrajetResponse;
import com.example.covoiturage.service.TrajetService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping("/api/trajets")
@Validated
public class TrajetController {

    private final TrajetService trajetService;

    public TrajetController(TrajetService trajetService) {
        this.trajetService = trajetService;
    }

    @PostMapping
    public ResponseEntity<TrajetResponse> creer(@Valid @RequestBody TrajetRequest request) {
        TrajetResponse cree = trajetService.creer(request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(cree.id())
                .toUri();

        return ResponseEntity.created(location).body(cree);
    }

    @GetMapping
    public ResponseEntity<List<TrajetResponse>> lister() {
        return ResponseEntity.ok(trajetService.lister());
    }

    @GetMapping("/recherche")
    public ResponseEntity<List<TrajetResponse>> rechercher(
            @RequestParam(name = "villeDepart", required = false) String villeDepart,
            @RequestParam(name = "villeArrivee", required = false) String villeArrivee) {
        return ResponseEntity.ok(trajetService.rechercher(villeDepart, villeArrivee));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrajetResponse> obtenirParId(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(trajetService.obtenirParId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrajetResponse> modifier(@PathVariable @Min(1) Long id,
                                                   @Valid @RequestBody TrajetRequest request) {
        return ResponseEntity.ok(trajetService.modifier(id, request));
    }
}
