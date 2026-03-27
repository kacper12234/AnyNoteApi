package com.betacom.anynoteapi.exceptions;

public class WrongVersionException extends RuntimeException {
    public WrongVersionException(Integer version) {
        super("Incorrect version, actual is " + version);
    }
}
