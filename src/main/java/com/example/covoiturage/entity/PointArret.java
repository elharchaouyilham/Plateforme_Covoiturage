package com.example.covoiturage.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "points_arret",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_trajet_ordre",
                        columnNames = {"trajet_id", "ordre"}
                ),
                @UniqueConstraint(
                        name = "uk_trajet_ville",
                        columnNames = {"trajet_id", "ville"}
                )
        }
)
public class PointArret {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String ville;

    @NotNull
    @Min(0)
    private Integer ordre;

    @NotNull
    private LocalDateTime heurePassage;

    @NotNull
    @DecimalMin("0.00")
    @Column(precision = 10, scale = 2)
    private BigDecimal prixDepuisDepart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trajet_id", nullable = false)
    private Trajet trajet;

    public PointArret() {
    }

    public Long getId() { return id; }
    public String getVille() { return ville; }
    public Integer getOrdre() { return ordre; }
    public LocalDateTime getHeurePassage() { return heurePassage; }
    public BigDecimal getPrixDepuisDepart() { return prixDepuisDepart; }
    public Trajet getTrajet() { return trajet; }

    public void setVille(String ville) { this.ville = ville; }
    public void setOrdre(Integer ordre) { this.ordre = ordre; }
    public void setHeurePassage(LocalDateTime heurePassage) { this.heurePassage = heurePassage; }
    public void setPrixDepuisDepart(BigDecimal prixDepuisDepart) { this.prixDepuisDepart = prixDepuisDepart; }
    public void setTrajet(Trajet trajet) { this.trajet = trajet; }
}