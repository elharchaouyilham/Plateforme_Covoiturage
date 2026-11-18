package com.example.covoiturage.exception;

public class PlacesInsuffisantesException extends ConflictException {

    public PlacesInsuffisantesException(int demandees, int restantes) {
        super("Places insuffisantes : " + demandees + " demandée(s), " + restantes + " restante(s)");
    }
}
