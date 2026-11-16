package com.example.covoiturage.entity;

import com.example.covoiturage.enums.StatutTrajet;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trajets")
public class Trajet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDateTime dateDepart;

    @NotNull
    @Min(1)
    private Integer placesProposees;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutTrajet statut = StatutTrajet.PUBLIE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conducteur_id", nullable = false)
    private Conducteur conducteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicule_id", nullable = false)
    private Vehicule vehicule;

    @OneToMany(
            mappedBy = "trajet",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("ordre ASC")
    private List<PointArret> pointsArret = new ArrayList<>();

    @OneToMany(mappedBy = "trajet")
    private List<Reservation> reservations = new ArrayList<>();

    public Trajet() {
    }

    public Long getId() { return id; }
    public LocalDateTime getDateDepart() { return dateDepart; }
    public Integer getPlacesProposees() { return placesProposees; }
    public StatutTrajet getStatut() { return statut; }
    public Conducteur getConducteur() { return conducteur; }
    public Vehicule getVehicule() { return vehicule; }
    public List<PointArret> getPointsArret() { return pointsArret; }
    public List<Reservation> getReservations() { return reservations; }

    public void setDateDepart(LocalDateTime dateDepart) { this.dateDepart = dateDepart; }
    public void setPlacesProposees(Integer placesProposees) { this.placesProposees = placesProposees; }
    public void setStatut(StatutTrajet statut) { this.statut = statut; }
    public void setConducteur(Conducteur conducteur) { this.conducteur = conducteur; }
    public void setVehicule(Vehicule vehicule) { this.vehicule = vehicule; }

    public void ajouterPointArret(PointArret point) {
        pointsArret.add(point);
        point.setTrajet(this);
    }

    public void supprimerPointArret(PointArret point) {
        pointsArret.remove(point);
        point.setTrajet(null);
    }
}