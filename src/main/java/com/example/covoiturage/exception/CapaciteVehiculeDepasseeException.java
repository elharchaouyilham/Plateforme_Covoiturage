package com.example.covoiturage.exception;

public class CapaciteVehiculeDepasseeException extends BadRequestException {

    public CapaciteVehiculeDepasseeException(int placesProposees, int capacite) {
        super("Le nombre de places proposées (" + placesProposees
                + ") dépasse la capacité du véhicule (" + capacite + " places passagers)");
    }
}
