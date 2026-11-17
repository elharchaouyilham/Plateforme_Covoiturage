package com.example.covoiturage.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "passagers")
public class Passager extends Utilisateur {

    @OneToMany(mappedBy = "passager")
    private List<Reservation> reservations = new ArrayList<>();

    public Passager() {
    }

    public List<Reservation> getReservations() { return reservations; }

    public void setReservations(List<Reservation> reservations) { this.reservations = reservations; }
}
