package com.example.covoiturage.controller;

import com.example.covoiturage.dto.ConducteurRequest;
import com.example.covoiturage.dto.ConducteurResponse;
import com.example.covoiturage.service.ConducteurService;
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
@RequestMapping("/api/conducteurs")
@Validated
public class ConducteurController {

    private final ConducteurService conducteurService;

    public ConducteurController(ConducteurService conducteurService) {
        this.conducteurService = conducteurService;
    }

    @PostMapping
    public ResponseEntity<ConducteurResponse> creer(@Valid @RequestBody ConducteurRequest request) {
        ConducteurResponse cree = conducteurService.creer(request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(cree.id())
                .toUri();

        return ResponseEntity.created(location).body(cree);
    }

    @GetMapping
    public ResponseEntity<List<ConducteurResponse>> lister() {
        return ResponseEntity.ok(conducteurService.lister());
    }

    @GetMapping("/recherche")
    public ResponseEntity<List<ConducteurResponse>> rechercher(
            @RequestParam(name = "q", required = false) String query) {
        return ResponseEntity.ok(conducteurService.rechercher(query));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConducteurResponse> obtenirParId(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(conducteurService.obtenirParId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConducteurResponse> modifier(@PathVariable @Min(1) Long id,
                                                       @Valid @RequestBody ConducteurRequest request) {
        return ResponseEntity.ok(conducteurService.modifier(id, request));
    }
}
