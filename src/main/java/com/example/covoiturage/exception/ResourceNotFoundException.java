package com.example.covoiturage.exception;

/** 404 - ressource inexistante. */
public class ResourceNotFoundException extends ApiException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
