package com.example.covoiturage.exception;

public class DelaiAnnulationDepasseException extends ConflictException {

    public DelaiAnnulationDepasseException() {
        super("L'annulation n'est plus possible : le délai de 24 heures avant le départ est dépassé");
    }
}
