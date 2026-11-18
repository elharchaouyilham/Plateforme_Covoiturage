package com.example.covoiturage.exception;

public class EmailDejaUtiliseException extends ConflictException {

    public EmailDejaUtiliseException(String email) {
        super("L'email '" + email + "' est déjà utilisé");
    }
}
