package com.example.covoiturage.exception;

public class ImmatriculationDejaUtiliseeException extends ConflictException {

    public ImmatriculationDejaUtiliseeException(String immatriculation) {
        super("L'immatriculation '" + immatriculation + "' est déjà utilisée");
    }
}
