package com.example.covoiturage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conducteurs")
public class Conducteur extends Utilisateur {

    @NotBlank
    @Column(unique = true, nullable = false)
    private String numeroPermis;

    @OneToMany(mappedBy = "conducteur")
    private List<Vehicule> vehicules = new ArrayList<>();

    @OneToMany(mappedBy = "conducteur")
    private List<Trajet> trajets = new ArrayList<>();

    public Conducteur() {
    }

    public String getNumeroPermis() { return numeroPermis; }
    public List<Vehicule> getVehicules() { return vehicules; }
    public List<Trajet> getTrajets() { return trajets; }

    public void setNumeroPermis(String numeroPermis) { this.numeroPermis = numeroPermis; }
    public void setVehicules(List<Vehicule> vehicules) { this.vehicules = vehicules; }
    public void setTrajets(List<Trajet> trajets) { this.trajets = trajets; }
}
