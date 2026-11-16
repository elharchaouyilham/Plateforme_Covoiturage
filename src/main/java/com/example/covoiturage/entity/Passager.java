
package com.example.covoiturage.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "passagers")
@Getter
@Setter
@NoArgsConstructor
public class Passager extends Utilisateur {

    @OneToMany(mappedBy = "passager")
    private List<Reservation> reservations = new ArrayList<>();
}
