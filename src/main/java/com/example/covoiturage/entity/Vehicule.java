package com.example.covoiturage.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "vehicules")
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String marque;

    @NotBlank
    private String modele;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String immatriculation;

    private String couleur;

    @NotNull
    @Min(1)
    @Max(8)
    private Integer capacite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conducteur_id", nullable = false)
    private Conducteur conducteur;

    public Vehicule() {
    }

    public Long getId() { return id; }
    public String getMarque() { return marque; }
    public String getModele() { return modele; }
    public String getImmatriculation() { return immatriculation; }
    public String getCouleur() { return couleur; }
    public Integer getCapacite() { return capacite; }
    public Conducteur getConducteur() { return conducteur; }

    public void setMarque(String marque) { this.marque = marque; }
    public void setModele(String modele) { this.modele = modele; }
    public void setImmatriculation(String immatriculation) { this.immatriculation = immatriculation; }
    public void setCouleur(String couleur) { this.couleur = couleur; }
    public void setCapacite(Integer capacite) { this.capacite = capacite; }
    public void setConducteur(Conducteur conducteur) { this.conducteur = conducteur; }
}