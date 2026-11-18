package com.example.covoiturage.exception;

public class ActionNonAutoriseeException extends ForbiddenException {

    public ActionNonAutoriseeException(String message) {
        super(message);
    }
}
