package com.example.covoiturage.exception;

public class MauvaisSensException extends BadRequestException {

    public MauvaisSensException() {
        super("Le point de montée doit être avant le point de descente dans le sens du trajet");
    }
}
