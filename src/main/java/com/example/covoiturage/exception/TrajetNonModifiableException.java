package com.example.covoiturage.exception;

public class TrajetNonModifiableException extends ConflictException {

    public TrajetNonModifiableException(Long trajetId, String statut) {
        super("Le trajet " + trajetId + " est à l'état " + statut + " et ne peut plus être modifié");
    }
}
