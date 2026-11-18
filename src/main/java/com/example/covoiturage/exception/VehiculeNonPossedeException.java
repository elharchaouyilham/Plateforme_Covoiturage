package com.example.covoiturage.exception;

public class VehiculeNonPossedeException extends ForbiddenException {

    public VehiculeNonPossedeException(Long vehiculeId, Long conducteurId) {
        super("Le véhicule " + vehiculeId + " n'appartient pas au conducteur " + conducteurId);
    }
}
