
package com.example.covoiturage.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conducteurs")
@Getter
@Setter
@NoArgsConstructor
public class Conducteur extends Utilisateur {

    @NotBlank
    @Column(unique = true, nullable = false)
    private String numeroPermis;

    @OneToMany(mappedBy = "conducteur")
    private List<Vehicule> vehicules = new ArrayList<>();

    @OneToMany(mappedBy = "conducteur")
    private List<Trajet> trajets = new ArrayList<>();
}
