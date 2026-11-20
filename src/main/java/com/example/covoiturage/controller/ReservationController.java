package com.example.covoiturage.controller;

import com.example.covoiturage.dto.ReservationRequest;
import com.example.covoiturage.dto.ReservationResponse;
import com.example.covoiturage.service.ReservationService;
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
@RequestMapping("/api/reservations")
@Validated
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> creer(@Valid @RequestBody ReservationRequest request) {
        ReservationResponse cree = reservationService.creer(request);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/reservations/{id}")
                .buildAndExpand(cree.id())
                .toUri();

        return ResponseEntity.created(location).body(cree);
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> lister(
            @RequestParam(name = "passagerId", required = false) Long passagerId,
            @RequestParam(name = "trajetId", required = false) Long trajetId) {
        if (passagerId != null) {
            return ResponseEntity.ok(reservationService.listerParPassager(passagerId));
        }
        if (trajetId != null) {
            return ResponseEntity.ok(reservationService.listerParTrajet(trajetId));
        }
        return ResponseEntity.ok(reservationService.lister());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> obtenirParId(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(reservationService.obtenirParId(id));
    }

    @PutMapping("/{id}/annuler")
    public ResponseEntity<ReservationResponse> annuler(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(reservationService.annuler(id));
    }
}
