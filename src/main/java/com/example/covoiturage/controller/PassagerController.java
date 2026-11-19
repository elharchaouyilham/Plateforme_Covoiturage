package com.example.covoiturage.controller;

import com.example.covoiturage.dto.PassagerRequest;
import com.example.covoiturage.dto.PassagerResponse;
import com.example.covoiturage.service.PassagerService;
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
@RequestMapping("/api/passagers")
@Validated
public class PassagerController {

    private final PassagerService passagerService;

    public PassagerController(PassagerService passagerService) {
        this.passagerService = passagerService;
    }

    @PostMapping
    public ResponseEntity<PassagerResponse> creer(@Valid @RequestBody PassagerRequest request) {
        PassagerResponse cree = passagerService.creer(request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(cree.id())
                .toUri();

        return ResponseEntity.created(location).body(cree);
    }

    @GetMapping
    public ResponseEntity<List<PassagerResponse>> lister() {
        return ResponseEntity.ok(passagerService.lister());
    }

    @GetMapping("/recherche")
    public ResponseEntity<List<PassagerResponse>> rechercher(
            @RequestParam(name = "q", required = false) String query) {
        return ResponseEntity.ok(passagerService.rechercher(query));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PassagerResponse> obtenirParId(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(passagerService.obtenirParId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PassagerResponse> modifier(@PathVariable @Min(1) Long id,
                                                     @Valid @RequestBody PassagerRequest request) {
        return ResponseEntity.ok(passagerService.modifier(id, request));
    }
}
