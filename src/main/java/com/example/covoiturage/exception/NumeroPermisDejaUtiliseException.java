package com.example.covoiturage.exception;

public class NumeroPermisDejaUtiliseException extends ConflictException {

    public NumeroPermisDejaUtiliseException(String numeroPermis) {
        super("Le numéro de permis '" + numeroPermis + "' est déjà utilisé");
    }
}
