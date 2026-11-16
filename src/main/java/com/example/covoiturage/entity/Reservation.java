package com.example.covoiturage.entity;

import com.example.covoiturage.enums.StatutReservation;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Min(1)
    private Integer nombrePlaces;

    @NotNull
    @DecimalMin("0.00")
    @Column(precision = 10, scale = 2)
    private BigDecimal montantTotal;

    @NotNull
    private LocalDateTime dateReservation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutReservation statut = StatutReservation.CONFIRMEE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passager_id", nullable = false)
    private Passager passager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trajet_id", nullable = false)
    private Trajet trajet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_montee_id", nullable = false)
    private PointArret pointMontee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_descente_id", nullable = false)
    private PointArret pointDescente;

    public Reservation() {
    }

    public Long getId() { return id; }
    public Integer getNombrePlaces() { return nombrePlaces; }
    public BigDecimal getMontantTotal() { return montantTotal; }
    public LocalDateTime getDateReservation() { return dateReservation; }
    public StatutReservation getStatut() { return statut; }
    public Passager getPassager() { return passager; }
    public Trajet getTrajet() { return trajet; }
    public PointArret getPointMontee() { return pointMontee; }
    public PointArret getPointDescente() { return pointDescente; }

    public void setNombrePlaces(Integer nombrePlaces) { this.nombrePlaces = nombrePlaces; }
    public void setMontantTotal(BigDecimal montantTotal) { this.montantTotal = montantTotal; }
    public void setDateReservation(LocalDateTime dateReservation) { this.dateReservation = dateReservation; }
    public void setStatut(StatutReservation statut) { this.statut = statut; }
    public void setPassager(Passager passager) { this.passager = passager; }
    public void setTrajet(Trajet trajet) { this.trajet = trajet; }
    public void setPointMontee(PointArret pointMontee) { this.pointMontee = pointMontee; }
    public void setPointDescente(PointArret pointDescente) { this.pointDescente = pointDescente; }
}