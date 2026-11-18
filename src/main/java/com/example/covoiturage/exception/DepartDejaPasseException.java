package com.example.covoiturage.exception;

public class DepartDejaPasseException extends ConflictException {

    public DepartDejaPasseException() {
        super("Le trajet ne peut être terminé qu'après son heure de départ");
    }
}
