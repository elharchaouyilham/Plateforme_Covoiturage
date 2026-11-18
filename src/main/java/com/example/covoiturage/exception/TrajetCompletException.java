package com.example.covoiturage.exception;

public class TrajetCompletException extends ConflictException {

    public TrajetCompletException(Long trajetId) {
        super("Le trajet " + trajetId + " est complet : aucune place restante");
    }
}
