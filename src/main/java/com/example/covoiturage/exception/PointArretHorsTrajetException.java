package com.example.covoiturage.exception;

public class PointArretHorsTrajetException extends BadRequestException {

    public PointArretHorsTrajetException(Long pointArretId, Long trajetId) {
        super("Le point d'arrêt " + pointArretId + " n'appartient pas au trajet " + trajetId);
    }
}
