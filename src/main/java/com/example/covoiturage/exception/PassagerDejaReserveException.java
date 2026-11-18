package com.example.covoiturage.exception;

public class PassagerDejaReserveException extends ConflictException {

    public PassagerDejaReserveException(Long trajetId) {
        super("Vous avez déjà une réservation confirmée sur le trajet " + trajetId);
    }
}
