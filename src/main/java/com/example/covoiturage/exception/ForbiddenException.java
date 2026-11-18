package com.example.covoiturage.exception;

public class ForbiddenException extends ApiException {

    public ForbiddenException(String message) {
        super(message);
    }
}
