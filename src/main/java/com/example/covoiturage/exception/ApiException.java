package com.example.covoiturage.exception;


public abstract class ApiException extends RuntimeException {

    protected ApiException(String message) {
        super(message);
    }
}
